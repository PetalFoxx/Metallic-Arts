package com.crimson.allomancy.mobs;

import com.crimson.allomancy.util.AllomancyCapability;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class AllomanticZombie extends ZombieEntity {

	
	public AllomanticZombie(EntityType<? extends AllomanticZombie> type, World worldIn) {
	      super(type, worldIn);
	      AllomancyCapability.forPlayer(this).canBurn(AllomancyCapability.PEWTER);
			AllomancyCapability.forPlayer(this).isAllomancer();
			AllomancyCapability.forPlayer(this).setBurnStrength(AllomancyCapability.PEWTER, 15);
			AllomancyCapability.forPlayer(this).setMetalAmounts(AllomancyCapability.PEWTER, 10);
			
	//		AllomancyCapability.forPlayer(this).setMetalBurning(AllomancyCapability.PEWTER, true);
	   }
	
	
	public AllomanticZombie(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
		AllomancyCapability.forPlayer(this).canBurn(AllomancyCapability.PEWTER);
		AllomancyCapability.forPlayer(this).isAllomancer();
		AllomancyCapability.forPlayer(this).setBurnStrength(AllomancyCapability.PEWTER, 15);
		AllomancyCapability.forPlayer(this).setMetalAmounts(AllomancyCapability.PEWTER, 10);
		
	//	AllomancyCapability.forPlayer(this).setMetalBurning(AllomancyCapability.PEWTER, true);
	}
	
	@Override
	protected void applyEntityAI() {
	      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
	      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::isBreakDoorsTaskSet));
	      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
	      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp(ZombiePigmanEntity.class));
	      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
	      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
	      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY));
	   }

	@Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
