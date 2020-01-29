package com.crimson.allomancy.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.crimson.allomancy.ModTileEntityTypes;
import com.crimson.allomancy.entity.particle.SoundParticle;
import com.crimson.allomancy.util.Registry;

import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AlloyingSmelterTileEntity extends TileEntity implements ITickableTileEntity {
	private IBlockReader reader;
	private int creationTime = 300;
	private Item firstMetal = null;
	private Item secondMetal = null;
	private boolean processing = false;
	private ItemStack result = null;
	private double itemX, itemY, itemZ;
	
	public AlloyingSmelterTileEntity() {
		super(ModTileEntityTypes.ALLOYING_SMELTER);
	}
	
	public AlloyingSmelterTileEntity(BlockPos pos, World world) {
		super(ModTileEntityTypes.ALLOYING_SMELTER);
		this.world = world;
		this.pos = pos;
		//resetTimer();
	}
	
	public AlloyingSmelterTileEntity(IBlockReader world) {
		super(ModTileEntityTypes.ALLOYING_SMELTER);
		this.reader = world;
	}
	
	private void resetProcess() {
		creationTime = 300;
		firstMetal = null;
		secondMetal = null;
		processing = false;
	}

	@Override
	public void tick() {
		if(processing) {
			creationTime = creationTime - 1;
			if (world.rand.nextInt(100) == 0) {
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), new SoundEvent(new ResourceLocation("item.flintandsteel.use")), SoundCategory.BLOCKS, 1, 5, false);
			}
			if (world.rand.nextInt(20) == 0) {
                world.addParticle(ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
                world.addParticle(ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
                world.addParticle(ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
                world.addParticle(ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
			}
			
			if(creationTime == 0) {
				ItemEntity itemResult = new ItemEntity(world, itemX, itemY, itemZ, result);
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), new SoundEvent(new ResourceLocation("block.fire.extinguish")), SoundCategory.BLOCKS, 1, 5, false);
				world.addEntity(itemResult);
        		
				resetProcess();
			}
			
		} else {
			BlockPos negative = new BlockPos(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2);
	        BlockPos positive = new BlockPos(pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2);
	        
	        List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(negative, positive));
	        for(ItemEntity item : entities) {
	        	ItemStack stack = item.getItem();
	        	Item realItem = stack.getItem();
	        		if(firstMetal == null) {	
			        	if(realItem.equals(Items.IRON_INGOT) || realItem.equals(Registry.tin_ingot) || realItem.equals(Registry.zinc_ingot) || realItem.equals(Registry.copper_ingot))
			        	{
			        		firstMetal = realItem;
			        		stack.shrink(1);
			        		
			        	}
	        		} else {
	        			if(firstMetal.equals(Items.IRON_INGOT)) {
	        				if(realItem.equals(Items.COAL))
	        				{
	        					stack.shrink(1);
	        					processing = true;
	        					result = new ItemStack(Registry.steel_ingot);
	        					itemX = item.posX;
	        					itemY = item.posY;
	        					itemZ = item.posZ;
	        				}
	        			}
	        			if(firstMetal.equals(Registry.tin_ingot)) {
	        				if(realItem.equals(Registry.copper_ingot))
	        				{
	        					stack.shrink(1);
	        					processing = true;
	        					result = new ItemStack(Registry.bronze_ingot);
	        					itemX = item.posX;
	        					itemY = item.posY;
	        					itemZ = item.posZ;
	        				}
	        			}
	        			if(firstMetal.equals(Registry.copper_ingot)) {
	        				if(realItem.equals(Registry.tin_ingot))
	        				{
	        					stack.shrink(1);
	        					processing = true;
	        					result = new ItemStack(Registry.bronze_ingot);
	        					itemX = item.posX;
	        					itemY = item.posY;
	        					itemZ = item.posZ;
	        				}
	        			}
	        			if(firstMetal.equals(Registry.copper_ingot)) {
	        				if(realItem.equals(Registry.zinc_ingot))
	        				{
	        					stack.shrink(1);
	        					processing = true;
	        					result = new ItemStack(Registry.brass_ingot);
	        					itemX = item.posX;
	        					itemY = item.posY;
	        					itemZ = item.posZ;
	        				}
	        			}
	        			if(firstMetal.equals(Registry.zinc_ingot)) {
	        				if(realItem.equals(Registry.copper_ingot))
	        				{
	        					stack.shrink(1);
	        					processing = true;
	        					result = new ItemStack(Registry.brass_ingot);
	        					itemX = item.posX;
	        					itemY = item.posY;
	        					itemZ = item.posZ;
	        				}
	        			}
	        			if(firstMetal.equals(Registry.tin_ingot)) {
	        				if(realItem.equals(Registry.lead_ingot))
	        				{
	        					stack.shrink(1);
	        					processing = true;
	        					result = new ItemStack(Registry.pewter_ingot);
	        					itemX = item.posX;
	        					itemY = item.posY;
	        					itemZ = item.posZ;
	        				}
	        			}
	        			if(firstMetal.equals(Registry.lead_ingot)) {
	        				if(realItem.equals(Registry.tin_ingot))
	        				{
	        					stack.shrink(1);
	        					processing = true;
	        					result = new ItemStack(Registry.pewter_ingot);
	        					itemX = item.posX;
	        					itemY = item.posY;
	        					itemZ = item.posZ;
	        				}
	        			}

	        		}
	        }
		}
		
	}
}
