package com.crimson.allomancy.util;

import com.crimson.allomancy.entity.GoldNuggetEntity;
import com.crimson.allomancy.entity.IronNuggetEntity;
import com.crimson.allomancy.entity.NuggetEntity;
import com.crimson.allomancy.network.NetworkHelper;
import com.crimson.allomancy.network.packets.AllomancyCapabilityPacket;
import com.crimson.allomancy.util.AllomancyConfig.INTERACTIONTYPE;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Contains all static, common methods in one place
 */

public class AllomancyUtils {


    public static final byte PUSH = 1;
    public static final byte PULL = -1;


    /**
     * Determines if a block is metal or not
     *
     * @param block to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isBlockMetal(Block block) {
        return AllomancyConfig.whitelist.contains(block.getRegistryName().toString());
    }

    /**
     * Determines if an item is metal or not
     *
     * @param item to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isItemMetal(ItemStack item) {
        return AllomancyConfig.whitelist.contains(item.getItem().getRegistryName().toString());
    }

    /**
     * Determines if an entity is metal or not
     *
     * @param entity to be checked
     * @return Whether or not the entity is metallic
     */
    public static boolean isEntityMetal(Entity entity, float strength) {
        if (entity == null) {
            return false;
        }

        if (entity instanceof ItemEntity) {
            return isItemMetal(((ItemEntity) entity).getItem());
        }
        if (entity instanceof ItemFrameEntity) {
            return isItemMetal(((ItemFrameEntity) entity).getDisplayedItem());
        }

        if (entity instanceof FallingBlockEntity) {
            return isBlockMetal(((FallingBlockEntity) entity).getBlockState().getBlock());
        }
        if (entity instanceof IronNuggetEntity || entity instanceof GoldNuggetEntity) {
            return true;
        }
        
        if (entity instanceof NuggetEntity) {
        	return true;
        }
        
        if (entity instanceof ArrowEntity) {
            return true;
        }
        if (entity instanceof AbstractMinecartEntity) {
            return true;
        }
        if(canAffectMob(entity, strength, INTERACTIONTYPE.THROW))
        	return true;
        if (entity instanceof MobEntity) {
            MobEntity ent = (MobEntity) entity;
            if (ent instanceof IronGolemEntity) {
                return true;
            }
            if (isItemMetal(ent.getHeldItem(Hand.MAIN_HAND)) || isItemMetal(ent.getHeldItem(Hand.OFF_HAND))) {
                return true;
            }
            for (ItemStack i : ent.getArmorInventoryList()) {
                if (isItemMetal(i)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    public static boolean canAffectMob(Entity ent, float strength, AllomancyConfig.INTERACTIONTYPE interaction) {
    	if (ent instanceof BlazeEntity)
    	{
    		if(AllomancyConfig.MOB.BLAZE.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof CaveSpiderEntity)
    	{
    		if(AllomancyConfig.MOB.CAVESPIDER.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof CreeperEntity)
    	{
    		if(AllomancyConfig.MOB.CREEPER.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof EndermanEntity)
    	{
    		if(AllomancyConfig.MOB.ENDERMAN.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof GhastEntity)
    	{
    		if(AllomancyConfig.MOB.GHAST.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof PhantomEntity) {
    		if(AllomancyConfig.MOB.PHANTOM.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof PillagerEntity || ent instanceof AbstractVillagerEntity || ent instanceof SheepEntity || ent instanceof CowEntity|| ent instanceof WolfEntity|| ent instanceof PigEntity)
    	{
    		if(AllomancyConfig.MOB.VILLAGER.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof ShulkerEntity)
    	{
    		if(AllomancyConfig.MOB.SHULKER.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof SkeletonEntity)
    	{
    		if(AllomancyConfig.MOB.SKELETON.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof SlimeEntity)
    	{
    		if(AllomancyConfig.MOB.SLIME.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof SpiderEntity)
    	{
    		if(AllomancyConfig.MOB.SPIDER.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof ZombieEntity || ent instanceof ZombieVillagerEntity || ent instanceof DrownedEntity || ent instanceof HuskEntity)
    	{
    		if(AllomancyConfig.MOB.ZOMBIE.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof IronGolemEntity)
    	{
    		if(AllomancyConfig.MOB.IRONGOLEM.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof WitherEntity)
    	{
    		if(AllomancyConfig.MOB.WITHER.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	if (ent instanceof EnderDragonEntity)
    	{
    		if(AllomancyConfig.MOB.ENDERDRAGON.getStrength(interaction) <= strength)
    			return true;
    	}
    	if (ent instanceof RabbitEntity)
    	{
    		if(AllomancyConfig.MOB.RABBIT.getStrength(interaction) <= strength)
    			return true;
    	}
    	
    	
    	if (strength > 70)
    		return true;
    	
    	
    	return false;
    }
    

    /**
     * Move an entity either toward or away from an anchor point
     *
     * @param directionScalar the direction and (possibly) scalar multiple of the magnitude
     * @param toMove          the entity to move
     * @param vec             the point being moved toward or away from
     */
    public static void move(double directionScalar, Entity toMove, BlockPos vec, float strength) {

        double motionX, motionY, motionZ, magnitude;
        if (toMove.isPassenger()) {
            toMove = toMove.getRidingEntity();
        }
        // Calculate the length of the vector between the entity and anchor
        magnitude = Math.sqrt(Math.pow((toMove.posX - (double) (vec.getX() + .5)), 2)
                + Math.pow((toMove.posY - (double) (vec.getY() + .5)), 2)
                + Math.pow((toMove.posZ - (double) (vec.getZ() + .5)), 2));
        // Get a unit(-ish) vector in the direction of motion
        motionX = ((toMove.posX - (double) (vec.getX() + .5)) * directionScalar * (1.1) / magnitude) * (strength / 10);
        motionY = ((toMove.posY - (double) (vec.getY() + .5)) * directionScalar * (1.1) / magnitude) * (strength / 10);
        motionZ = ((toMove.posZ - (double) (vec.getZ() + .5)) * directionScalar * (1.1) / magnitude) * (strength / 10);
        // Move along that vector, additively increasing motion until you max
        // out at the above values
        double x = toMove.getMotion().getX(), y = toMove.getMotion().getY(), z = toMove.getMotion().getZ();
        toMove.setMotion(Math.abs(x + motionX) > 0.5//0.01
                ? MathHelper.clamp(x + motionX, -Math.abs(motionX), motionX) : 0, Math.abs(y + motionY) > 0.5//0.01
                ? MathHelper.clamp(y + motionY, -Math.abs(motionY), motionY) : 0, Math.abs(z + motionZ) > 0.5//0.01
                ? MathHelper.clamp(z + motionZ, -Math.abs(motionZ), motionZ) : 0);

        toMove.velocityChanged = true;

        // Only save players from fall damage
        if (toMove instanceof ServerPlayerEntity) {
            toMove.fallDistance = 0;
        }
    }


    /**
     * Runs each worldTick, checking the burn times, abilities, and metal
     * amounts. Then syncs to the client to make sure everyone is on the same
     * page
     *
     * @param capability the AllomancyCapabilities data
     * @param player     the player being checked
     */
    public static void updateMetalBurnTime(AllomancyCapability capability, LivingEntity player) {
        for (int i = 0; i < 8; i++) {
            if (capability.getMetalBurning(i)) {
                if (!capability.canBurn(i)) {
                    // put out any metals that the player shouldn't be able to burn
                    capability.setMetalBurning(i, false);
                    if(player instanceof ServerPlayerEntity)
                    	NetworkHelper.sendTo(new AllomancyCapabilityPacket(capability, player.getEntityId()), (ServerPlayerEntity) player);
                } else {
                    if(capability.getMetalFlaring(i))
                    {
                    	capability.setBurnTime(i, capability.getBurnTime(i) - 2);
                    	if(Math.random() == 1.0)
                    		capability.setSavant(i, capability.getSavant(i) + 2);
                    } else
                    {
                    	capability.setBurnTime(i, capability.getBurnTime(i) - 1);
                    	if(Math.random() == 1.0)
                    		capability.setSavant(i, capability.getSavant(i) + 1);
                    }
                    if (capability.getBurnTime(i) == 0) {
                        capability.setBurnTime(i, capability.MAX_BURN_TIME[i]);
                        capability.setMetalAmounts(i, capability.getMetalAmounts(i) - 1);
                        if(player instanceof ServerPlayerEntity)
                        	NetworkHelper.sendTo(new AllomancyCapabilityPacket(capability, player.getEntityId()), (ServerPlayerEntity) player);
                        if (capability.getMetalAmounts(i) == 0) {
                            capability.setMetalBurning(i, false);
                            NetworkHelper.sendTo(new AllomancyCapabilityPacket(capability, player.getEntityId()), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player));
                        }
                    }
                }
            } else if (capability.getMetalFlaring(i))
            {
            	capability.setMetalFlaring(i, false);
            	if(player instanceof ServerPlayerEntity)
            		NetworkHelper.sendTo(new AllomancyCapabilityPacket(capability, player.getEntityId()), (ServerPlayerEntity) player);
            }
        }
    }


}
