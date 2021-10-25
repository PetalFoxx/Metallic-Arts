package com.crimson.allomancy.network.packets;

import com.crimson.allomancy.network.NetworkHelper;
import com.crimson.allomancy.util.AllomancyCapability;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;


public class UpdateBurnPacket {

    private int mat;
    private boolean value;

    /**
     * Send request to the server to change the burning state of a metal
     *
     * @param mat   the index of the metal
     * @param value whether or not it is burning
     */
    public UpdateBurnPacket(int mat, boolean value) {
        this.mat = mat;
        this.value = value; // Convert bool to int
    }

    public static void encode(UpdateBurnPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.mat);
        buf.writeBoolean(pkt.value);
    }

    public static UpdateBurnPacket decode(PacketBuffer buf) {
        return new UpdateBurnPacket(buf.readInt(), buf.readBoolean());
    }


    public static void handle(final UpdateBurnPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);

            if (cap.getMetalAmounts(message.mat) > 0) {
                cap.setMetalBurning(message.mat, message.value, player);
            } else {
                cap.setMetalBurning(message.mat, false, player);
            }
            
            //NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player));
            //NetworkHelper.sync(player);
        });
        
        ctx.get().setPacketHandled(true);
    }
}
