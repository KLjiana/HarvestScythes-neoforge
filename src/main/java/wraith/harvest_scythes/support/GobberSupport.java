package wraith.harvest_scythes.support;

import com.kwpugh.gobber2.lists.tiers.ToolMaterialTiers;
import net.minecraft.world.item.Item;
import wraith.harvest_scythes.item.MacheteItem;
import wraith.harvest_scythes.item.ScytheItem;
import wraith.harvest_scythes.registry.ItemRegistry;

public final class GobberSupport {

    private GobberSupport() {}

    public static void loadItems() {
        ItemRegistry.registerItem("gobber2_scythe", () -> new ScytheItem(ToolMaterialTiers.OVERWORLD_GOBBER, new Item.Properties()));
        ItemRegistry.registerItem("gobber2_nether_scythe", () -> new ScytheItem(ToolMaterialTiers.NETHER_GOBBER, new Item.Properties()));
        ItemRegistry.registerItem("gobber2_end_scythe", () -> new ScytheItem(ToolMaterialTiers.END_GOBBER, new Item.Properties()));

        ItemRegistry.registerItem("gobber2_machete", () -> new MacheteItem(ToolMaterialTiers.OVERWORLD_GOBBER, new Item.Properties()));
        ItemRegistry.registerItem("gobber2_nether_machete", () -> new MacheteItem(ToolMaterialTiers.NETHER_GOBBER, new Item.Properties()));
        ItemRegistry.registerItem("gobber2_end_machete", () -> new MacheteItem(ToolMaterialTiers.END_GOBBER, new Item.Properties()));
    }

}
