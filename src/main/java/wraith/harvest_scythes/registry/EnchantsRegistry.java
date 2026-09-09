package wraith.harvest_scythes.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import wraith.harvest_scythes.util.HSUtils;

public final class EnchantsRegistry {

    public static final ResourceKey<Enchantment> CROP_REAPER = create("crop_reaper");
    public static final ResourceKey<Enchantment> LEAF_EATER = create("leaf_eater");
    public static final ResourceKey<Enchantment> BLIND_HARVEST_CURSE = create("blind_harvest_curse");

    private EnchantsRegistry() {}

    private static ResourceKey<Enchantment> create(String id) {
        return ResourceKey.create(Registries.ENCHANTMENT, HSUtils.ID(id));
    }

    public static int getLevel(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        for (var entry : stack.getEnchantments().entrySet()) {
            if (entry.getKey().is(enchantment)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

}
