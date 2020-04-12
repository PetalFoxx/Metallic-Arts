package com.crimson.allomancy.command;

import com.crimson.allomancy.network.NetworkHelper;
import com.crimson.allomancy.network.packets.AllomancyCapabilityPacket;
import com.crimson.allomancy.util.AllomancyCapability;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Predicate;

public class AllomancyPowerCommand {

    private static final String[] names = {"none", "iron_misting", "steel_misting", "tin_misting", "pewter_misting", "zinc_misting", "brass_misting", "copper_misting", "bronze_misting"};


    private static Predicate<CommandSource> permissions(int level) {
        return (player) -> player.hasPermissionLevel(level);
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        LiteralArgumentBuilder<CommandSource> root = Commands.literal("allomancy").requires(permissions(0));
        //root.then(Commands.literal("get").requires(permissions(0))
        //        .executes(ctx -> getPower(ctx, false))
        //        .then(Commands.argument("targets", EntityArgument.players())
        //                .executes(ctx -> getPower(ctx, true))));
        root.then(Commands.literal("set").requires(permissions(2))
                .then(Commands.argument("type", AllomancyPowerType.INSTANCE)
                        .executes(ctx -> setPower(ctx, false))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> setPower(ctx, true)))));
        
        root.then(Commands.literal("setferuchemy").requires(permissions(2))
                .then(Commands.argument("type", AllomancyPowerType.INSTANCE)
                        .executes(ctx -> setFeruchemyPower(ctx, false))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> setFeruchemyPower(ctx, true)))));
        
        
        root.then(Commands.literal("remove").requires(permissions(2))
                .then(Commands.argument("type", AllomancyPowerType.INSTANCE)
                        .executes(ctx -> removePower(ctx, false))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> removePower(ctx, true)))));
        
        root.then(Commands.literal("removeferuchemy").requires(permissions(2))
                .then(Commands.argument("type", AllomancyPowerType.INSTANCE)
                        .executes(ctx -> removeFeruchemyPower(ctx, false))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> removeFeruchemyPower(ctx, true)))));


        LiteralCommandNode<CommandSource> command = dispatcher.register(root);

        dispatcher.register(Commands.literal("ap").requires(permissions(0)).redirect(command));
    }




    private static int setPower(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
                setPower(ctx, p);
                i++;
            }
        } else {
            setPower(ctx, ctx.getSource().asPlayer());
            i = 1;
        }
        return i;
    }

    private static int setPower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        String type = ctx.getArgument("type", String.class);
        byte power = powerTypeToByte(type);
        cap.setCanBurn(power, true);
        cap.setBurnStrength(power, 10);
//        for (int i = 0; i < 8; i++) {
//            cap.setMetalBurning(i, false);
//        }
        NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
        ctx.getSource().sendFeedback(new TranslationTextComponent("commands.allomancy.setpower", player.getDisplayName(), names[power + 1]), true);
        return power;

    }

    private static byte powerTypeToByte(String type) {
        for (byte i = 0; i <= 9; i++) {
            if (type.equals(names[i])) {
                i -= 1; //index at -1
                return i;
            }
        }
        return -1;
    }
    
    private static int removePower(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
            	removePower(ctx, p);
                i++;
            }
        } else {
        	removePower(ctx, ctx.getSource().asPlayer());
            i = 1;
        }
        return i;
    }

    private static int removePower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        String type = ctx.getArgument("type", String.class);
        byte power = powerTypeToByte(type);
        cap.setCanBurn(power, false);
        cap.setBurnStrength(power, 0);
//        for (int i = 0; i < 8; i++) {
//            cap.setMetalBurning(i, false);
//        }
        NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
        ctx.getSource().sendFeedback(new TranslationTextComponent("commands.allomancy.setpower", player.getDisplayName(), names[power + 1]), true);
        return power;

    }
    
    
    private static int setFeruchemyPower(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
            	setFeruchemyPower(ctx, p);
                i++;
            }
        } else {
        	setFeruchemyPower(ctx, ctx.getSource().asPlayer());
            i = 1;
        }
        return i;
    }

    private static int setFeruchemyPower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        String type = ctx.getArgument("type", String.class);
        byte power = feruchemyPowerTypeToByte(type);
        cap.setCanStore(power, true);
//        for (int i = 0; i < 8; i++) {
//            cap.setMetalBurning(i, false);
//        }
        NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
        ctx.getSource().sendFeedback(new TranslationTextComponent("commands.allomancy.setpower", player.getDisplayName(), names[power + 1]), true);
        return power;

    }

    private static byte feruchemyPowerTypeToByte(String type) {
        for (byte i = 0; i <= 9; i++) {
            if (type.equals(names[i])) {
                i -= 1; //index at -1
                return i;
            }
        }
        return -1;
    }
    
    private static int removeFeruchemyPower(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
            	removeFeruchemyPower(ctx, p);
                i++;
            }
        } else {
        	removeFeruchemyPower(ctx, ctx.getSource().asPlayer());
            i = 1;
        }
        return i;
    }

    private static int removeFeruchemyPower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        String type = ctx.getArgument("type", String.class);
        byte power = feruchemyPowerTypeToByte(type);
        cap.setCanStore(power, false);
//        for (int i = 0; i < 8; i++) {
//            cap.setMetalBurning(i, false);
//        }
        NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
        ctx.getSource().sendFeedback(new TranslationTextComponent("commands.allomancy.setpower", player.getDisplayName(), names[power + 1]), true);
        return power;

    }

}
