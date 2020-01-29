package com.crimson.allomancy.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.item.metalmind.MetalMindItem;
import com.crimson.allomancy.item.metalmind.MetalMindItem.METALMIND_ACTION;

public class AllomancyCapability implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(AllomancyCapability.class)
    public static final Capability<AllomancyCapability> PLAYER_CAP = null;

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Allomancy.MODID, "allomancy_data");

    private LazyOptional<AllomancyCapability> handler;

    //todo seriously rethink this cap
    public static final int[] MAX_BURN_TIME = {3600, 3600, 7200, 2400, 3600, 3600, 4800, 3200};
    public static final int IRON = 0, STEEL = 1, TIN = 2, PEWTER = 3, ZINC = 4, BRASS = 5, COPPER = 6, BRONZE = 7;

    private boolean isAllomancer = false;
    private int damageStored = 0;
    private int[] BurnTime = {3600, 3600, 7200, 2400, 3600, 3600, 4800, 3200};
    private int[] MetalAmounts = {0, 0, 0, 0, 0, 0, 0, 0};
    private int[] BurnStrength = {10, 10, 10, 10, 10, 10, 10, 10};
    private int[] savantism = {0, 0, 0, 0, 0, 0, 0, 0};
    private boolean[] CanBurn = {false, false, false, false, false, false, false, false};
    private boolean[] CanStore = {false, false, false, false, false, false, false, false};
    private boolean[] MetalBurning = {false, false, false, false, false, false, false, false};
    private boolean[] MetalFlaring = {false, false, false, false, false, false, false, false};
    private boolean[] FaveMetal = {false, false, false, false, false, false, false, false};
    private ItemStack[] activeMetalMinds = new ItemStack[Registry.allomanctic_metals.length];
    private float coppercloudStrength = 0;
    private List<BlockPos> marks = new ArrayList<BlockPos>();
    

    /**
     * Retrieve data for a specific player
     *
     * @param player the player you want data for
     * @return the AllomancyCapabilites data of the player
     */
    public static AllomancyCapability forPlayer(Entity player) {
        //return player.getCapability(PLAYER_CAP).orElseThrow(() -> new RuntimeException("Capability not attached!"));
        return player.getCapability(PLAYER_CAP).orElse(new AllomancyCapability());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return PLAYER_CAP.orEmpty(cap, handler);
    }
    
    public AllomancyCapability() {
        handler = LazyOptional.of(() -> this);
    }
    
    /**
     * Check if a specific metal is burning
     *
     * @param metal the index of the metal to check
     * @return whether or not it is burning
     */
    public boolean getMetalBurning(int metal) {
        return MetalBurning[metal];
    }
    
    
    /**
     * Check if a specific metal is flaring
     *
     * @param metal the index of the metal to check
     * @return whether or not it is flaring
     */
    public boolean getMetalFlaring(int metal) {
        return MetalFlaring[metal];
    }
    
    /**
     * Check if a specific metal is flaring
     *
     * @param metal the index of the metal to check
     * @return whether or not it is flaring
     */
    public void setMetalFlaring(int metal, Boolean bool) {
        this.MetalFlaring[metal] = bool;
    }
    
    /**
     * Check if a specific metal is fave
     *
     * @param metal the index of the metal to check
     * @return whether or not it is flaring
     */
    public boolean getFaveMetal(int metal) {
        return FaveMetal[metal];
    }
    
    public boolean isSavant(int metal) {
        return savantism[metal] >= 100;
    }
    
    public int getSavant(int metal) {
        return savantism[metal];
    }
    
    public void setSavant(int metal, int number) {
        savantism[metal] = number;
    }

    /**
     * Set whether or not a metal is burning
     *
     * @param metal        the index of the metal to set
     * @param metalBurning the value to set
     */
    public void setMetalBurning(int metal, boolean metalBurning) {
    	if(canBurn(metal))
    		MetalBurning[metal] = metalBurning;
    }

    /**
     * Get how much damage has been accumulated
     *
     * @return the amount of damage
     */
    public int getDamageStored() {
        return damageStored;
    }

    /**
     * Set the amount of damage stored
     *
     * @param damageStored the amount of damage
     */
    public void setDamageStored(int damageStored) {
        this.damageStored = damageStored;
    }

    /**
     * Get the amount of a specific metal
     *
     * @param metal the index of the metal to retrieve
     * @return the amount of metal
     */
    public int getMetalAmounts(int metal) {
        return metal >= 0 && metal < 8 ? MetalAmounts[metal] : 0;
    }

    /**
     * Set the amount of a specific metal
     *
     * @param metal        the index of the metal to set
     * @param metalAmounts the amount of metal
     */
    public void setMetalAmounts(int metal, int metalAmounts) {
        MetalAmounts[metal] = metalAmounts;
    }

    /**
     * Get the burn time of a specific metal
     *
     * @param metal the index of the metal to retrieve
     * @return the burn time
     */
    public int getBurnTime(int metal) {
        return BurnTime[metal];
    }

    /**
     * Set the burn time of a specific metal
     *
     * @param metal    the index of the metal to set
     * @param burnTime the burn time
     */
    public void setBurnTime(int metal, int burnTime) {
        BurnTime[metal] = burnTime;
    }
    
    public float isInCoppercloud () {
    	return coppercloudStrength;
    }
    
    public void setIsInCoppercloud (float strength) {
    	coppercloudStrength = strength;
    }
    
    public void setCanBurn(int metal, boolean canBurn) {
        CanBurn[metal] = canBurn;
    }
    
    public boolean canBurn(int metal) {
        return CanBurn[metal];
    }
    
    public void setCanStore(int metal, boolean canStore) {
        CanStore[metal] = canStore;
    }
    
    public boolean canStore(int metal) {
        return CanStore[metal];
    }
    
    public boolean isBurningMetal() {
        return (MetalBurning[0] || MetalBurning[1] || MetalBurning[2] || MetalBurning[3] || MetalBurning[4] || MetalBurning[5] || MetalBurning[6] || MetalBurning[7]);
    }
    
    public void setIsAllomancer(boolean bool) {
        isAllomancer = true;
    }
    
    public boolean isAllomancer() {
        return isAllomancer;
    }
    
    
    public void setBurnStrength(int metal, int power) {
        BurnStrength[metal] = power;
    }
    
    public int getBurnStrength(int metal) {
        int strength = BurnStrength[metal];
        if(MetalFlaring[metal])
        {
        	strength = strength*2;
        }
    	
        if(isSavant(metal)) {
        	strength = strength + (5 * (getSavant(metal)/100));
        }
        
    	return strength;
    }
    
    public boolean hasMetalmind(int metal) {
    	if(activeMetalMinds[metal] != null)
    		return true;
    	return false;
    }
    
    public ItemStack getMetalmind(int metal) {
    	return activeMetalMinds[metal];
    }
    
    public void addActiveMetalMind(ItemStack metalMind, int metal) {
    	if(activeMetalMinds[metal] != null) {
    		//((MetalMindItem) activeMetalMinds[metal].getItem()).changeAction(METALMIND_ACTION.INACTIVE,  , metalMind);
    		((MetalMindItem) activeMetalMinds[metal].getItem()).setActive(false);
    	}
    				
    	activeMetalMinds[metal] = metalMind;
    	((MetalMindItem) activeMetalMinds[metal].getItem()).setActive(true);
    }
    
    public List<BlockPos> getMarkedLocations() {
    	return marks;
    }
    
    public void addMarkedLocation(BlockPos mark) {
    	marks.add(mark);
    }
    
    public void resetMarkedLocation() {
    	marks = new ArrayList<BlockPos>();
    }
    

    public static void register() {
        CapabilityManager.INSTANCE.register(AllomancyCapability.class, new AllomancyCapability.Storage(), () -> null);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT allomancy_data = new CompoundNBT();

        CompoundNBT metal_storage = new CompoundNBT();
        metal_storage.putInt("iron", this.getMetalAmounts(0));
        metal_storage.putInt("steel", this.getMetalAmounts(1));
        metal_storage.putInt("tin", this.getMetalAmounts(2));
        metal_storage.putInt("pewter", this.getMetalAmounts(3));
        metal_storage.putInt("zinc", this.getMetalAmounts(4));
        metal_storage.putInt("brass", this.getMetalAmounts(5));
        metal_storage.putInt("copper", this.getMetalAmounts(6));
        metal_storage.putInt("bronze", this.getMetalAmounts(7));

        allomancy_data.put("metal_storage", metal_storage);

        CompoundNBT metal_burning = new CompoundNBT();
        metal_burning.putBoolean("iron", this.getMetalBurning(0));
        metal_burning.putBoolean("steel", this.getMetalBurning(1));
        metal_burning.putBoolean("tin", this.getMetalBurning(2));
        metal_burning.putBoolean("pewter", this.getMetalBurning(3));
        metal_burning.putBoolean("zinc", this.getMetalBurning(4));
        metal_burning.putBoolean("brass", this.getMetalBurning(5));
        metal_burning.putBoolean("copper", this.getMetalBurning(6));
        metal_burning.putBoolean("bronze", this.getMetalBurning(7));

        allomancy_data.put("metal_burning", metal_burning);
        
        
        CompoundNBT metalFlaring = new CompoundNBT();
        metalFlaring.putBoolean("iron", this.getMetalFlaring(0));
        metalFlaring.putBoolean("steel", this.getMetalFlaring(1));
        metalFlaring.putBoolean("tin", this.getMetalFlaring(2));
        metalFlaring.putBoolean("pewter", this.getMetalFlaring(3));
        metalFlaring.putBoolean("zinc", this.getMetalFlaring(4));
        metalFlaring.putBoolean("brass", this.getMetalFlaring(5));
        metalFlaring.putBoolean("copper", this.getMetalFlaring(6));
        metalFlaring.putBoolean("bronze", this.getMetalFlaring(7));

        allomancy_data.put("metalFlaring", metalFlaring);
        
        
        CompoundNBT faveMetal = new CompoundNBT();
        faveMetal.putBoolean("iron", this.getFaveMetal(0));
        faveMetal.putBoolean("steel", this.getFaveMetal(1));
        faveMetal.putBoolean("tin", this.getFaveMetal(2));
        faveMetal.putBoolean("pewter", this.getFaveMetal(3));
        faveMetal.putBoolean("zinc", this.getFaveMetal(4));
        faveMetal.putBoolean("brass", this.getFaveMetal(5));
        faveMetal.putBoolean("copper", this.getFaveMetal(6));
        faveMetal.putBoolean("bronze", this.getFaveMetal(7));

        allomancy_data.put("faveMetal", faveMetal);
        
        
        CompoundNBT canBurn = new CompoundNBT();
        canBurn.putBoolean("iron", this.canBurn(0));
        canBurn.putBoolean("steel", this.canBurn(1));
        canBurn.putBoolean("tin", this.canBurn(2));
        canBurn.putBoolean("pewter", this.canBurn(3));
        canBurn.putBoolean("zinc", this.canBurn(4));
        canBurn.putBoolean("brass", this.canBurn(5));
        canBurn.putBoolean("copper", this.canBurn(6));
        canBurn.putBoolean("bronze", this.canBurn(7));

        allomancy_data.put("canBurn", canBurn);
        
        
        CompoundNBT burnStrength = new CompoundNBT();
        burnStrength.putInt("iron", this.BurnStrength[0]);
        burnStrength.putInt("steel", this.BurnStrength[1]);
        burnStrength.putInt("tin", this.BurnStrength[2]);
        burnStrength.putInt("pewter", this.BurnStrength[3]);
        burnStrength.putInt("zinc", this.BurnStrength[4]);
        burnStrength.putInt("brass", this.BurnStrength[5]);
        burnStrength.putInt("copper", this.BurnStrength[6]);
        burnStrength.putInt("bronze", this.BurnStrength[7]);

        allomancy_data.put("burnStrength", burnStrength);
        
        
        CompoundNBT isAllomancer = new CompoundNBT();
        isAllomancer.putBoolean("isAllomancer", this.isAllomancer());
        allomancy_data.put("isAllomancer", isAllomancer);
        

        return allomancy_data;

    }

    @Override
    public void deserializeNBT(CompoundNBT allomancy_data) {

        CompoundNBT metal_storage = (CompoundNBT) allomancy_data.get("metal_storage");
        this.MetalAmounts[0] = metal_storage.getInt("iron");
        this.MetalAmounts[1] = metal_storage.getInt("steel");
        this.MetalAmounts[2] = metal_storage.getInt("tin");
        this.MetalAmounts[3] = metal_storage.getInt("pewter");
        this.MetalAmounts[4] = metal_storage.getInt("zinc");
        this.MetalAmounts[5] = metal_storage.getInt("brass");
        this.MetalAmounts[6] = metal_storage.getInt("copper");
        this.MetalAmounts[7] = metal_storage.getInt("bronze");

        CompoundNBT metal_burning = (CompoundNBT) allomancy_data.get("metal_burning");
        this.MetalBurning[0] = metal_burning.getBoolean("iron");
        this.MetalBurning[1] = metal_burning.getBoolean("steel");
        this.MetalBurning[2] = metal_burning.getBoolean("tin");
        this.MetalBurning[3] = metal_burning.getBoolean("pewter");
        this.MetalBurning[4] = metal_burning.getBoolean("zinc");
        this.MetalBurning[5] = metal_burning.getBoolean("brass");
        this.MetalBurning[6] = metal_burning.getBoolean("copper");
        this.MetalBurning[7] = metal_burning.getBoolean("bronze");
        
        try{
	        if(this.MetalFlaring != null) {
	        CompoundNBT metalFlaring = (CompoundNBT) allomancy_data.get("metalFlaring");
	        this.MetalFlaring[0] = metalFlaring.getBoolean("iron");
	        this.MetalFlaring[1] = metalFlaring.getBoolean("steel");
	        this.MetalFlaring[2] = metalFlaring.getBoolean("tin");
	        this.MetalFlaring[3] = metalFlaring.getBoolean("pewter");
	        this.MetalFlaring[4] = metalFlaring.getBoolean("zinc");
	        this.MetalFlaring[5] = metalFlaring.getBoolean("brass");
	        this.MetalFlaring[6] = metalFlaring.getBoolean("copper");
	        this.MetalFlaring[7] = metalFlaring.getBoolean("bronze");
	        }
        } finally {}

        try{
	        if(this.FaveMetal != null) {
	        CompoundNBT faveMetal = (CompoundNBT) allomancy_data.get("faveMetal");
	        this.FaveMetal[0] = faveMetal.getBoolean("iron");
	        this.FaveMetal[1] = faveMetal.getBoolean("steel");
	        this.FaveMetal[2] = faveMetal.getBoolean("tin");
	        this.FaveMetal[3] = faveMetal.getBoolean("pewter");
	        this.FaveMetal[4] = faveMetal.getBoolean("zinc");
	        this.FaveMetal[5] = faveMetal.getBoolean("brass");
	        this.FaveMetal[6] = faveMetal.getBoolean("copper");
	        this.FaveMetal[7] = faveMetal.getBoolean("bronze");
	        }
        } finally {}
        
        try{
	        if(this.CanBurn != null) {
	        CompoundNBT canBurn = (CompoundNBT) allomancy_data.get("canBurn");
	        this.CanBurn[0] = canBurn.getBoolean("iron");
	        this.CanBurn[1] = canBurn.getBoolean("steel");
	        this.CanBurn[2] = canBurn.getBoolean("tin");
	        this.CanBurn[3] = canBurn.getBoolean("pewter");
	        this.CanBurn[4] = canBurn.getBoolean("zinc");
	        this.CanBurn[5] = canBurn.getBoolean("brass");
	        this.CanBurn[6] = canBurn.getBoolean("copper");
	        this.CanBurn[7] = canBurn.getBoolean("bronze");
	        }
	    } finally {}
        
	    try{
	    	if(this.BurnStrength != null) {
	        CompoundNBT burnStrength = (CompoundNBT) allomancy_data.get("burnStrength");
	        this.BurnStrength[0] = burnStrength.getInt("iron");
	        this.BurnStrength[1] = burnStrength.getInt("steel");
	        this.BurnStrength[2] = burnStrength.getInt("tin");
	        this.BurnStrength[3] = burnStrength.getInt("pewter");
	        this.BurnStrength[4] = burnStrength.getInt("zinc");
	        this.BurnStrength[5] = burnStrength.getInt("brass");
	        this.BurnStrength[6] = burnStrength.getInt("copper");
	        this.BurnStrength[7] = burnStrength.getInt("bronze");
	    	}
	    } finally {}
        
	    try{
	        CompoundNBT isAllomancer = (CompoundNBT) allomancy_data.get("isAllomancer");
	        this.isAllomancer = isAllomancer.getBoolean("isAllomancer");
	    } finally {}
        
        

    }


    public static class Storage implements Capability.IStorage<AllomancyCapability> {

        @Override
        public INBT writeNBT(Capability<AllomancyCapability> capability, AllomancyCapability instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<AllomancyCapability> capability, AllomancyCapability instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }
    }
}
