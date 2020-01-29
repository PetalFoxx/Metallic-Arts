package com.crimson.allomancy.entity;


import com.crimson.allomancy.util.Registry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class NuggetEntity extends ProjectileItemEntity {
    private boolean dropItem = true;
    private int metal;
    private float strength;

    public NuggetEntity(EntityType<? extends ProjectileItemEntity> type, World world) {
        super(type, world);
    }
    
    public NuggetEntity(World world, int metal) {
        super(Registry.getNuggetEntities()[metal], world);
        this.metal = metal;
    }
    

    public NuggetEntity(EntityType<? extends ProjectileItemEntity> type, LivingEntity livingEntity, World world, int metal) {
        super(type, livingEntity, world);
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity ep = (PlayerEntity) livingEntity;
            this.metal = metal;
            if (ep.abilities.isCreativeMode) {
                this.dropItem = false;
            }
        }
    }

    public NuggetEntity(EntityType<? extends ProjectileItemEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
    }


    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {
        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) rayTraceResult).getEntity() == this.getThrower()) {
            return;
        }

        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
            ((EntityRayTraceResult) rayTraceResult).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float) 4 * (strength/10));
        }

        if (!this.world.isRemote) {
            ItemStack ammo = new ItemStack(Registry.getNuggetItems()[metal]);
            if (this.world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && rayTraceResult.getType() != RayTraceResult.Type.ENTITY && this.dropItem) {
                this.world.addEntity(new ItemEntity(this.world, this.posX, this.posY, this.posZ, ammo));
            }

            this.remove();
        }
    }
    
    public void setStrength(float strength) {
    	this.strength = strength;
    }


    @Override
    protected Item func_213885_i() {
        return Registry.getNuggetItems()[metal];
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
