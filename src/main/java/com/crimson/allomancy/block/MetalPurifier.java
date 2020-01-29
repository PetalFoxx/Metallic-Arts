package com.crimson.allomancy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.ModTileEntityTypes;
import com.crimson.allomancy.tileentity.MetalPurifierTileEntity;
import com.crimson.allomancy.util.Registry;

public class MetalPurifier extends Block {

	private MetalPurifierTileEntity purifier;
	
	public MetalPurifier() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F, 3.0F));
		this.setRegistryName(Allomancy.MODID, "metal_purifier");
		
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		purifier.setWorld(worldIn);
		purifier.setPos(pos);
	   }

	
	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		// Always use TileEntityType#create to allow registry overrides to work.
		purifier = (MetalPurifierTileEntity) ModTileEntityTypes.METAL_PURIFIER.create();
		return purifier;
	}


}
