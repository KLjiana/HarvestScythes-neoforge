package wraith.harvest_scythes.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wraith.harvest_scythes.api.event.HarvestEvent;
import wraith.harvest_scythes.api.event.SingleHarvestEvent;
import wraith.harvest_scythes.api.machete.HSMacheteEvents;
import wraith.harvest_scythes.registry.EnchantsRegistry;
import wraith.harvest_scythes.registry.ItemRegistry;
import wraith.harvest_scythes.util.Config;
import wraith.harvest_scythes.util.HSUtils;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class MacheteItem extends SwordItem {

    private static final Set<Level> HARVESTING = Collections.newSetFromMap(new IdentityHashMap<>());

    protected int harvestDepth;

    public MacheteItem(Tier material, int attackDamage, float attackSpeed, int harvestDepth, Item.Properties settings) {
        super(material, attackDamage, attackSpeed, settings);
        this.harvestDepth = harvestDepth;
    }

    public MacheteItem(Tier material, int attackDamage, float attackSpeed, Item.Properties settings) {
        this(material, attackDamage, attackSpeed, getDepthFromMaterial(material), settings);
    }

    public MacheteItem(Tier material, Item.Properties settings) {
        this(material, 2, -2.0F, settings);
    }

    public MacheteItem(Tier material, int harvestDepth, Item.Properties settings) {
        this(material, 2, -2.0F, harvestDepth, settings);
    }

    private static int getDepthFromMaterial(Tier material) {
        return (Math.min(10, material.getLevel() + 1)) * 18;
    }

    public static int getHarvestDepth(ItemStack stack) {
        if (!(stack.getItem() instanceof MacheteItem machete)) {
            return 0;
        }
        var enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantsRegistry.get("leaf_eater"), stack);
        return Mth.clamp(machete.getRegularHarvestDepth() + Mth.clamp(enchantLevel * 18, 0, 240), 0, 240);
    }

    public static boolean tryHarvest(Level world, Player player, BlockPos pos, BlockState blockState) {
        if (world.isClientSide) {
            return false;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof MacheteItem machete)) {
            return false;
        }
        if (!HARVESTING.add(world)) {
            return false;
        }
        try {
            int blocksHarvested = 0;
            int damage = 0;
            boolean isCreative = machete == ItemRegistry.get("creative_machete");
            Queue<BlockPos> positions = new LinkedList<>();
            positions.add(pos);
            while (!positions.isEmpty() && blocksHarvested <= MacheteItem.getHarvestDepth(stack)) {
                var curPos = positions.remove();
                var curState = world.getBlockState(curPos);
                var block = curState.getBlock();
                if ((!(block instanceof LeavesBlock) && blocksHarvested > 0) || !Config.getInstance().canMacheteHarvest(BuiltInRegistries.BLOCK.getKey(block))) {
                    continue;
                }
                world.destroyBlock(curPos, true);
                ++blocksHarvested;
                for (int x = -1; x <= 1; ++x) {
                    for (int y = -1; y <= 1; ++y) {
                        for (int z = -1; z <= 1; ++z) {
                            if (x == 0 && y == 0 && z == 0) {
                                continue;
                            }
                            positions.add(curPos.offset(x, y, z));
                        }
                    }
                }
                var takeDamage = HSUtils.getRandomIntInRange(0, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack)) == 0;
                HSMacheteEvents.onSingleHarvest(new SingleHarvestEvent(world, player, stack, curState, curPos, 1, blocksHarvested, takeDamage));
                if (!isCreative && takeDamage) {
                    ++damage;
                    stack.hurt(1, player.getRandom(), player instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                }
            }
            HSMacheteEvents.onHarvest(new HarvestEvent(world, player, stack, blocksHarvested, damage));
            return blocksHarvested > 0;
        } finally {
            HARVESTING.remove(world);
        }
    }

    public int getRegularHarvestDepth() {
        return Mth.clamp(this.harvestDepth, 0, 240);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return super.isCorrectToolForDrops(state) || state.getBlock() instanceof LeavesBlock;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return super.canAttackBlock(state, world, pos, miner) || state.getBlock() instanceof LeavesBlock;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(Component.translatable("harvest_scythes.machete_tooltip.depth", Component.translatable("harvest_scythes.machete_tooltip.depth.arg_color").append(String.valueOf(getHarvestDepth(stack)))));
    }

}
