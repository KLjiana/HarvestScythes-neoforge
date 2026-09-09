package wraith.harvest_scythes.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record HarvestEvent(Level world, Player user,
                           ItemStack stack, int totalBlocksHarvested, int totalToolDamage) {
}
