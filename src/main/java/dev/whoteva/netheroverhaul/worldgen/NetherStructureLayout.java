package dev.whoteva.netheroverhaul.worldgen;

import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

/**
 * Deterministic structure grid shared by worldgen, the Rift Seeker, and commands.
 */
public final class NetherStructureLayout {

    public enum Kind {
        TOWER("watchtower"),
        HAMLET("hamlet"),
        PIT("wither pit"),
        NEST("ghast nest"),
        FORGE("basalt forge"),
        SHRINE("quartz shrine"),
        WELL("obsidian well"),
        DUNGEON("nether dungeon"),
        RING("fire ring"),
        BLAZE("blaze tower"),
        FIRE_TOWER("fire tower");

        private final String label;

        Kind(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public record Hit(Kind kind, BlockPos origin, int chunkDist) {
        public String describe(BlockPos from) {
            int dx = origin.getX() - from.getX();
            int dz = origin.getZ() - from.getZ();
            double dist = Math.sqrt((double) dx * dx + (double) dz * dz);
            return String.format(Locale.ROOT, "%s · %dm · %s", kind.label(), (int) Math.round(dist), heading(dx, dz));
        }
    }

    private NetherStructureLayout() {
    }

    public static Kind kindAt(ChunkPos pos) {
        if (matches(pos, NetherOverhaulConfig.TOWER_SPACING.get(), 2, 3)) {
            return Kind.TOWER;
        }
        if (matches(pos, NetherOverhaulConfig.HAMLET_SPACING.get(), 1, 4)) {
            return Kind.HAMLET;
        }
        if (matches(pos, NetherOverhaulConfig.RUIN_SPACING.get(), 5, 1)
                && Math.floorMod(pos.x * 341 + pos.z * 913, 64) < 40) {
            return Kind.PIT;
        }
        if (matches(pos, NetherOverhaulConfig.NEST_SPACING.get(), 6, 7)) {
            return Kind.NEST;
        }
        if (matches(pos, NetherOverhaulConfig.FORGE_SPACING.get(), 3, 8)) {
            return Kind.FORGE;
        }
        if (matches(pos, NetherOverhaulConfig.SHRINE_SPACING.get(), 4, 9)) {
            return Kind.SHRINE;
        }
        if (matches(pos, 15, 8, 2)) {
            return Kind.WELL;
        }
        if (matches(pos, 16, 3, 6)) {
            return Kind.DUNGEON;
        }
        if (matches(pos, 9, 0, 5)) {
            return Kind.RING;
        }
        if (matches(pos, 12, 7, 4)) {
            return Kind.BLAZE;
        }
        if (matches(pos, 8, 2, 1)) {
            return Kind.FIRE_TOWER;
        }
        return null;
    }

    public static Hit nearest(BlockPos from, Kind filter, int rangeChunks) {
        ChunkPos origin = new ChunkPos(from);
        Hit best = null;
        int bestScore = Integer.MAX_VALUE;
        for (int dx = -rangeChunks; dx <= rangeChunks; dx++) {
            for (int dz = -rangeChunks; dz <= rangeChunks; dz++) {
                ChunkPos chunk = new ChunkPos(origin.x + dx, origin.z + dz);
                Kind kind = kindAt(chunk);
                if (kind == null || (filter != null && kind != filter)) {
                    continue;
                }
                int score = dx * dx + dz * dz;
                if (score < bestScore) {
                    bestScore = score;
                    best = new Hit(kind, new BlockPos(chunk.getMinBlockX() + 8, from.getY(), chunk.getMinBlockZ() + 8), Mth.ceil(Math.sqrt(score)));
                }
            }
        }
        return best;
    }

    public static String heading(int dx, int dz) {
        if (Math.abs(dx) < 12 && Math.abs(dz) < 12) {
            return "here";
        }
        Vec3 vec = new Vec3(dx, 0, dz).normalize();
        double angle = Math.toDegrees(Math.atan2(-vec.x, vec.z));
        int index = Mth.floor((angle + 22.5D) / 45.0D) & 7;
        return switch (index) {
            case 0 -> "south";
            case 1 -> "southwest";
            case 2 -> "west";
            case 3 -> "northwest";
            case 4 -> "north";
            case 5 -> "northeast";
            case 6 -> "east";
            default -> "southeast";
        };
    }

    private static boolean matches(ChunkPos pos, int spacing, int xMod, int zMod) {
        return Math.floorMod(pos.x, spacing) == xMod && Math.floorMod(pos.z, spacing) == zMod;
    }
}
