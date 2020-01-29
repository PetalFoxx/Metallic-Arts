package com.crimson.allomancy.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.crimson.allomancy.ModTileEntityTypes;
import com.crimson.allomancy.util.Registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class MetalPurifierTileEntity extends TileEntity implements ITickableTileEntity {
	private IBlockReader reader;
	private int creationTime = 300;
	private List<Item> metals = new ArrayList<Item>();
	private boolean processing = false;
	private ItemStack result = null;
	private double itemX, itemY, itemZ;
	
	public MetalPurifierTileEntity() {
		super(ModTileEntityTypes.METAL_PURIFIER);
	}
	
	public MetalPurifierTileEntity(BlockPos pos, World world) {
		super(ModTileEntityTypes.METAL_PURIFIER);
		this.world = world;
		this.pos = pos;
		//resetTimer();
	}
	
	public MetalPurifierTileEntity(IBlockReader world) {
		super(ModTileEntityTypes.METAL_PURIFIER);
		this.reader = world;
	}
	
	
	public void resetTimer() {
		creationTime = 300;
		metals = new ArrayList<Item>();
		processing = false;
	}
	

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		if(processing) {
			creationTime = creationTime - 1;
			if (world.rand.nextInt(100) == 0) {
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), new SoundEvent(new ResourceLocation("entity.endermen.teleport")), SoundCategory.BLOCKS, 1, 5, false);
			}
			if (world.rand.nextInt(25) == 0) {
                world.addParticle(ParticleTypes.ENCHANT, pos.getX(), pos.getY(), pos.getZ(), Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
                world.addParticle(ParticleTypes.ENCHANT, pos.getX(), pos.getY(), pos.getZ(), Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
                world.addParticle(ParticleTypes.ENCHANT, pos.getX(), pos.getY(), pos.getZ(), Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
                world.addParticle(ParticleTypes.ENCHANT, pos.getX(), pos.getY(), pos.getZ(), Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
			}
			if(creationTime == 0) {
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), new SoundEvent(new ResourceLocation("entity.firework.blast")), SoundCategory.BLOCKS, 1, 5, false);
				ItemEntity itemResult = new ItemEntity(world, itemX, itemY, itemZ, result);
				world.addEntity(itemResult);
        		
				resetTimer();
			}
			
		} else {
			BlockPos negative = new BlockPos(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2);
	        BlockPos positive = new BlockPos(pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2);
	        
	        List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(negative, positive));
	        for(ItemEntity item : entities) {
	        	ItemStack stack = item.getItem();
	        	Item realItem = stack.getItem();
	        			
		        	if(realItem.equals(Items.IRON_INGOT) && !metals.contains(stack.getItem()))
		        	{
		        		metals.add(realItem);
		        		stack.shrink(1);
		        		
		        	}
		        	
		        	if(realItem.equals(Registry.steel_ingot) && !metals.contains(stack.getItem()))
		        	{
		        		metals.add(realItem);
		        		stack.shrink(1);
		        	}
		        	
		        	if(realItem.equals(Registry.tin_ingot) && !metals.contains(stack.getItem()))
		        	{
		        		metals.add(realItem);
		        		stack.shrink(1);
		        	}
		        	
		        	if(realItem.equals(Registry.pewter_ingot) && !metals.contains(stack.getItem()))
		        	{
		        		metals.add(realItem);
		        		stack.shrink(1);
		        	}
		        	
		        	if(realItem.equals(Registry.zinc_ingot) && !metals.contains(stack.getItem()))
		        	{
		        		metals.add(realItem);
		        		stack.shrink(1);
		        	}
		        	
		        	if(realItem.equals(Registry.brass_ingot) && !metals.contains(stack.getItem()))
		        	{
		        		metals.add(realItem);
		        		stack.shrink(1);
		        	}
		        	
		        	if(realItem.equals(Registry.copper_ingot) && !metals.contains(stack.getItem()))
		        	{
		        		metals.add(realItem);
		        		stack.shrink(1);
		        	}
		        	
		        	if(realItem.equals(Registry.bronze_ingot) && !metals.contains(stack.getItem()))
		        	{
		        		metals.add(realItem);
		        		stack.shrink(1);
		        	}
		        	
		        	
		        	if(stack.getItem().equals(Items.GLASS_BOTTLE)) {
		        		stack.shrink(1);
		        		
		        		ItemStack vial = new ItemStack(Registry.vial, 1);
		        		CompoundNBT nbt = new CompoundNBT();
		        		for(int i = 0; i < metals.size(); i++) {
		        			if(metals.get(i).equals(Items.IRON_INGOT))
		        				nbt.putBoolean(Registry.allomanctic_metals[0], true);
		        			if(metals.get(i).equals(Registry.steel_ingot))
		        				nbt.putBoolean(Registry.allomanctic_metals[1], true);
		        			if(metals.get(i).equals(Registry.tin_ingot))
		        				nbt.putBoolean(Registry.allomanctic_metals[2], true);
		        			if(metals.get(i).equals(Registry.pewter_ingot))
		        				nbt.putBoolean(Registry.allomanctic_metals[3], true);
		        			if(metals.get(i).equals(Registry.zinc_ingot))
		        				nbt.putBoolean(Registry.allomanctic_metals[4], true);
		        			if(metals.get(i).equals(Registry.brass_ingot))
		        				nbt.putBoolean(Registry.allomanctic_metals[5], true);
		        			if(metals.get(i).equals(Registry.copper_ingot))
		        				nbt.putBoolean(Registry.allomanctic_metals[6], true);
		        			if(metals.get(i).equals(Registry.bronze_ingot))
		        				nbt.putBoolean(Registry.allomanctic_metals[7], true);
		        			
		        		}
		                
		                vial.setTag(nbt);
		                itemX = item.posX;
		                itemY = item.posY;
		                itemZ = item.posZ;
		                
		                processing = true;
		                result = vial;

		        	}
	        }
        	
        }
		
	}

}