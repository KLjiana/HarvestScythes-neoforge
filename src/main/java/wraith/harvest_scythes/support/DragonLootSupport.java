package wraith.harvest_scythes.support;

import net.mcreator.enderdragonloot.init.EnderDragonLootModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import wraith.harvest_scythes.item.MacheteItem;
import wraith.harvest_scythes.item.ScytheItem;
import wraith.harvest_scythes.registry.ItemRegistry;

public final class DragonLootSupport {

    private static final Tier DRAGON_TIER = new Tier() {
        @Override
        public int getUses() {
            return 2785;
        }

        @Override
        public float getSpeed() {
            return 11.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 20;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return EnderDragonLootModItems.DRAGON_SCALE.isBound() ? Ingredient.of(EnderDragonLootModItems.DRAGON_SCALE.get()) : Ingredient.EMPTY;
        }
    };

    private DragonLootSupport() {}

    public static void loadItems() {
        ItemRegistry.registerItem("dragon_scythe", properties -> new ScytheItem(DRAGON_TIER, properties));

        ItemRegistry.registerItem("dragon_machete", properties -> new MacheteItem(DRAGON_TIER, properties));
    }

}
