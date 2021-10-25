package com.crimson.allomancy.network.packets;

import com.crimson.allomancy.ai.AIAttackOnCollideExtended;
import com.crimson.allomancy.ai.AIEvilAttack;
import com.crimson.allomancy.util.AllomancyConfig;
import com.crimson.allomancy.util.AllomancyUtils;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeEmotionPacket {

    private int entityID;
    private emotion emote;
    private float strength;
    public enum emotion {AGGRO, CALM, IGNORE};

    /**
     * Make a mob either angry or passive, depending on aggro
     *
     * @param entityID the mob to be effected
     * @param aggro    whether the mob should be mad or passive
     */
    public ChangeEmotionPacket(int entityID, emotion emote, float strength) {
        this.entityID = entityID;
        this.emote = emote;
        this.strength = strength;
    }


    public static void encode(ChangeEmotionPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.entityID);
        buf.writeEnumValue(pkt.emote);
        buf.writeFloat(pkt.strength);
    }

    public static ChangeEmotionPacket decode(PacketBuffer buf) {
        return new ChangeEmotionPacket(buf.readInt(), buf.readEnumValue(emotion.class), buf.readFloat());
    }


    public static void handle(final ChangeEmotionPacket message, Supplier<NetworkEvent.Context> ctx) {
        
    	ctx.get().enqueueWork(() -> {
                    LivingEntity target;
                    target = (LivingEntity) ctx.get().getSender().world.getEntityByID(message.entityID);
                    
                    if(target instanceof ServerPlayerEntity)
                    {
                    	if(message.emote == emotion.AGGRO) {
                    		((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.STRENGTH, 600, 1, false, false, false));
                    		((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.RESISTANCE, 600, -2, false, false, false));
                    	}
                    	if(message.emote == emotion.CALM) {
                    		((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.WEAKNESS, 600, 1, false, false, false));
                    		((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.RESISTANCE, 600, 1, false, false, false));
                    	}
                    }
                    else
                    {
                    	CreatureEntity cTarget = (CreatureEntity) target;
                    
                    
	                    if(AllomancyUtils.canAffectMob(cTarget, message.strength, AllomancyConfig.INTERACTIONTYPE.EMOTE))
	                    {
		                    if ((cTarget != null) && message.emote == emotion.AGGRO) {
		                        // Remove all current goals
		                    	cTarget.goalSelector.getRunningGoals().forEach(cTarget.goalSelector::removeGoal);
		                    	cTarget.targetSelector.getRunningGoals().forEach(cTarget.targetSelector::removeGoal);
		                    	cTarget.goalSelector.tick();
		                    	cTarget.targetSelector.tick();
		                        //Enable Targeting goals
		                    	cTarget.targetSelector.enableFlag(Goal.Flag.TARGET);
		                        //Add new goals
		                    	cTarget.goalSelector.addGoal(1, new SwimGoal(cTarget));
		                    	cTarget.targetSelector.addGoal(5, new AIAttackOnCollideExtended(cTarget, 1d, false));
		                    	cTarget.targetSelector.addGoal(5, new NearestAttackableTargetGoal<CreatureEntity>(cTarget, CreatureEntity.class, false));
		                    	cTarget.goalSelector.addGoal(5, new RandomWalkingGoal(cTarget, 0.8D));
		                    	cTarget.goalSelector.addGoal(6, new LookAtGoal(cTarget, CreatureEntity.class, 8.0F));
		                    	cTarget.goalSelector.addGoal(6, new LookRandomlyGoal(cTarget));
		                    	cTarget.targetSelector.addGoal(2, new HurtByTargetGoal(cTarget, CreatureEntity.class));
		                        if (cTarget instanceof CreeperEntity) {
		                        	cTarget.goalSelector.addGoal(2, new CreeperSwellGoal((CreeperEntity) cTarget));
		                        }
		                        if (cTarget instanceof RabbitEntity) {
		                        	cTarget.goalSelector.addGoal(4, new AIEvilAttack((RabbitEntity) cTarget));
		                        }
		                        cTarget.addPotionEffect(new EffectInstance(Effects.GLOWING, 50, 0, false, false, false));
		
		
		                    } else if ((cTarget != null) && message.emote == emotion.CALM) {
		                        // Remove all current goals
		                    	cTarget.goalSelector.getRunningGoals().forEach(cTarget.goalSelector::removeGoal);
		                    	cTarget.targetSelector.getRunningGoals().forEach(cTarget.targetSelector::removeGoal);
		                    	cTarget.goalSelector.tick();
		                    	cTarget.targetSelector.tick();
		                    	cTarget.setAttackTarget(null);
		                    	cTarget.setRevengeTarget(null);
		                        //Disable targeting as a whole
		                    	cTarget.targetSelector.disableFlag(Goal.Flag.TARGET);
		                        //Add new goals
		                    	cTarget.goalSelector.addGoal(0, new SwimGoal(cTarget));
		                    	cTarget.goalSelector.addGoal(5, new RandomWalkingGoal(cTarget, 1.0D));
		                    	cTarget.goalSelector.addGoal(6, new LookAtGoal(cTarget, PlayerEntity.class, 6.0F));
		                    	cTarget.goalSelector.addGoal(7, new LookRandomlyGoal(cTarget));
		                    	cTarget.addPotionEffect(new EffectInstance(Effects.GLOWING, 50, 0, false, false, false));
		
		                    } else if ((cTarget != null) && message.emote == emotion.IGNORE) {
		                        // Remove all current goals
		                    	cTarget.goalSelector.getRunningGoals().forEach(cTarget.goalSelector::removeGoal);
		                    	cTarget.targetSelector.getRunningGoals().forEach(cTarget.targetSelector::removeGoal);
		                    	cTarget.goalSelector.tick();
		                    	cTarget.targetSelector.tick();
		                    	cTarget.setAttackTarget(null);
		                    	cTarget.setRevengeTarget(null);
		                    	cTarget.goalSelector.addGoal(0, new SwimGoal(cTarget));
		                    	cTarget.goalSelector.addGoal(5, new RandomWalkingGoal(cTarget, 1.0D));
		                    	cTarget.goalSelector.addGoal(7, new LookRandomlyGoal(cTarget));
		
		                }
	            }
            }
    	}
        );
    }
}