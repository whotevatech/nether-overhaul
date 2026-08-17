package dev.whoteva.netheroverhaul;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, NetherOverhaul.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> RIFT_UNLOCKED =
            ATTACHMENTS.register("rift_unlocked", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .copyOnDeath()
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> NETHER_SPAWNED =
            ATTACHMENTS.register("nether_spawned", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .copyOnDeath()
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BlockPos>> NETHER_CAMP =
            ATTACHMENTS.register("nether_camp", () -> AttachmentType.builder(() -> BlockPos.ZERO)
                    .serialize(BlockPos.CODEC)
                    .copyOnDeath()
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>> LAST_LOCK_MESSAGE =
            ATTACHMENTS.register("last_lock_message", () -> AttachmentType.builder(() -> 0L).build());

    private ModAttachments() {
    }

    public static boolean isUnlocked(ServerPlayer player) {
        return player.getData(RIFT_UNLOCKED);
    }

    public static void setUnlocked(ServerPlayer player, boolean unlocked) {
        player.setData(RIFT_UNLOCKED, unlocked);
        RiftScoreboard.sync(player, unlocked);
    }

    public static boolean hasNetherSpawned(ServerPlayer player) {
        return player.getData(NETHER_SPAWNED);
    }

    public static void markNetherSpawned(ServerPlayer player) {
        player.setData(NETHER_SPAWNED, true);
    }

    public static BlockPos campPos(ServerPlayer player) {
        return player.getData(NETHER_CAMP);
    }

    public static void setCampPos(ServerPlayer player, BlockPos pos) {
        player.setData(NETHER_CAMP, pos.immutable());
    }

    public static boolean tryLockMessage(ServerPlayer player, long gameTime, int cooldownTicks) {
        long last = player.getData(LAST_LOCK_MESSAGE);
        if (gameTime - last < cooldownTicks) {
            return false;
        }
        player.setData(LAST_LOCK_MESSAGE, gameTime);
        return true;
    }
}
