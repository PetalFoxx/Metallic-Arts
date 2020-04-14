package com.crimson.allomancy.network.packets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import com.crimson.allomancy.util.AllomancyUtils;

public class TryPushPullEntity {

    private int entityIDOther;
    private byte direction;
    private float strength;

    /**
     * Send a request to the server to use iron or steel on an entity
     *
     * @param entityIDOther the entity you are requesting the data of
     * @param direction     the direction (1 for push, -1 for pull)
     */
    public TryPushPullEntity(int entityIDOther, byte direction, float strength) {
        this.entityIDOther = entityIDOther;
        this.direction = direction;
        this.strength = strength;

    }

    public static void encode(TryPushPullEntity pkt, PacketBuffer buf) {
        buf.writeInt(pkt.entityIDOther);
        buf.writeByte(pkt.direction);
        buf.writeFloat(pkt.strength);
    }

    public static TryPushPullEntity decode(PacketBuffer buf) {
        return new TryPushPullEntity(buf.readInt(), buf.readByte(), buf.readFloat());
    }


    public static void handle(final TryPushPullEntity message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();

            Entity target = player.world.getEntityByID(message.entityIDOther);
            if (target != null) {
                if (AllomancyUtils.isEntityMetal(target, message.strength)) {
                    // The player moves
                    if (target instanceof IronGolemEntity || target instanceof ItemFrameEntity) {
                        AllomancyUtils.move(message.direction, player, target.getPosition(), message.strength);

                        // Depends if the minecart is filled
                    } else if (target instanceof AbstractMinecartEntity) {
                        if (target.isBeingRidden()) {
                            if (target.isRidingOrBeingRiddenBy(player)) {
                                //no op
                            } else {
                                AllomancyUtils.move(message.direction / 2.0, target, player.getPosition(), message.strength);
                                AllomancyUtils.move(message.direction / 2.0, player, target.getPosition(), message.strength);
                            }
                        } else {
                            AllomancyUtils.move(message.direction, target, player.getPosition(), message.strength);
                        }
                        // The target moves
                    } else if (target instanceof ItemEntity || target instanceof FallingBlockEntity) {
                        AllomancyUtils.move(message.direction / 2.0, target, player.getPosition().down(), message.strength);

                        // Split the difference
                    } else if (target instanceof ProjectileItemEntity) {
                        return;
                    } else {
                        AllomancyUtils.move(message.direction / 2.0, target, player.getPosition(), message.strength);

                        AllomancyUtils.move(message.direction / 2.0, player, target.getPosition(), message.strength);
                    }
                }
            }

        });
    }
}
