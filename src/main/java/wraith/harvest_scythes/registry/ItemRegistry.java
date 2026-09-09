package wraith.harvest_scythes.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import wraith.harvest_scythes.HarvestScythes;
import wraith.harvest_scythes.item.MacheteItem;
import wraith.harvest_scythes.item.ScytheItem;

import java.util.HashMap;
import java.util.function.Function;

public final class ItemRegistry {

    private static final DeferredRegister.Items ITEM_DEFERRED = DeferredRegister.createItems(HarvestScythes.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TAB_DEFERRED = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HarvestScythes.MOD_ID);
    private static final HashMap<String, DeferredItem<Item>> ITEMS = new HashMap<>();

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCYTHES = TAB_DEFERRED.register("scythes", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(get("diamond_scythe")))
            .title(Component.translatable("itemGroup.harvest_scythes.scythes"))
            .displayItems((displayContext, entries) -> ITEMS.values().stream().map(DeferredItem::get).filter(entry -> entry instanceof ScytheItem).map(ItemStack::new).forEach(entries::accept))
            .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MACHETES = TAB_DEFERRED.register("machetes", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(get("diamond_machete")))
            .title(Component.translatable("itemGroup.harvest_scythes.machetes"))
            .displayItems((displayContext, entries) -> ITEMS.values().stream().map(DeferredItem::get).filter(entry -> entry instanceof MacheteItem).map(ItemStack::new).forEach(entries::accept))
            .build());

    private ItemRegistry() {}

    public static Item get(String id) {
        DeferredItem<Item> item = ITEMS.get(id);
        return item != null && item.isBound() ? item.get() : Items.AIR;
    }

    public static void register(IEventBus eventBus) {
        ITEM_DEFERRED.register(eventBus);
        TAB_DEFERRED.register(eventBus);
    }

    public static void init() {
        if (!ITEMS.isEmpty()) {
            return;
        }
        registerItem("wooden_scythe", properties -> new ScytheItem(Tiers.WOOD, properties));
        registerItem("stone_scythe", properties -> new ScytheItem(Tiers.STONE, properties));
        registerItem("iron_scythe", properties -> new ScytheItem(Tiers.IRON, properties));
        registerItem("golden_scythe", properties -> new ScytheItem(Tiers.GOLD, 3, properties));
        registerItem("diamond_scythe", properties -> new ScytheItem(Tiers.DIAMOND, properties));
        registerItem("netherite_scythe", properties -> new ScytheItem(Tiers.NETHERITE, properties.fireResistant()));
        registerItem("creative_scythe", properties -> new ScytheItem(Tiers.NETHERITE, 20, properties.fireResistant().stacksTo(1)));

        registerItem("wooden_machete", properties -> new MacheteItem(Tiers.WOOD, properties));
        registerItem("stone_machete", properties -> new MacheteItem(Tiers.STONE, properties));
        registerItem("iron_machete", properties -> new MacheteItem(Tiers.IRON, properties));
        registerItem("golden_machete", properties -> new MacheteItem(Tiers.GOLD, 100, properties));
        registerItem("diamond_machete", properties -> new MacheteItem(Tiers.DIAMOND, properties));
        registerItem("netherite_machete", properties -> new MacheteItem(Tiers.NETHERITE, properties.fireResistant()));
        registerItem("creative_machete", properties -> new MacheteItem(Tiers.NETHERITE, 240, properties.fireResistant().stacksTo(1)));
    }

    public static void registerItem(String id, Function<Item.Properties, Item> item) {
        if (ITEMS.containsKey(id)) {
            return;
        }
        ITEMS.put(id, ITEM_DEFERRED.registerItem(id, item));
    }

    public static int count() {
        return ITEMS.size();
    }

}
