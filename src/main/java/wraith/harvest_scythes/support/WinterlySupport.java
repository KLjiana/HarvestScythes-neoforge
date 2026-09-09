package wraith.harvest_scythes.support;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import wraith.harvest_scythes.item.MacheteItem;
import wraith.harvest_scythes.item.ScytheItem;
import wraith.harvest_scythes.registry.ItemRegistry;

public class WinterlySupport {

    private WinterlySupport() {}

    public static void loadItems() {
        ItemRegistry.registerItem("cryomarble_scythe", () -> new ScytheItem(Tiers.DIAMOND, new Item.Properties()));

        ItemRegistry.registerItem("cryomarble_machete", () -> new MacheteItem(Tiers.DIAMOND, new Item.Properties()));
    }

}
