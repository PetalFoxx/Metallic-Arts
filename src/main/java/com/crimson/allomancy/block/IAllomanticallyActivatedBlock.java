package com.crimson.allomancy.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This interface can be used to signify if a block should react to being pushed
 * or pulled, rather than moving the pusher
 *
 * @author legobmw99
 * @see IronLeverBlock
 */
public interface IAllomanticallyActivatedBlock {

    /**
     * Called when the block is steelpushed or ironpulled
     *
     * @param isPush whether or not the activation is Steel
     * @return whether or not the block was activated
     */
    boolean onBlockActivatedAllomantically(BlockState state, World world, BlockPos pos, PlayerEntity playerIn, boolean isPush);
}
