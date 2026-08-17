package dev.whoteva.netheroverhaul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * Dummy scoreboard used by FTB Quests / KubeJS: 1 = Overworld rift unlocked.
 */
public final class RiftScoreboard {

    public static final String OBJECTIVE = "netheroverhaul_rift";

    private RiftScoreboard() {
    }

    public static void ensure(MinecraftServer server) {
        Scoreboard board = server.getScoreboard();
        if (board.getObjective(OBJECTIVE) != null) {
            return;
        }
        board.addObjective(
                OBJECTIVE,
                ObjectiveCriteria.DUMMY,
                Component.literal("Rift").withStyle(ChatFormatting.LIGHT_PURPLE),
                ObjectiveCriteria.RenderType.INTEGER,
                false,
                null
        );
    }

    public static void sync(ServerPlayer player, boolean unlocked) {
        ensure(player.server);
        Objective objective = player.server.getScoreboard().getObjective(OBJECTIVE);
        if (objective != null) {
            player.server.getScoreboard().getOrCreatePlayerScore(player, objective).set(unlocked ? 1 : 0);
        }
    }
}
