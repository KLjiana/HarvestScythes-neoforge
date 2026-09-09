package wraith.harvest_scythes.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wraith.harvest_scythes.api.event.HarvestEvent;
import wraith.harvest_scythes.api.event.SingleHarvestEvent;
import wraith.harvest_scythes.api.scythe.HSScythesEvents;
import wraith.harvest_scythes.registry.EnchantsRegistry;
import wraith.harvest_scythes.util.Config;
import wraith.harvest_scythes.util.HSUtils;

import java.util.List;

public class ScytheItem extends HoeItem {

    protected int harvestRadius;

    public ScytheItem(Tier material, int attackDamage, float attackSpeed, Item.Properties settings) {
        this(material, attackDamage, attackSpeed, getLowerRadius(material.getLevel()), settings);
    }

    public ScytheItem(Tier material, int attackDamage, float attackSpeed, int harvestRadius, Item.Properties settings) {
        super(material, attackDamage, attackSpeed, settings);
        this.harvestRadius = harvestRadius;
    }

    public ScytheItem(Tier material, Item.Properties settings) {
        this(material, 5, -3.3F, settings);
    }

    public ScytheItem(Tier material, int harvestRadius, Item.Properties settings) {
        this(material, 5, -3.3F, harvestRadius, settings);
    }

    public static int getLowerRadius(int miningLevel) {
        return (int) (Math.floor(miningLevel / 2.0) + 1);
    }

    public static int getUpperRadius(int miningLevel) {
        return (int) (Math.ceil(miningLevel / 2.0) + 1);
    }

    protected static boolean shouldBeCircle(int miningLevel) {
        return getLowerRadius(miningLevel) == getUpperRadius(miningLevel);
    }

    public static InteractionResultHolder<ItemStack> harvest(int harvestRadius, int miningLevel, Level world, Player user, InteractionHand hand) {
        var blockPos = user.blockPosition();
        var stack = user.getItemInHand(hand);
        var item = stack.getItem();

        int lvl = EnchantmentHelper.getItemEnchantmentLevel(EnchantsRegistry.get("crop_reaper"), stack);
        boolean prematureHarvest = EnchantmentHelper.getItemEnchantmentLevel(EnchantsRegistry.get("blind_harvest_curse"), stack) > 0;
        int radius = (int) (Math.floor(lvl / 2.0) + harvestRadius);
        boolean circleHarvest = shouldBeCircle(miningLevel + lvl);

        int totalBlocks = 0;
        int totalDamage = 0;

        for (int x = -radius; x <= radius; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockPos cropPos = new BlockPos(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);
                    if (circleHarvest &&
                        ((y == -1 && cropPos.distManhattan(blockPos.below()) > radius) ||
                            (y == 0 && cropPos.distManhattan(blockPos) > radius) ||
                            (y == 1 && cropPos.distManhattan(blockPos.above()) > radius))) {
                        continue;
                    }
                    BlockState blockState = world.getBlockState(cropPos);
                    Block block = blockState.getBlock();
                    int damageTool = 0;
                    boolean canHarvest = Config.getInstance().canScytheHarvest(BuiltInRegistries.BLOCK.getKey(block));
                    if (block instanceof CropBlock cropBlock && (prematureHarvest || cropBlock.isMaxAge(blockState)) && canHarvest) {
                        if (prematureHarvest) {
                            world.destroyBlock(cropPos, true, user);
                        } else {
                            BlockEntity blockEntity = world.getBlockEntity(blockPos);
                            var drops = Block.getDrops(blockState, (ServerLevel) world, blockPos, blockEntity, user, stack);
                            boolean removedExtraSeed = false;
                            for (var drop : drops) {
                                Item dropItem = drop.getItem();
                                if (!removedExtraSeed && dropItem instanceof BlockItem dropBlock && dropBlock.getBlock() == cropBlock) {
                                    removedExtraSeed = true;
                                    drop.shrink(1);
                                }
                                Block.popResource(world, cropPos, drop);
                            }
                            world.setBlockAndUpdate(cropPos, blockState.setValue(CropBlock.AGE, 0));
                        }
                        damageTool = 1;
                    } else if (block instanceof BushBlock && !(block instanceof CropBlock) && canHarvest) {
                        world.destroyBlock(cropPos, true, user);
                        damageTool = 1;
                    }
                    if (damageTool > 0) {
                        totalBlocks += damageTool;
                        var takeDamage = HSUtils.getRandomIntInRange(0, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack)) == 0;
                        HSScythesEvents.onSingleHarvest(new SingleHarvestEvent(world, user, stack, blockState, cropPos, damageTool, totalBlocks, takeDamage));
                        if (!takeDamage) {
                            continue;
                        }
                        totalDamage += damageTool;
                        if (user instanceof ServerPlayer serverPlayer) {
                            stack.hurt(damageTool, serverPlayer.getRandom(), serverPlayer);
                        }
                        if (stack.getItem() != item) {
                            HSScythesEvents.onHarvest(new HarvestEvent(world, user, stack, totalBlocks, totalDamage));
                            return InteractionResultHolder.success(stack);
                        }
                    }
                }
            }
        }
        HSScythesEvents.onHarvest(new HarvestEvent(world, user, stack, totalBlocks, totalDamage));
        return totalBlocks > 0 ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return world.isClientSide ? InteractionResultHolder.fail(user.getItemInHand(hand)) : harvest(this.harvestRadius, this.getTier().getLevel(), world, user, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        int lvl = EnchantmentHelper.getItemEnchantmentLevel(EnchantsRegistry.get("crop_reaper"), stack);
        int radius = (int) (Math.floor(lvl / 2.0) + harvestRadius);
        boolean circleHarvest = shouldBeCircle(this.getTier().getLevel() + lvl);
        tooltip.add(Component.translatable("harvest_scythes.scythe_tooltip.radius", Component.translatable("harvest_scythes.scythe_tooltip.radius.arg_color").append(String.valueOf(radius))));
        tooltip.add(Component.translatable("harvest_scythes.scythe_tooltip.circle", Component.translatable("harvest_scythes.scythe_tooltip.circle.arg_color").append(String.valueOf(circleHarvest))));
    }

    public int getHarvestRadius() {
        return this.harvestRadius;
    }

    public boolean hasCircleHarvset() {
        return shouldBeCircle(this.getTier().getLevel());
    }

}
