package dev.whoteva.netheroverhaul.event;

import dev.whoteva.netheroverhaul.ModAttachments;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import dev.whoteva.netheroverhaul.RiftScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

public final class QolEvents {

    private QolEvents() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        RiftScoreboard.ensure(event.getServer());
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RiftScoreboard.sync(player, ModAttachments.isUnlocked(player));
        }
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (shouldKeep(event.getEntity() instanceof ServerPlayer player ? player : null)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onXp(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && shouldKeep(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Player original = event.getOriginal();
        if (!(original instanceof ServerPlayer previous)) {
            return;
        }
        if (!shouldKeep(previous)) {
            return;
        }
        player.getInventory().replaceWith(previous.getInventory());
        player.experienceLevel = previous.experienceLevel;
        player.experienceProgress = previous.experienceProgress;
        player.totalExperience = previous.totalExperience;
    }

    private static boolean shouldKeep(ServerPlayer player) {
        if (player == null || !NetherOverhaulConfig.KEEP_INVENTORY_UNTIL_UNLOCKED.get()) {
            return false;
        }
        return !ModAttachments.isUnlocked(player);
    }
}
