package com.crimson.allomancy.ai;

import com.crimson.allomancy.util.AllomancyCapability;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class AIAlloAttackNearest extends NearestAttackableTargetGoal {
	
	AllomancyCapability cap;
	
	public AIAlloAttackNearest(MobEntity p_i50313_1_, Class p_i50313_2_, boolean p_i50313_3_) {
		super(p_i50313_1_, p_i50313_2_, p_i50313_3_);
		cap = AllomancyCapability.forPlayer(p_i50313_1_);
		
		//if(cap.canBurn(AllomancyCapability.TIN))
			//cap.setMetalBurning(AllomancyCapability.TIN, true);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		if(cap.getMetalBurning(AllomancyCapability.TIN))
			return this.goalOwner.getBoundingBox().grow(targetDistance + cap.getCalcBurnStrength(AllomancyCapability.TIN) / 10, 4.0D, targetDistance + cap.getCalcBurnStrength(AllomancyCapability.TIN));
		
		
		
		return this.goalOwner.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
	}


	   /**
	    * Execute a one shot task or start executing a continuous task
	    */
	@Override
	public void startExecuting() {
		if(cap.canBurn(AllomancyCapability.PEWTER))
			//cap.setMetalBurning(AllomancyCapability.PEWTER, true);
	   this.goalOwner.setAttackTarget(this.nearestTarget);
	   super.startExecuting();
	}
	
	
	@Override
	protected void findNearestTarget() {
	         this.nearestTarget = this.goalOwner.world.getClosestPlayer(this.targetEntitySelector, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double)this.goalOwner.getEyeHeight(), this.goalOwner.posZ);
	      

	   }
	   
}
