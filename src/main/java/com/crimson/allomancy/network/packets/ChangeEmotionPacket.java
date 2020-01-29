package com.crimson.allomancy.network.packets;

import com.crimson.allomancy.ai.AIAttackOnCollideExtended;
import com.crimson.allomancy.ai.AIEvilAttack;
import com.crimson.allomancy.util.AllomancyConfig;
import com.crimson.allomancy.util.AllomancyUtils;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
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
                    CreatureEntity target;
                    target = (CreatureEntity) ctx.get().getSender().world.getEntityByID(message.entityID);
                    if(AllomancyUtils.canAffectMob(target, message.strength, AllomancyConfig.INTERACTIONTYPE.EMOTE))
                    {
	                    if ((target != null) && message.emote == emotion.AGGRO) {
	                        // Remove all current goals
	                        target.goalSelector.getRunningGoals().forEach(target.goalSelector::removeGoal);
	                        target.targetSelector.getRunningGoals().forEach(target.targetSelector::removeGoal);
	                        target.goalSelector.tick();
	                        target.targetSelector.tick();
	                        //Enable Targeting goals
	                        target.targetSelector.enableFlag(Goal.Flag.TARGET);
	                        //Add new goals
	                        target.goalSelector.addGoal(1, new SwimGoal(target));
	                        target.targetSelector.addGoal(5, new AIAttackOnCollideExtended(target, 1d, false));
	                        target.targetSelector.addGoal(5, new NearestAttackableTargetGoal<CreatureEntity>(target, CreatureEntity.class, false));
	                        target.goalSelector.addGoal(5, new RandomWalkingGoal(target, 0.8D));
	                        target.goalSelector.addGoal(6, new LookAtGoal(target, CreatureEntity.class, 8.0F));
	                        target.goalSelector.addGoal(6, new LookRandomlyGoal(target));
	                        target.targetSelector.addGoal(2, new HurtByTargetGoal(target, CreatureEntity.class));
	                        if (target instanceof CreeperEntity) {
	                            target.goalSelector.addGoal(2, new CreeperSwellGoal((CreeperEntity) target));
	                        }
	                        if (target instanceof RabbitEntity) {
	                            target.goalSelector.addGoal(4, new AIEvilAttack((RabbitEntity) target));
	                        }
	                        target.addPotionEffect(new EffectInstance(Effects.GLOWING, 50, 0, false, false, false));
	
	
	                    } else if ((target != null) && message.emote == emotion.CALM) {
	                        // Remove all current goals
	                        target.goalSelector.getRunningGoals().forEach(target.goalSelector::removeGoal);
	                        target.targetSelector.getRunningGoals().forEach(target.targetSelector::removeGoal);
	                        target.goalSelector.tick();
	                        target.targetSelector.tick();
	                        target.setAttackTarget(null);
	                        target.setRevengeTarget(null);
	                        //Disable targeting as a whole
	                        target.targetSelector.disableFlag(Goal.Flag.TARGET);
	                        //Add new goals
	                        target.goalSelector.addGoal(0, new SwimGoal(target));
	                        target.goalSelector.addGoal(5, new RandomWalkingGoal(target, 1.0D));
	                        target.goalSelector.addGoal(6, new LookAtGoal(target, PlayerEntity.class, 6.0F));
	                        target.goalSelector.addGoal(7, new LookRandomlyGoal(target));
	                        target.addPotionEffect(new EffectInstance(Effects.GLOWING, 50, 0, false, false, false));
	
	                    } else if ((target != null) && message.emote == emotion.IGNORE) {
	                        // Remove all current goals
	                        target.goalSelector.getRunningGoals().forEach(target.goalSelector::removeGoal);
	                        target.targetSelector.getRunningGoals().forEach(target.targetSelector::removeGoal);
	                        target.goalSelector.tick();
	                        target.targetSelector.tick();
	                        target.setAttackTarget(null);
	                        target.setRevengeTarget(null);
	                        target.goalSelector.addGoal(0, new SwimGoal(target));
	                        target.goalSelector.addGoal(5, new RandomWalkingGoal(target, 1.0D));
	                        target.goalSelector.addGoal(7, new LookRandomlyGoal(target));
	
	                }
            }
    	}
        );
    }
}