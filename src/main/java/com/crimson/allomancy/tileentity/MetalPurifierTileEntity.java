package com.crimson.allomancy.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.crimson.allomancy.ModTileEntityTypes;
import com.crimson.allomancy.container.MetalPurifierContainer;
import com.crimson.allomancy.util.Registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class MetalPurifierTileEntity extends LockableTileEntity implements IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity {
	private IBlockReader reader;
	private int creationTime = 300;
	private List<Item> metals = new ArrayList<Item>();
	private boolean processing = false;
	private ItemStack result = null;
	private double itemX, itemY, itemZ;
	private IInventory purifierInv;
	private IIntArray purifierArray;
	
	public MetalPurifierTileEntity() {
		super(ModTileEntityTypes.METAL_PURIFIER);
		purifierInv = new Inventory(5);
		purifierArray = new IntArray(2);
	}
	
	
	public MetalPurifierTileEntity(BlockPos pos, World world) {
		super(ModTileEntityTypes.METAL_PURIFIER);
		this.world = world;
		this.pos = pos;
		purifierInv = new Inventory(5);
		purifierArray = new IntArray(2);
		//resetTimer();
	}
	
	public MetalPurifierTileEntity(IBlockReader world) {
		super(ModTileEntityTypes.METAL_PURIFIER);
		this.reader = world;
		purifierInv = new Inventory(5);
		purifierArray = new IntArray(2);
	}
	
	
	public void resetTimer() {
		creationTime = 300;
		metals = new ArrayList<Item>();
		processing = false;
	}
	

	@Override
	public void tick() {
		Random rand = new Random();
		
		if (!this.world.isRemote) {
			if(checkValidRecipe() && creationTime == 0)
			{
				for(int i = 0; i < 3; i++)
				{
					processRecipe(i);
					
				}
				if (rand.nextDouble() < 0.1D) {
	                this.world.playSound(this.itemX, this.itemY, this.itemZ, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
	            }
				
				this.world.addParticle(ParticleTypes.SMOKE, this.itemX, this.itemY, this.itemZ, 0.0D, 0.0D, 0.0D);
				this.world.addParticle(ParticleTypes.FLAME, this.itemX, this.itemY, this.itemZ, 0.0D, 0.0D, 0.0D);
	            
				purifierInv.decrStackSize(3, 1);
				processing = false;
				creationTime = 600;
			}
			else if (checkValidRecipe() && creationTime > 0 && processing)
				creationTime--;
			else if (checkValidRecipe() && !processing)
				processing = true;
			else
			{
				processing = false;
				creationTime = 300;
			}
		}

	}
	
	
	private boolean checkValidRecipe() {
		Item metal = purifierInv.getStackInSlot(3).getItem();
		boolean metalCheck = false;
		boolean vialCheck = false;
		if((metal.equals(Items.IRON_INGOT) || metal.equals(Registry.steel_ingot) || metal.equals(Registry.tin_ingot) 
				||metal.equals(Registry.pewter_ingot) ||metal.equals(Registry.zinc_ingot) ||metal.equals(Registry.brass_ingot) 
				||metal.equals(Registry.copper_ingot) ||metal.equals(Registry.bronze_ingot)))
			metalCheck = true;
		
		for(int i = 0; i < 3; i++)
			if (purifierInv.getStackInSlot(i).getItem().equals(Items.POTION) || purifierInv.getStackInSlot(i).getItem().equals(Registry.vial))
				vialCheck = true;
		
		return metalCheck && vialCheck;
	}
	
	private void processRecipe(int slot) {
		if(purifierInv.getStackInSlot(slot).getItem().equals(Items.POTION) || purifierInv.getStackInSlot(slot).getItem().equals(Registry.vial))
		{
			CompoundNBT nbt;
			ItemStack vial;
			if(purifierInv.getStackInSlot(slot).getItem().equals(Registry.vial))
			{
				if(purifierInv.getStackInSlot(slot).getTag() != null) 
					nbt = purifierInv.getStackInSlot(slot).getTag();
				else
					nbt = new CompoundNBT();
				vial = purifierInv.getStackInSlot(slot);
			} else {
				nbt = new CompoundNBT();
				vial = new ItemStack(Registry.vial, 1);
			}
	
	
			if(purifierInv.getStackInSlot(3).getItem().equals(Items.IRON_INGOT))
				nbt.putBoolean(Registry.allomanctic_metals[0], true);
			if(purifierInv.getStackInSlot(3).getItem().equals(Registry.steel_ingot))
				nbt.putBoolean(Registry.allomanctic_metals[1], true);
			if(purifierInv.getStackInSlot(3).getItem().equals(Registry.tin_ingot))
				nbt.putBoolean(Registry.allomanctic_metals[2], true);
			if(purifierInv.getStackInSlot(3).getItem().equals(Registry.pewter_ingot))
				nbt.putBoolean(Registry.allomanctic_metals[3], true);
			if(purifierInv.getStackInSlot(3).getItem().equals(Registry.zinc_ingot))
				nbt.putBoolean(Registry.allomanctic_metals[4], true);
			if(purifierInv.getStackInSlot(3).getItem().equals(Registry.brass_ingot))
				nbt.putBoolean(Registry.allomanctic_metals[5], true);
			if(purifierInv.getStackInSlot(3).getItem().equals(Registry.copper_ingot))
				nbt.putBoolean(Registry.allomanctic_metals[6], true);
			if(purifierInv.getStackInSlot(3).getItem().equals(Registry.bronze_ingot))
				nbt.putBoolean(Registry.allomanctic_metals[7], true);
	        
			
			
	        vial.setTag(nbt);
	        
			purifierInv.setInventorySlotContents(slot, vial);
		}
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return purifierInv.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		// TODO Auto-generated method stub
		return purifierInv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		// TODO Auto-generated method stub
		return purifierInv.decrStackSize(3, 1);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return purifierInv.getStackInSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		purifierInv.setInventorySlotContents(index, stack);
		
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void clear() {
		purifierInv.clear();
		
	}

	@Override
	public void fillStackedContents(RecipeItemHelper helper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRecipeUsed(IRecipe<?> recipe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRecipe<?> getRecipeUsed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("Metal Purifier");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new MetalPurifierContainer(id, player, purifierInv, purifierArray);
	}

}