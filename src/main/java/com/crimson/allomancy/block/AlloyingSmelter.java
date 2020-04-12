package com.crimson.allomancy.block;

import javax.annotation.Nullable;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.ModTileEntityTypes;
import com.crimson.allomancy.tileentity.AlloyingSmelterTileEntity;
import com.crimson.allomancy.tileentity.MetalPurifierTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class AlloyingSmelter extends Block {
	private AlloyingSmelterTileEntity alloySmelter;
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	public AlloyingSmelter() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F, 3.0F));
		this.setRegistryName(Allomancy.MODID, "alloying_smelter");
		
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
		
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
	      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		alloySmelter.setWorld(worldIn);
		alloySmelter.setPos(pos);

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
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	      builder.add(FACING);
	}
	
	@Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        return state.with(FACING, direction.rotate(state.get(FACING)));
    }
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
	      return BlockRenderType.MODEL;
	}
}
