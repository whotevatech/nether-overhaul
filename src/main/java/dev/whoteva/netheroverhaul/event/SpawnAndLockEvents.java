package dev.whoteva.netheroverhaul.event;

import dev.whoteva.netheroverhaul.ModAttachments;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import dev.whoteva.netheroverhaul.worldgen.NetherLore;
import dev.whoteva.netheroverhaul.worldgen.NetherSpawn;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class SpawnAndLockEvents {

    private SpawnAndLockEvents() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!NetherOverhaulConfig.SPAWN_IN_NETHER.get()) {
            return;
        }
        if (ModAttachments.hasNetherSpawned(player)) {
            return;
        }
        NetherSpawn.sendToNether(player, true);
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!NetherOverhaulConfig.SPAWN_IN_NETHER.get()) {
            return;
        }
        if (ModAttachments.hasNetherSpawned(player)) {
            return;
        }
        if (player.level().dimension() != Level.OVERWORLD) {
            return;
        }
        player.server.execute(() -> {
            if (!ModAttachments.hasNetherSpawned(player)) {
                NetherSpawn.sendToNether(player, true);
            }
        });
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!NetherOverhaulConfig.LOCK_OVERWORLD.get()) {
            return;
        }
        if (ModAttachments.isUnlocked(player) || bypassesLock(player)) {
            return;
        }
        if (player.level().dimension() != Level.NETHER) {
            NetherSpawn.sendToNether(player, false);
        }
    }

    @SubscribeEvent
    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!NetherOverhaulConfig.LOCK_OVERWORLD.get()) {
            return;
        }
        if (ModAttachments.isUnlocked(player) || bypassesLock(player)) {
            return;
        }

        ResourceKey<Level> destination = event.getDimension();
        ResourceKey<Level> origin = player.level().dimension();
        if (origin == Level.NETHER && destination == Level.OVERWORLD) {
            event.setCanceled(true);
            if (ModAttachments.tryLockMessage(player, player.level().getGameTime(), NetherOverhaulConfig.LOCK_MESSAGE_COOLDOWN_TICKS.get())) {
                player.playNotifySound(SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.55F, 0.45F);
                NetherLore.showTitle(player, "title.netheroverhaul.locked", "subtitle.netheroverhaul.locked");
                player.displayClientMessage(Component.translatable("message.netheroverhaul.locked"), true);
            }
        }
    }

    public static boolean bypassesLock(ServerPlayer player) {
        if (NetherOverhaulConfig.CREATIVE_BYPASSES_LOCK.get() && player.getAbilities().instabuild) {
            return true;
        }
        return NetherOverhaulConfig.OPERATORS_BYPASS_LOCK.get() && player.hasPermissions(2);
    }
}
