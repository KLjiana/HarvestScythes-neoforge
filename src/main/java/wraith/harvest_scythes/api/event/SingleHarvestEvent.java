package wraith.harvest_scythes.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record SingleHarvestEvent(Level world, Player user, ItemStack stack,
                                 BlockState blockState, BlockPos cropPos, int currentBlockAmount, int totalBlocks,
                                 boolean gotDamaged) {

}
