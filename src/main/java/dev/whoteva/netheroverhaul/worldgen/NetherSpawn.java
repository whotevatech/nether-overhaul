package dev.whoteva.netheroverhaul.worldgen;

import dev.whoteva.netheroverhaul.ModAttachments;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import dev.whoteva.netheroverhaul.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.Set;

/**
 * Finds a standable nether column, builds a starter camp, and moves the player there.
 */
public final class NetherSpawn {

    private NetherSpawn() {
    }

    public static void sendToNether(ServerPlayer player, boolean firstJoin) {
        ServerLevel nether = player.server.getLevel(Level.NETHER);
        if (nether == null) {
            return;
        }

        BlockPos spawn = findSafe(nether);
        nether.getChunkAt(spawn);
        if (firstJoin && NetherOverhaulConfig.SPAWN_CAMP.get()) {
            NetherStructures.placeSpawnCamp(nether, spawn.below(), nether.getSeed() ^ spawn.asLong());
        } else {
            ensurePlatform(nether, spawn);
        }

        player.teleportTo(
                nether,
                spawn.getX() + 0.5D,
                spawn.getY(),
                spawn.getZ() + 0.5D,
                Set.of(),
                player.getYRot(),
                player.getXRot()
        );
        player.setRespawnPosition(Level.NETHER, spawn, player.getYRot(), true, false);

        if (firstJoin) {
            ModAttachments.markNetherSpawned(player);
            ModAttachments.setCampPos(player, spawn);
            int fireTicks = NetherOverhaulConfig.SPAWN_FIRE_RESISTANCE_TICKS.get();
            if (fireTicks > 0) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, fireTicks, 0, false, true, true));
            }
            if (NetherOverhaulConfig.GIVE_LORE_BOOK.get()) {
                player.getInventory().add(NetherLore.sealedBelowBook());
            }
            if (NetherOverhaulConfig.GIVE_CAMP_COMPASS.get()) {
                player.getInventory().add(campCompass(spawn.below()));
            }
            if (NetherOverhaulConfig.GIVE_FLASK_AT_SPAWN.get()) {
                player.getInventory().add(new ItemStack(ModItems.SEALED_FLASK.get()));
            }
            NetherLore.showTitle(player, "title.netheroverhaul.sealed", "subtitle.netheroverhaul.sealed");
        }
    }

    public static ItemStack campCompass(BlockPos lodestone) {
        ItemStack stack = new ItemStack(Items.COMPASS);
        stack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(Level.NETHER, lodestone)), true));
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.netheroverhaul.camp_compass").withStyle(style -> style.withItalic(false)));
        return stack;
    }

    public static BlockPos findSafe(ServerLevel nether) {
        for (int radius = 0; radius <= 8; radius++) {
            int step = Math.max(8, radius * 16);
            for (int dx = -radius * step; dx <= radius * step; dx += step) {
                for (int dz = -radius * step; dz <= radius * step; dz += step) {
                    if (radius > 0 && Math.abs(dx) != radius * step && Math.abs(dz) != radius * step) {
                        continue;
                    }
                    BlockPos found = scanColumn(nether, dx, dz);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return new BlockPos(0, 80, 0);
    }

    private static BlockPos scanColumn(ServerLevel nether, int x, int z) {
        nether.getChunkAt(new BlockPos(x, 80, z));
        int min = Math.max(nether.getMinBuildHeight() + 10, NetherOverhaulConfig.LAVA_SEA_LEVEL.get() + 6);
        int max = 155;
        for (int y = max; y >= min; y--) {
            BlockPos feet = new BlockPos(x, y, z);
            BlockState ground = nether.getBlockState(feet.below());
            if (!nether.getBlockState(feet).isAir() || !nether.getBlockState(feet.above()).isAir()) {
                continue;
            }
            if (!ground.isSolid() || ground.is(Blocks.LAVA) || ground.is(Blocks.MAGMA_BLOCK) || ground.is(Blocks.BEDROCK)) {
                continue;
            }
            if (nether.getBlockState(feet).is(Blocks.LAVA) || nether.getBlockState(feet.above()).is(Blocks.LAVA)) {
                continue;
            }
            return feet;
        }
        return null;
    }

    private static void ensurePlatform(ServerLevel nether, BlockPos spawn) {
        BlockPos floor = spawn.below();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                nether.setBlockAndUpdate(floor.offset(dx, 0, dz), Blocks.POLISHED_BLACKSTONE.defaultBlockState());
                nether.setBlockAndUpdate(floor.offset(dx, 1, dz), Blocks.AIR.defaultBlockState());
                nether.setBlockAndUpdate(floor.offset(dx, 2, dz), Blocks.AIR.defaultBlockState());
            }
        }
    }
}
