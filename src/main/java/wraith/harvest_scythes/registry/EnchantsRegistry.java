package wraith.harvest_scythes.registry;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wraith.harvest_scythes.HarvestScythes;
import wraith.harvest_scythes.enchantment.BlindHarvestCurseEnchantment;
import wraith.harvest_scythes.enchantment.CropReaperEnchantment;
import wraith.harvest_scythes.enchantment.LeafEaterEnchantment;

import java.util.HashMap;
import java.util.function.Supplier;

public final class EnchantsRegistry {

    private static final DeferredRegister<Enchantment> ENCHANTMENT_DEFERRED = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, HarvestScythes.MOD_ID);
    public static final HashMap<String, RegistryObject<Enchantment>> ENCHANTMENTS = new HashMap<>();

    private EnchantsRegistry() {}

    public static void register(IEventBus eventBus) {
        ENCHANTMENT_DEFERRED.register(eventBus);
    }

    public static void registerEnchantments() {
        if (!ENCHANTMENTS.isEmpty()) {
            return;
        }
        registerEnchantment("crop_reaper", CropReaperEnchantment::new);
        registerEnchantment("leaf_eater", LeafEaterEnchantment::new);
        registerEnchantment("blind_harvest_curse", BlindHarvestCurseEnchantment::new);
    }

    private static void registerEnchantment(String id, Supplier<Enchantment> enchantment) {
        ENCHANTMENTS.put(id, ENCHANTMENT_DEFERRED.register(id, enchantment));
    }

    public static Enchantment get(String id) {
        RegistryObject<Enchantment> enchantment = ENCHANTMENTS.get(id);
        return enchantment != null && enchantment.isPresent() ? enchantment.get() : null;
    }

}
