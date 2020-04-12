package com.crimson.allomancy.container;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MetalPurifierContainer extends Container {

	private final IInventory tileMetalPurifier;
	   private final IIntArray field_216983_d;
	   private final Slot slot;
	   

	   public MetalPurifierContainer(int p_i50095_1_, PlayerInventory inv) {
	      this(p_i50095_1_, inv, new Inventory(5), new IntArray(2));
	   }

	   public MetalPurifierContainer(int p_i50096_1_, PlayerInventory inv, IInventory purifier, IIntArray p_i50096_4_) {
	      super(ContainerType.BREWING_STAND, p_i50096_1_);
	      assertInventorySize(purifier, 5);
	      assertIntArraySize(p_i50096_4_, 2);
	      this.tileMetalPurifier = purifier;
	      this.field_216983_d = p_i50096_4_;
	      this.addSlot(new MetalPurifierContainer.PotionSlot(purifier, 0, 56, 51));
	      this.addSlot(new MetalPurifierContainer.PotionSlot(purifier, 1, 79, 58));
	      this.addSlot(new MetalPurifierContainer.PotionSlot(purifier, 2, 102, 51));
	      this.slot = this.addSlot(new MetalPurifierContainer.IngredientSlot(purifier, 3, 79, 17));
	      this.addSlot(new MetalPurifierContainer.FuelSlot(purifier, 4, 17, 17));
	      this.trackIntArray(p_i50096_4_);

	      for(int i = 0; i < 3; ++i) {
	         for(int j = 0; j < 9; ++j) {
	            this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
	         }
	      }

	      for(int k = 0; k < 9; ++k) {
	         this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
	      }

	   }
	   
	   @Override
	   public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		   ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
		   this.detectAndSendChanges();
		   return stack;
	   }


//	   /**
//	    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
//	    * inventory and the other inventory(s).
//	    */
//	   public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
//	      ItemStack itemstack = ItemStack.EMPTY;
//	      Slot slot = this.inventorySlots.get(index);
//	      if (slot != null && slot.getHasStack()) {
//	         ItemStack itemstack1 = slot.getStack();
//	         itemstack = itemstack1.copy();
//	         if ((index < 0 || index > 2) && index != 3 && index != 4) {
//	            if (this.slot.isItemValid(itemstack1)) {
//	               if (!this.mergeItemStack(itemstack1, 3, 4, false)) {
//	                  return ItemStack.EMPTY;
//	               }
//	            } else if (itemstack.getCount() == 1) {
//	               if (!this.mergeItemStack(itemstack1, 0, 3, false)) {
//	                  return ItemStack.EMPTY;
//	               }
//	            } else if (MetalPurifierContainer.FuelSlot.isValidBrewingFuel(itemstack)) {
//	               if (!this.mergeItemStack(itemstack1, 4, 5, false)) {
//	                  return ItemStack.EMPTY;
//	               }
//	            } else if (index >= 5 && index < 32) {
//	               if (!this.mergeItemStack(itemstack1, 32, 41, false)) {
//	                  return ItemStack.EMPTY;
//	               }
//	            } else if (index >= 32 && index < 41) {
//	               if (!this.mergeItemStack(itemstack1, 5, 32, false)) {
//	                  return ItemStack.EMPTY;
//	               }
//	            } else if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
//	               return ItemStack.EMPTY;
//	            }
//	         } else {
//	            if (!this.mergeItemStack(itemstack1, 5, 41, true)) {
//	               return ItemStack.EMPTY;
//	            }
//
//	            slot.onSlotChange(itemstack1, itemstack);
//	         }
//
//	         if (itemstack1.isEmpty()) {
//	            slot.putStack(ItemStack.EMPTY);
//	         } else {
//	            slot.onSlotChanged();
//	         }
//
//	         if (itemstack1.getCount() == itemstack.getCount()) {
//	            return ItemStack.EMPTY;
//	         }
//
//	         slot.onTake(playerIn, itemstack1);
//	      }
//
//	      return itemstack;
//	   }


//	   @OnlyIn(Dist.CLIENT)
//	   public int func_216982_e() {
//	      return this.field_216983_d.get(1);
//	   }
//
//	   @OnlyIn(Dist.CLIENT)
//	   public int func_216981_f() {
//	      return this.field_216983_d.get(0);
//	   }

	   static class FuelSlot extends Slot {
	      public FuelSlot(IInventory iInventoryIn, int index, int xPosition, int yPosition) {
	         super(iInventoryIn, index, xPosition, yPosition);
	      }

	      public static boolean isValidBrewingFuel(ItemStack itemStackIn) {
	         return true;
	      }

	      public int getSlotStackLimit() {
	         return 64;
	      }
	   }

	   static class IngredientSlot extends Slot {
	      public IngredientSlot(IInventory iInventoryIn, int index, int xPosition, int yPosition) {
	         super(iInventoryIn, index, xPosition, yPosition);
	      }

	      public int getSlotStackLimit() {
	         return 64;
	      }
	   }

	   static class PotionSlot extends Slot {
	      public PotionSlot(IInventory iInventoryIn, int index, int xPosition, int yPosition) {
	         super(iInventoryIn, index, xPosition, yPosition);
	      }

	      public int getSlotStackLimit() {
	         return 1;
	      }
	   }

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		// TODO Auto-generated method stub
		return true;
	}
     

}
