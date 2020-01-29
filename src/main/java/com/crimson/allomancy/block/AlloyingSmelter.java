package com.crimson.allomancy.block;

import javax.annotation.Nullable;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.ModTileEntityTypes;
import com.crimson.allomancy.tileentity.AlloyingSmelterTileEntity;
import com.crimson.allomancy.tileentity.MetalPurifierTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AlloyingSmelter extends Block {
	private AlloyingSmelterTileEntity alloySmelter;
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	public AlloyingSmelter() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F, 3.0F));
		this.setRegistryName(Allomancy.MODID, "alloying_smelter");
		
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		alloySmelter.setWorld(worldIn);
		alloySmelter.setPos(pos);
		
		
		Direction entityFacing = placer.getHorizontalFacing();

        if(!worldIn.isRemote) {
            if(entityFacing == Direction.NORTH) {
                entityFacing = Direction.SOUTH;
            } else if(entityFacing == Direction.EAST) {
                entityFacing = Direction.WEST;
            } else if(entityFacing == Direction.SOUTH) {
                entityFacing = Direction.NORTH;
            } else if(entityFacing == Direction.WEST) {
                entityFacing = Direction.EAST;
            }
            

            worldIn.setBlockState(pos, state.with(FACING, entityFacing), 2);
        }
		
	   }

	
	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		// Always use TileEntityType#create to allow registry overrides to work.
		alloySmelter = (AlloyingSmelterTileEntity) ModTileEntityTypes.ALLOYING_SMELTER.create();
		return alloySmelter;
	}
}
