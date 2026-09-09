package wraith.harvest_scythes.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import wraith.harvest_scythes.HarvestScythes;

import java.util.Calendar;
import java.util.Random;

public final class HSUtils {

    private HSUtils() {}

    private static final Random RANDOM = new Random(Calendar.getInstance().getTimeInMillis());

    public static int getRandomIntInRange(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static ResourceLocation ID(String path) {
        return ResourceLocation.fromNamespaceAndPath(HarvestScythes.MOD_ID, path);
    }

    public static int getTierLevel(Tier tier) {
        if (tier == Tiers.NETHERITE) {
            return 4;
        }
        if (tier == Tiers.DIAMOND) {
            return 3;
        }
        if (tier == Tiers.IRON) {
            return 2;
        }
        if (tier == Tiers.STONE) {
            return 1;
        }
        if (tier == Tiers.WOOD || tier == Tiers.GOLD) {
            return 0;
        }
        int uses = tier.getUses();
        if (uses >= 2000) {
            return 4;
        }
        if (uses >= 1500) {
            return 3;
        }
        if (uses >= 500) {
            return 2;
        }
        return uses >= 100 ? 1 : 0;
    }

}
