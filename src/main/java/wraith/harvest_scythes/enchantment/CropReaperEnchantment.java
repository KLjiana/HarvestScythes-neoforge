package wraith.harvest_scythes.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import wraith.harvest_scythes.item.ScytheItem;

public class CropReaperEnchantment extends Enchantment {

    public CropReaperEnchantment() {
        super(Enchantment.Rarity.COMMON, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ScytheItem;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

}
