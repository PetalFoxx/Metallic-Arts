package com.crimson.allomancy.network;

import com.crimson.allomancy.network.packets.AllomancyCapabilityPacket;
import com.crimson.allomancy.util.AllomancyCapability;
import com.crimson.allomancy.util.Registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

public class NetworkHelper {

    public static void sendToServer(Object msg) {
        Registry.NETWORK.sendToServer(msg);
    }


    public static void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            Registry.NETWORK.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendTo(Object msg, PacketDistributor.PacketTarget target) {
        Registry.NETWORK.send(target, msg);
    }

    public static void sync(LivingEntity playerIn) {
    	AllomancyCapability cap = AllomancyCapability.forPlayer(playerIn);
    	if(AllomancyCapability.forPlayer(playerIn) != null)
    		sendTo(new AllomancyCapabilityPacket(cap, playerIn.getEntityId()), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerIn));
    }

}
