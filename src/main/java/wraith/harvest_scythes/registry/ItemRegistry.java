package wraith.harvest_scythes.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wraith.harvest_scythes.HarvestScythes;
import wraith.harvest_scythes.item.MacheteItem;
import wraith.harvest_scythes.item.ScytheItem;

import java.util.HashMap;
import java.util.function.Supplier;

public final class ItemRegistry {

    private static final DeferredRegister<Item> ITEM_DEFERRED = DeferredRegister.create(ForgeRegistries.ITEMS, HarvestScythes.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TAB_DEFERRED = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HarvestScythes.MOD_ID);
    private static final HashMap<String, RegistryObject<Item>> ITEMS = new HashMap<>();

    public static final RegistryObject<CreativeModeTab> SCYTHES = TAB_DEFERRED.register("scythes", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(get("diamond_scythe")))
            .title(Component.translatable("itemGroup.harvest_scythes.scythes"))
            .displayItems((displayContext, entries) -> ITEMS.values().stream().map(RegistryObject::get).filter(entry -> entry instanceof ScytheItem).map(ItemStack::new).forEach(entries::accept))
            .build());
    public static final RegistryObject<CreativeModeTab> MACHETES = TAB_DEFERRED.register("machetes", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(get("diamond_machete")))
            .title(Component.translatable("itemGroup.harvest_scythes.machetes"))
            .displayItems((displayContext, entries) -> ITEMS.values().stream().map(RegistryObject::get).filter(entry -> entry instanceof MacheteItem).map(ItemStack::new).forEach(entries::accept))
            .build());

    private ItemRegistry() {}

    public static Item get(String id) {
        RegistryObject<Item> item = ITEMS.get(id);
        return item != null && item.isPresent() ? item.get() : Items.AIR;
    }

    public static void register(IEventBus eventBus) {
        ITEM_DEFERRED.register(eventBus);
        TAB_DEFERRED.register(eventBus);
    }

    public static void init() {
        if (!ITEMS.isEmpty()) {
            return;
        }
        registerItem("wooden_scythe", () -> new ScytheItem(Tiers.WOOD, new Item.Properties()));
        registerItem("stone_scythe", () -> new ScytheItem(Tiers.STONE, new Item.Properties()));
        registerItem("iron_scythe", () -> new ScytheItem(Tiers.IRON, new Item.Properties()));
        registerItem("golden_scythe", () -> new ScytheItem(Tiers.GOLD, 3, new Item.Properties()));
        registerItem("diamond_scythe", () -> new ScytheItem(Tiers.DIAMOND, new Item.Properties()));
        registerItem("netherite_scythe", () -> new ScytheItem(Tiers.NETHERITE, new Item.Properties().fireResistant()));
        registerItem("creative_scythe", () -> new ScytheItem(Tiers.NETHERITE, 20, new Item.Properties().fireResistant().stacksTo(1)));

        registerItem("wooden_machete", () -> new MacheteItem(Tiers.WOOD, new Item.Properties()));
        registerItem("stone_machete", () -> new MacheteItem(Tiers.STONE, new Item.Properties()));
        registerItem("iron_machete", () -> new MacheteItem(Tiers.IRON, new Item.Properties()));
        registerItem("golden_machete", () -> new MacheteItem(Tiers.GOLD, 100, new Item.Properties()));
        registerItem("diamond_machete", () -> new MacheteItem(Tiers.DIAMOND, new Item.Properties()));
        registerItem("netherite_machete", () -> new MacheteItem(Tiers.NETHERITE, new Item.Properties().fireResistant()));
        registerItem("creative_machete", () -> new MacheteItem(Tiers.NETHERITE, 240, new Item.Properties().fireResistant().stacksTo(1)));
    }

    public static void registerItem(String id, Supplier<Item> item) {
        if (ITEMS.containsKey(id)) {
            return;
        }
        ITEMS.put(id, ITEM_DEFERRED.register(id, item));
    }

    public static int count() {
        return ITEMS.size();
    }

}
