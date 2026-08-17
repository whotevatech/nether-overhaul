package dev.whoteva.netheroverhaul.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.whoteva.netheroverhaul.ModAttachments;
import dev.whoteva.netheroverhaul.item.ModItems;
import dev.whoteva.netheroverhaul.worldgen.NetherSpawn;
import dev.whoteva.netheroverhaul.worldgen.NetherStructureLayout;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ModCommands {

    private ModCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("netheroverhaul")
                .then(Commands.literal("where")
                        .executes(ctx -> where(ctx.getSource().getPlayerOrException())))
                .then(Commands.literal("unlock")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> setUnlocked(ctx, ctx.getSource().getPlayerOrException(), true))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> setUnlocked(ctx, EntityArgument.getPlayer(ctx, "player"), true))))
                .then(Commands.literal("lock")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> setUnlocked(ctx, ctx.getSource().getPlayerOrException(), false))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> setUnlocked(ctx, EntityArgument.getPlayer(ctx, "player"), false))))
                .then(Commands.literal("spawn")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> {
                            NetherSpawn.sendToNether(ctx.getSource().getPlayerOrException(), false);
                            return 1;
                        }))
                .then(Commands.literal("locate")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> locate(ctx.getSource().getPlayerOrException(), null))
                        .then(Commands.literal("tower").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.TOWER)))
                        .then(Commands.literal("hamlet").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.HAMLET)))
                        .then(Commands.literal("pit").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.PIT)))
                        .then(Commands.literal("nest").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.NEST)))
                        .then(Commands.literal("forge").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.FORGE)))
                        .then(Commands.literal("shrine").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.SHRINE)))
                        .then(Commands.literal("well").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.WELL)))
                        .then(Commands.literal("dungeon").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.DUNGEON)))
                        .then(Commands.literal("ring").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.RING)))
                        .then(Commands.literal("blaze").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.BLAZE)))
                        .then(Commands.literal("firetower").executes(ctx -> locate(ctx.getSource().getPlayerOrException(), NetherStructureLayout.Kind.FIRE_TOWER))))
                .then(Commands.literal("key")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> give(ctx.getSource().getPlayerOrException(), new ItemStack(ModItems.RIFT_KEY.get()))))
                .then(Commands.literal("kit")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> kit(ctx.getSource().getPlayerOrException()))));
    }

    private static int setUnlocked(CommandContext<CommandSourceStack> ctx, ServerPlayer player, boolean unlocked) {
        ModAttachments.setUnlocked(player, unlocked);
        ctx.getSource().sendSuccess(() -> Component.translatable(
                unlocked ? "command.netheroverhaul.unlocked" : "command.netheroverhaul.locked",
                player.getName()), true);
        return 1;
    }

    private static int where(ServerPlayer player) {
        boolean unlocked = ModAttachments.isUnlocked(player);
        player.displayClientMessage(Component.translatable(
                unlocked ? "command.netheroverhaul.where_open" : "command.netheroverhaul.where_sealed"), false);
        NetherStructureLayout.Hit hit = NetherStructureLayout.nearest(player.blockPosition(), null, 48);
        if (hit != null) {
            player.displayClientMessage(Component.literal(hit.describe(player.blockPosition())), false);
        }
        return 1;
    }

    private static int locate(ServerPlayer player, NetherStructureLayout.Kind kind) {
        NetherStructureLayout.Hit hit = NetherStructureLayout.nearest(player.blockPosition(), kind, 64);
        if (hit == null) {
            player.displayClientMessage(Component.translatable("message.netheroverhaul.seeker_none"), false);
            return 0;
        }
        player.displayClientMessage(Component.literal(hit.describe(player.blockPosition())
                + " @ " + hit.origin().getX() + " " + hit.origin().getZ()), false);
        return 1;
    }

    private static int kit(ServerPlayer player) {
        give(player, new ItemStack(ModItems.RIFT_KEY.get()));
        give(player, new ItemStack(ModItems.RIFT_SEEKER.get()));
        give(player, new ItemStack(ModItems.RECALL_CHARM.get()));
        give(player, new ItemStack(ModItems.SEALED_FLASK.get()));
        give(player, new ItemStack(ModItems.GLOWSTONE_SEED.get(), 8));
        give(player, new ItemStack(ModItems.QUICK_SOUL_SAND.get(), 4));
        return 1;
    }

    private static int give(ServerPlayer player, ItemStack stack) {
        player.addItem(stack);
        return 1;
    }
}
