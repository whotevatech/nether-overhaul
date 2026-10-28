package dev.whoteva.netheroverhaul.worldgen;

import dev.whoteva.netheroverhaul.ModLoot;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import dev.whoteva.netheroverhaul.block.GlowstoneCropBlock;
import dev.whoteva.netheroverhaul.block.ModBlocks;
import dev.whoteva.netheroverhaul.entity.ModEntities;
import dev.whoteva.netheroverhaul.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.server.level.ServerLevel;

public final class NetherStructures {

    private NetherStructures() {
    }

    public static void placeSpawnCamp(WorldGenLevel level, BlockPos origin, long lootSeed) {
        int hut = 4;
        for (int x = -8; x <= 11; x++) {
            for (int z = -8; z <= 8; z++) {
                set(level, origin.offset(x, -1, z), Blocks.BLACKSTONE.defaultBlockState());
                boolean inside = Math.abs(x) <= hut && Math.abs(z) <= hut;
                boolean path = (x == -1 || x == 0) && z >= 5 && z <= 7;
                BlockState floor = inside
                        ? (x == 0 && z == 0 ? Blocks.LODESTONE.defaultBlockState() : Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState())
                        : (path ? Blocks.POLISHED_BLACKSTONE.defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState());
                set(level, origin.offset(x, 0, z), floor);
                for (int y = 1; y <= 6; y++) {
                    set(level, origin.offset(x, y, z), Blocks.AIR.defaultBlockState());
                }
            }
        }

        for (int x = -hut; x <= hut; x++) {
            for (int z = -hut; z <= hut; z++) {
                boolean edge = Math.abs(x) == hut || Math.abs(z) == hut;
                if (!edge) {
                    continue;
                }
                boolean corner = Math.abs(x) == hut && Math.abs(z) == hut;
                boolean door = z == hut && (x == 0 || x == -1);
                boolean window = !corner && !door && (
                        (z == -hut && (x == -2 || x == 0 || x == 2))
                                || (z == hut && (x == -3 || x == 2))
                                || (x == hut && (z == -2 || z == 0 || z == 2))
                                || (x == -hut && (z == -2 || z == 0 || z == 2)));
                for (int y = 1; y <= 4; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (door && y <= 2) {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    } else if (window && y == 2) {
                        set(level, pos, Blocks.IRON_BARS.defaultBlockState());
                    } else if (corner) {
                        set(level, pos, Blocks.CRIMSON_STEM.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y));
                    } else if (y == 4) {
                        set(level, pos, Blocks.GILDED_BLACKSTONE.defaultBlockState());
                    } else if (door && y == 3) {
                        set(level, pos, Blocks.GILDED_BLACKSTONE.defaultBlockState());
                    } else {
                        set(level, pos, brick(x, y, z));
                    }
                }
            }
        }

        for (int x = -hut; x <= hut; x++) {
            for (int z = -hut; z <= hut; z++) {
                boolean glow = Math.abs(x) <= 1 && Math.abs(z) <= 1;
                set(level, origin.offset(x, 5, z), glow ? Blocks.GLOWSTONE.defaultBlockState() : Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
            }
        }
        for (int x = -4; x <= 4; x++) {
            stair(level, origin.offset(x, 5, 5), Direction.SOUTH);
            stair(level, origin.offset(x, 5, -5), Direction.NORTH);
        }
        for (int z = -4; z <= 4; z++) {
            stair(level, origin.offset(5, 5, z), Direction.EAST);
            stair(level, origin.offset(-5, 5, z), Direction.WEST);
        }

        lantern(level, origin.offset(0, 4, 0), true);
        placeDoor(level, origin.offset(-1, 1, hut), Direction.SOUTH, DoorHingeSide.LEFT);
        placeDoor(level, origin.offset(0, 1, hut), Direction.SOUTH, DoorHingeSide.RIGHT);
        stair(level, origin.offset(-1, 0, 5), Direction.SOUTH);
        stair(level, origin.offset(0, 0, 5), Direction.SOUTH);

        set(level, origin.offset(-3, 1, -3), Blocks.CRAFTING_TABLE.defaultBlockState());
        set(level, origin.offset(-2, 1, -3), Blocks.STONECUTTER.defaultBlockState().setValue(StonecutterBlock.FACING, Direction.SOUTH));
        if (NetherOverhaulConfig.GIVE_LORE_BOOK.get()) {
            placeLectern(level, origin.offset(-1, 1, -3), Direction.SOUTH, NetherLore.sealedBelowBook());
        }
        set(level, origin.offset(1, 1, -3), Blocks.FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.SOUTH));
        placeChest(level, origin.offset(2, 1, -3), lootSeed, ModLoot.SPAWN_CAMP, Direction.SOUTH);
        placeSign(level, origin.offset(1, 2, -3), Direction.SOUTH, "CAMP", "Lodestone", "in the floor", "");
        set(level, origin.offset(3, 1, -3), Blocks.SMOKER.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.SOUTH));

        set(level, origin.offset(-3, 1, -1), Blocks.GRINDSTONE.defaultBlockState()
                .setValue(GrindstoneBlock.FACING, Direction.EAST)
                .setValue(GrindstoneBlock.FACE, AttachFace.FLOOR));
        set(level, origin.offset(-3, 1, 1), Blocks.ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, Direction.EAST));
        placeOptionalSleepingBag(level, origin.offset(-3, 1, 2), Direction.EAST);
        set(level, origin.offset(-3, 1, 3), Blocks.WHITE_WOOL.defaultBlockState());
        set(level, origin.offset(-2, 1, 3), Blocks.WHITE_WOOL.defaultBlockState());
        set(level, origin.offset(-3, 2, 3), Blocks.WHITE_CARPET.defaultBlockState());
        set(level, origin.offset(-2, 2, 3), Blocks.WHITE_CARPET.defaultBlockState());

        set(level, origin.offset(3, 1, -2), Blocks.POTTED_CRIMSON_FUNGUS.defaultBlockState());
        set(level, origin.offset(3, 1, -1), Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.WEST));
        int charges = Math.max(0, Math.min(4, NetherOverhaulConfig.SPAWN_ANCHOR_CHARGES.get()));
        set(level, origin.offset(3, 1, 0), Blocks.RESPAWN_ANCHOR.defaultBlockState().setValue(RespawnAnchorBlock.CHARGE, charges));
        set(level, origin.offset(3, 1, 1), waterCauldron());
        set(level, origin.offset(-3, 1, 0), waterCauldron());
        set(level, origin.offset(1, 1, 3), Blocks.SMITHING_TABLE.defaultBlockState());
        set(level, origin.offset(3, 1, 3), Blocks.COMPOSTER.defaultBlockState().setValue(ComposterBlock.LEVEL, 4));

        BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.NORTH);
        for (int y = 1; y <= 4; y++) {
            set(level, origin.offset(2, y, 3), ladder);
        }
        set(level, origin.offset(2, 5, 3), Blocks.CRIMSON_TRAPDOOR.defaultBlockState()
                .setValue(TrapDoorBlock.FACING, Direction.NORTH)
                .setValue(TrapDoorBlock.HALF, Half.TOP)
                .setValue(TrapDoorBlock.OPEN, true));
        set(level, origin.offset(-4, 6, -4), Blocks.NETHER_BRICKS.defaultBlockState());
        set(level, origin.offset(-4, 7, -4), Blocks.SOUL_CAMPFIRE.defaultBlockState()
                .setValue(CampfireBlock.LIT, true)
                .setValue(CampfireBlock.FACING, Direction.SOUTH));

        set(level, origin.offset(-2, 1, 5), Blocks.CRIMSON_FENCE.defaultBlockState());
        set(level, origin.offset(-2, 2, 5), Blocks.CRIMSON_FENCE.defaultBlockState());
        lantern(level, origin.offset(-2, 3, 5), false);
        set(level, origin.offset(0, 1, 7), Blocks.SOUL_CAMPFIRE.defaultBlockState()
                .setValue(CampfireBlock.LIT, true)
                .setValue(CampfireBlock.FACING, Direction.NORTH));
        set(level, origin.offset(3, 1, 6), Blocks.ORANGE_BANNER.defaultBlockState().setValue(BannerBlock.ROTATION, 8));
        post(level, origin.offset(-7, 1, -7));
        post(level, origin.offset(7, 1, -7));
        post(level, origin.offset(-7, 1, 7));

        for (int x = 6; x <= 10; x++) {
            for (int z = -3; z <= 3; z++) {
                boolean fence = x == 6 || x == 10 || z == -3 || z == 3;
                boolean gate = x == 6 && z == 0;
                if (fence && gate) {
                    set(level, origin.offset(x, 1, z), Blocks.CRIMSON_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.WEST));
                } else if (fence) {
                    set(level, origin.offset(x, 1, z), Blocks.NETHER_BRICK_FENCE.defaultBlockState());
                } else {
                    set(level, origin.offset(x, 0, z), Blocks.SOUL_SAND.defaultBlockState());
                    if (x == 8 && z == 0) {
                        set(level, origin.offset(x, 1, z), ModBlocks.GLOWSTONE_CROP.get().defaultBlockState().setValue(GlowstoneCropBlock.AGE, 3));
                    } else {
                        set(level, origin.offset(x, 1, z), Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 3));
                    }
                }
            }
        }
        lantern(level, origin.offset(10, 2, 0), false);
        connect(level, origin, -8, -1, -8, 11, 8, 8);
    }

    public static void placeWatchtower(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        int height = 15 + random.nextInt(6);
        int radius = 3;
        preparePad(level, origin, radius + 1, 2, Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), false);
        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    boolean edge = Math.abs(x) == radius || Math.abs(z) == radius;
                    boolean corner = Math.abs(x) == radius && Math.abs(z) == radius;
                    BlockPos pos = origin.offset(x, y, z);
                    if (y == 0) {
                        set(level, pos, Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
                    } else if (edge) {
                        boolean slit = !corner && y % 5 == 3 && (x == 0 || z == 0) && !(z == -radius && x == 0);
                        if (slit) {
                            set(level, pos, Blocks.IRON_BARS.defaultBlockState());
                        } else if (corner || y % 5 == 0) {
                            set(level, pos, y % 5 == 0 ? Blocks.RED_NETHER_BRICKS.defaultBlockState() : Blocks.POLISHED_BLACKSTONE.defaultBlockState());
                        } else {
                            set(level, pos, brick(x, y, z));
                        }
                    } else if (y % 5 == 0 && !(x == 0 && z == -radius + 1)) {
                        set(level, pos, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB.defaultBlockState());
                    } else {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
            set(level, origin.offset(0, y, -radius + 1), Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH));
            if (y > 1 && y % 5 == 1) {
                lantern(level, origin.offset(1, y, 1), false);
            }
        }
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                boolean edge = Math.abs(x) == radius || Math.abs(z) == radius;
                if (edge && ((x + z) % 2 == 0 || (Math.abs(x) == radius && Math.abs(z) == radius))) {
                    set(level, origin.offset(x, height, z), Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState());
                } else {
                    set(level, origin.offset(x, height, z), Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
                }
            }
        }
        set(level, origin.offset(0, height, -radius + 1), Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH));
        placeChest(level, origin.offset(2, height + 1, 1), lootSeed, ModLoot.WATCHTOWER, Direction.WEST);
        lantern(level, origin.offset(-2, height + 1, -2), false);
        set(level, origin.offset(1, height - 2, radius + 1), Blocks.RED_WALL_BANNER.defaultBlockState().setValue(WallBannerBlock.FACING, Direction.SOUTH));
        connect(level, origin, -radius - 1, 0, -radius - 1, radius + 1, height + 2, radius + 2);
    }

    public static void placeHamlet(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        preparePad(level, origin, 12, 6, Blocks.BLACKSTONE.defaultBlockState(), true);
        for (int i = 0; i < 3; i++) {
            int ox = (i - 1) * 7;
            int oz = i == 1 ? 6 : -1;
            placeHut(level, origin.offset(ox, 0, oz), random, i);
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (Math.abs(x) == 1 && Math.abs(z) == 1) {
                    set(level, origin.offset(x, 1, z), Blocks.CRIMSON_FENCE.defaultBlockState());
                    set(level, origin.offset(x, 2, z), Blocks.CRIMSON_FENCE.defaultBlockState());
                }
                set(level, origin.offset(x, 3, z), Blocks.CRIMSON_SLAB.defaultBlockState());
            }
        }
        placeChest(level, origin.offset(0, 1, 2), lootSeed, ModLoot.HAMLET, Direction.NORTH);
        lantern(level, origin.offset(0, 2, 0), true);
        placeStandingSign(level, origin.offset(2, 1, 0), 8, "HAMLET", "Wart", "and trade", "");

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos farm = origin.offset(x, 0, z - 8);
                boolean fence = Math.abs(x) == 2 || Math.abs(z) == 2;
                boolean gate = z == -2 && x == 0;
                if (fence && gate) {
                    set(level, farm.above(), Blocks.CRIMSON_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.SOUTH));
                } else if (fence) {
                    set(level, farm.above(), Blocks.NETHER_BRICK_FENCE.defaultBlockState());
                } else {
                    set(level, farm, Blocks.SOUL_SAND.defaultBlockState());
                    if (x == 0 && z == 0) {
                        set(level, farm.above(), ModBlocks.GLOWSTONE_CROP.get().defaultBlockState().setValue(GlowstoneCropBlock.AGE, 3));
                    } else {
                        set(level, farm.above(), Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 3));
                    }
                }
            }
        }
        connect(level, origin, -12, -1, -12, 12, 8, 12);
    }

    public static void placeWitherPit(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        int depth = 12 + random.nextInt(4);
        int radius = 4;
        for (int y = 1; y <= 3; y++) {
            for (int x = -radius - 1; x <= radius + 1; x++) {
                for (int z = -radius - 1; z <= radius + 1; z++) {
                    set(level, origin.offset(x, y, z), Blocks.AIR.defaultBlockState());
                }
            }
        }
        for (int y = 0; y >= -depth; y--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    boolean wall = Math.abs(x) == radius || Math.abs(z) == radius || y == -depth;
                    BlockPos pos = origin.offset(x, y, z);
                    if (wall) {
                        boolean accent = Math.floorMod(x * 3 + y + z * 5, 7) == 0;
                        set(level, pos, accent ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : Blocks.REINFORCED_DEEPSLATE.defaultBlockState());
                    } else {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
            set(level, origin.offset(0, y, -radius + 1), Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH));
        }
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x) == radius || Math.abs(z) == radius) {
                    if ((x + z) % 2 == 0) {
                        set(level, origin.offset(x, 1, z), Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState());
                    }
                }
            }
        }
        lantern(level, origin.offset(-radius, 2, 0), false);
        lantern(level, origin.offset(radius, 2, 0), false);
        set(level, origin.offset(0, 1, radius), Blocks.WITHER_SKELETON_SKULL.defaultBlockState());
        set(level, origin.offset(-2, 1, radius), Blocks.SKELETON_SKULL.defaultBlockState());
        for (int i = 3; i <= 8; i += 2) {
            set(level, origin.offset(0, -depth + i, 0), Blocks.COBWEB.defaultBlockState());
            set(level, origin.offset(1, -depth + i, 1), Blocks.COBWEB.defaultBlockState());
        }
        for (int x = -radius + 1; x <= radius - 1; x++) {
            for (int z = -radius + 1; z <= radius - 1; z++) {
                set(level, origin.offset(x, -depth, z), Blocks.SOUL_SOIL.defaultBlockState());
            }
        }
        set(level, origin.offset(2, -depth, 2), Blocks.SOUL_SOIL.defaultBlockState());
        set(level, origin.offset(2, -depth + 1, 2), Blocks.SOUL_FIRE.defaultBlockState());
        set(level, origin.offset(-2, -depth + 1, -1), Blocks.BONE_BLOCK.defaultBlockState());
        placeChest(level, origin.offset(-2, -depth + 1, 2), lootSeed, ModLoot.WITHER_PIT, Direction.EAST);
        connect(level, origin, -radius - 1, -depth, -radius - 1, radius + 1, 3, radius + 1);
    }

    public static void placeGhastNest(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        preparePad(level, origin, 4, 5, Blocks.NETHER_BRICKS.defaultBlockState(), false);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                int chebyshev = Math.max(Math.abs(x), Math.abs(z));
                if (chebyshev == 3) {
                    set(level, origin.offset(x, 0, z), Blocks.NETHER_BRICK_SLAB.defaultBlockState());
                } else if (chebyshev == 0) {
                    set(level, origin.offset(x, 0, z), Blocks.BONE_BLOCK.defaultBlockState());
                } else {
                    set(level, origin.offset(x, 0, z), Blocks.CRYING_OBSIDIAN.defaultBlockState());
                    set(level, origin.offset(x, 1, z), Blocks.LIGHT_GRAY_CARPET.defaultBlockState());
                }
            }
        }
        for (int[] post : new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}}) {
            set(level, origin.offset(post[0], 1, post[1]), Blocks.BONE_BLOCK.defaultBlockState());
            set(level, origin.offset(post[0], 2, post[1]), Blocks.BONE_BLOCK.defaultBlockState());
            set(level, origin.offset(post[0], 3, post[1]), Blocks.NETHER_BRICK_FENCE.defaultBlockState());
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    set(level, origin.offset(x, 4, z), Blocks.NETHER_BRICK_SLAB.defaultBlockState());
                }
            }
        }
        set(level, origin.offset(0, 4, 0), Blocks.NETHER_BRICKS.defaultBlockState());
        set(level, origin.offset(0, 3, 0), Blocks.CHAIN.defaultBlockState().setValue(ChainBlock.AXIS, Direction.Axis.Y));
        lantern(level, origin.offset(0, 2, 0), true);
        set(level, origin.offset(3, 0, 0), Blocks.NETHER_BRICKS.defaultBlockState());
        placeChest(level, origin.offset(3, 1, 0), lootSeed, ModLoot.GHAST_NEST, Direction.EAST);
        if (level.getBlockState(origin.offset(-1, 2, -2)).isAir()) {
            placeFrame(level, origin.offset(-1, 2, -2), Direction.WEST, new ItemStack(Items.GHAST_TEAR));
        }
        if (random.nextBoolean()) {
            set(level, origin.offset(2, 1, -3), Blocks.SOUL_LANTERN.defaultBlockState());
        }
        connect(level, origin, -4, 0, -4, 4, 5, 4);
    }

    public static void placeBasaltForge(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        preparePad(level, origin, 3, 5, Blocks.POLISHED_BASALT.defaultBlockState(), false);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                set(level, origin.offset(x, 0, z), Blocks.POLISHED_BASALT.defaultBlockState());
                boolean wall = Math.abs(x) == 3 || Math.abs(z) == 3;
                boolean door = z == 3 && Math.abs(x) <= 1;
                if (wall && !door) {
                    BlockState column = Math.abs(x) == 3 && Math.abs(z) == 3
                            ? Blocks.POLISHED_BASALT.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                            : Blocks.BASALT.defaultBlockState();
                    set(level, origin.offset(x, 1, z), column);
                    set(level, origin.offset(x, 2, z), column);
                    if ((x == 0 || z == 0) && random.nextBoolean()) {
                        set(level, origin.offset(x, 2, z), Blocks.IRON_BARS.defaultBlockState());
                    }
                    set(level, origin.offset(x, 3, z), Blocks.BLACKSTONE.defaultBlockState());
                }
            }
        }
        set(level, origin.offset(-2, 0, 0), Blocks.MAGMA_BLOCK.defaultBlockState());
        set(level, origin.offset(2, 0, 0), Blocks.MAGMA_BLOCK.defaultBlockState());
        set(level, origin.offset(-2, 1, -2), Blocks.BLAST_FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.SOUTH));
        set(level, origin.offset(-1, 1, -2), Blocks.LAVA_CAULDRON.defaultBlockState());
        set(level, origin.offset(2, 1, -2), Blocks.ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, Direction.WEST));
        set(level, origin.offset(2, 1, -1), Blocks.GRINDSTONE.defaultBlockState()
                .setValue(GrindstoneBlock.FACING, Direction.WEST)
                .setValue(GrindstoneBlock.FACE, AttachFace.FLOOR));
        set(level, origin.offset(2, 1, 1), Blocks.SMITHING_TABLE.defaultBlockState());
        placeChest(level, origin.offset(-2, 1, 1), lootSeed, ModLoot.BASALT_FORGE, Direction.EAST);
        set(level, origin.offset(0, 4, -3), Blocks.BASALT.defaultBlockState());
        set(level, origin.offset(0, 5, -3), Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, true));
        lantern(level, origin.offset(-3, 3, 0), false);
        lantern(level, origin.offset(3, 3, 0), false);
        connect(level, origin, -3, 0, -3, 3, 5, 3);
    }

    public static void placeQuartzShrine(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        preparePad(level, origin, 3, 7, Blocks.SMOOTH_QUARTZ.defaultBlockState(), false);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, origin.offset(x, 0, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
            }
        }
        for (int[] pillar : new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}}) {
            for (int y = 1; y <= 4; y++) {
                set(level, origin.offset(pillar[0], y, pillar[1]), Blocks.QUARTZ_PILLAR.defaultBlockState());
            }
            set(level, origin.offset(pillar[0], 5, pillar[1]), Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState());
            set(level, origin.offset(pillar[0], 6, pillar[1]), Blocks.END_ROD.defaultBlockState().setValue(EndRodBlock.FACING, Direction.UP));
        }
        set(level, origin, Blocks.CRYING_OBSIDIAN.defaultBlockState());
        set(level, origin.above(), Blocks.AMETHYST_BLOCK.defaultBlockState());
        set(level, origin.offset(0, 1, -1), Blocks.PURPLE_CANDLE.defaultBlockState()
                .setValue(CandleBlock.CANDLES, 2)
                .setValue(CandleBlock.LIT, true));
        placeLectern(level, origin.offset(-1, 1, 0), Direction.EAST, NetherLore.shrineNote());
        placeChest(level, origin.offset(3, 1, 0), lootSeed, ModLoot.QUARTZ_SHRINE, Direction.WEST);
        connect(level, origin, -3, 0, -3, 3, 7, 3);
    }

    public static void placeObsidianWell(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        int radius = 2;
        for (int x = -radius - 1; x <= radius + 1; x++) {
            for (int z = -radius - 1; z <= radius + 1; z++) {
                set(level, origin.offset(x, -1, z), Blocks.OBSIDIAN.defaultBlockState());
                set(level, origin.offset(x, 0, z), Blocks.OBSIDIAN.defaultBlockState());
                set(level, origin.offset(x, 1, z), Blocks.AIR.defaultBlockState());
            }
        }
        for (int y = 0; y >= -7; y--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    boolean rim = Math.abs(x) == radius || Math.abs(z) == radius;
                    if (y == -7) {
                        set(level, origin.offset(x, y, z), Blocks.OBSIDIAN.defaultBlockState());
                    } else if (rim) {
                        boolean corner = Math.abs(x) == radius && Math.abs(z) == radius;
                        set(level, origin.offset(x, y, z), (corner && y >= -1) ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : Blocks.OBSIDIAN.defaultBlockState());
                    } else {
                        set(level, origin.offset(x, y, z), Blocks.AIR.defaultBlockState());
                    }
                }
            }
            if (y > -7) {
                set(level, origin.offset(0, y, -1), Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH));
            }
        }
        set(level, origin.offset(0, -6, 0), Blocks.WATER.defaultBlockState());
        set(level, origin.offset(0, 1, -2), Blocks.OBSIDIAN.defaultBlockState());
        lantern(level, origin.offset(0, 2, -2), false);
        stair(level, origin.offset(-1, 0, 3), Direction.NORTH);
        stair(level, origin.offset(0, 0, 3), Direction.NORTH);
        stair(level, origin.offset(1, 0, 3), Direction.NORTH);
        placeChest(level, origin.offset(3, 1, 0), lootSeed, ModLoot.WELL, Direction.WEST);
        set(level, origin.offset(3, 0, 0), Blocks.OBSIDIAN.defaultBlockState());
        if (random.nextBoolean()) {
            set(level, origin.offset(-3, 1, 0), Blocks.CRYING_OBSIDIAN.defaultBlockState());
        }
        connect(level, origin, -3, -7, -3, 3, 2, 3);
    }

    public static void placeNetherDungeon(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        preparePad(level, origin, 5, 1, Blocks.NETHER_BRICKS.defaultBlockState(), false);
        int radius = 4;
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    boolean wall = Math.abs(x) == radius || Math.abs(z) == radius || y == 0 || y == 4;
                    BlockState state = wall ? brick(x, y, z) : Blocks.AIR.defaultBlockState();
                    if (y == 0 && Math.abs(x) <= 1 && Math.abs(z) <= 1) {
                        state = Blocks.RED_NETHER_BRICKS.defaultBlockState();
                    }
                    if (y == 4 && wall && Math.abs(x) != radius && Math.abs(z) != radius) {
                        state = Blocks.NETHER_BRICKS.defaultBlockState();
                    }
                    set(level, origin.offset(x, y, z), state);
                }
            }
        }
        set(level, origin.offset(0, 1, radius), Blocks.AIR.defaultBlockState());
        set(level, origin.offset(0, 2, radius), Blocks.AIR.defaultBlockState());
        stair(level, origin.offset(-1, 0, radius + 1), Direction.NORTH);
        stair(level, origin.offset(0, 0, radius + 1), Direction.NORTH);
        stair(level, origin.offset(1, 0, radius + 1), Direction.NORTH);
        set(level, origin.offset(radius, 2, 0), Blocks.IRON_BARS.defaultBlockState());
        set(level, origin.offset(-radius, 2, 0), Blocks.IRON_BARS.defaultBlockState());
        lantern(level, origin.offset(-radius + 1, 3, -radius + 1), true);
        lantern(level, origin.offset(radius - 1, 3, -radius + 1), true);
        set(level, origin.offset(radius - 1, 3, radius - 1), Blocks.COBWEB.defaultBlockState());
        set(level, origin.offset(-radius + 1, 3, radius - 1), Blocks.COBWEB.defaultBlockState());
        placeChest(level, origin.offset(-2, 1, -2), lootSeed, ModLoot.DUNGEON, Direction.SOUTH);
        EntityType<?> mob = switch (random.nextInt(5)) {
            case 0 -> ModEntities.NETHER_CREEPER.get();
            case 1 -> ModEntities.NETHER_SPIDER.get();
            case 2 -> ModEntities.NETHER_ZOMBIE.get();
            case 3 -> EntityType.BLAZE;
            default -> EntityType.WITHER_SKELETON;
        };
        placeSpawner(level, origin.offset(0, 1, 0), mob, random);
        connect(level, origin, -radius - 1, 0, -radius - 1, radius + 1, 4, radius + 1);
    }

    public static void placeFireRing(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        preparePad(level, origin, 5, 4, Blocks.NETHERRACK.defaultBlockState(), false);
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                int chebyshev = Math.max(Math.abs(x), Math.abs(z));
                if (chebyshev == 4) {
                    set(level, origin.offset(x, 0, z), Blocks.NETHER_BRICKS.defaultBlockState());
                    set(level, origin.offset(x, 1, z), Blocks.NETHER_BRICK_FENCE.defaultBlockState());
                } else if (chebyshev <= 1) {
                    set(level, origin.offset(x, 0, z), Blocks.MAGMA_BLOCK.defaultBlockState());
                } else {
                    set(level, origin.offset(x, 0, z), Blocks.NETHERRACK.defaultBlockState());
                    if (random.nextFloat() < 0.45F) {
                        set(level, origin.offset(x, 1, z), Blocks.FIRE.defaultBlockState());
                    }
                }
            }
        }
        set(level, origin, Blocks.GILDED_BLACKSTONE.defaultBlockState());
        set(level, origin.above(), Blocks.SOUL_CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, true));
        for (int[] pillar : new int[][]{{3, 3}, {3, -3}, {-3, 3}, {-3, -3}}) {
            set(level, origin.offset(pillar[0], 1, pillar[1]), Blocks.POLISHED_BLACKSTONE.defaultBlockState());
            set(level, origin.offset(pillar[0], 2, pillar[1]), Blocks.POLISHED_BLACKSTONE.defaultBlockState());
            set(level, origin.offset(pillar[0], 3, pillar[1]), Blocks.NETHERRACK.defaultBlockState());
            set(level, origin.offset(pillar[0], 4, pillar[1]), Blocks.FIRE.defaultBlockState());
        }
        set(level, origin.offset(0, 0, 6), Blocks.NETHER_BRICKS.defaultBlockState());
        placeChest(level, origin.offset(0, 1, 6), lootSeed, ModLoot.ASH_PYRE, Direction.SOUTH);
        if (random.nextFloat() < NetherOverhaulConfig.CINDER_WARDEN_CHANCE.get()) {
            spawnCinderWarden(level, origin.above(3));
        }
        connect(level, origin, -5, 0, -5, 5, 4, 6);
    }

    public static void placeRiftAltar(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        preparePad(level, origin, 5, 10, Blocks.OBSIDIAN.defaultBlockState(), true);
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                boolean rim = Math.max(Math.abs(x), Math.abs(z)) >= 3;
                BlockState floor = ((x + z) & 1) == 0 ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : Blocks.OBSIDIAN.defaultBlockState();
                set(level, origin.offset(x, 0, z), rim ? Blocks.POLISHED_BLACKSTONE.defaultBlockState() : floor);
            }
        }
        for (int y = 1; y <= 8; y++) {
            set(level, origin.above(y), y == 8 ? Blocks.GLOWSTONE.defaultBlockState() : Blocks.CRYING_OBSIDIAN.defaultBlockState());
        }
        set(level, origin.above(), Blocks.CHISELED_POLISHED_BLACKSTONE.defaultBlockState());
        for (int[] pillar : new int[][]{{-3, -3}, {-3, 3}, {3, -3}, {3, 3}}) {
            for (int y = 1; y <= 4; y++) {
                set(level, origin.offset(pillar[0], y, pillar[1]), Blocks.POLISHED_BLACKSTONE.defaultBlockState());
            }
            set(level, origin.offset(pillar[0], 5, pillar[1]), Blocks.END_ROD.defaultBlockState().setValue(EndRodBlock.FACING, Direction.UP));
        }
        placeChest(level, origin.offset(0, 1, 3), lootSeed, ModLoot.RIFT_CACHE, Direction.NORTH);
        placeLectern(level, origin.offset(-2, 1, 2), Direction.NORTH, NetherLore.altarNote());
        set(level, origin.offset(2, 1, 2), Blocks.SOUL_FIRE.defaultBlockState());
        set(level, origin.offset(2, 0, 2), Blocks.SOUL_SOIL.defaultBlockState());
        set(level, origin.offset(-2, 0, -2), Blocks.SOUL_SOIL.defaultBlockState());
        set(level, origin.offset(-2, 1, -2), Blocks.SOUL_FIRE.defaultBlockState());
        placeFrame(level, origin.offset(1, 3, 0), Direction.EAST, new ItemStack(ModItems.RIFT_SHARD.get()));
        set(level, origin.offset(0, 1, -4), Blocks.BLACK_BANNER.defaultBlockState().setValue(BannerBlock.ROTATION, 8));
        if (random.nextFloat() < 1.0F) {
            spawnRiftWarden(level, origin.offset(0, 1, -2));
        }
        connect(level, origin, -5, 0, -5, 5, 9, 5);
    }

    /**
     * Original Nether Overload {@code FireTower}: nether-brick trunk, short roots, fire-tipped branches.
     */
    public static void placeFireTower(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        preparePad(level, origin, 2, 1, Blocks.NETHER_BRICKS.defaultBlockState(), false);
        int height = 3 + random.nextInt(6);
        int branchDrop = 2 + random.nextInt(3);
        int branches = 3 + random.nextInt(3);
        int reach = 3 + random.nextInt(3);
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();
        BlockState brick = Blocks.NETHER_BRICKS.defaultBlockState();
        BlockState fire = Blocks.FIRE.defaultBlockState();

        for (int i = 0; i < height; i++) {
            set(level, new BlockPos(x, y + i, z), brick);
        }
        set(level, new BlockPos(x, y + height, z), fire);

        int[] trunk = {0, 0, 1, 0, 0, 1, -1, 0, 0, -1};
        for (int i = 0; i < 5; i++) {
            int hy = y + random.nextInt(3);
            while (hy > y - 1) {
                set(level, new BlockPos(x + trunk[i * 2], hy, z + trunk[i * 2 + 1]), brick);
                hy--;
            }
        }

        int angle = random.nextInt(Math.max(1, (int) (360.0F / branches)));
        for (int n = 0; n < branches; n++) {
            float dist = 0.0F;
            float by = height - random.nextFloat() * branchDrop - 2.0F;
            angle += (int) (360.0F / branches);
            float cos = (float) Math.cos(angle * Math.PI / 180.0D);
            float sin = (float) Math.sin(angle * Math.PI / 180.0D);
            while (dist < reach) {
                dist += 1.0F;
                by += 0.5F;
                int bx = x + (int) (dist * cos);
                int bz = z + (int) (dist * sin);
                int byi = y + (int) by;
                BlockPos above = new BlockPos(bx, byi + 1, bz);
                if (!level.getBlockState(above).isAir()) {
                    continue;
                }
                set(level, new BlockPos(bx, byi, bz), brick);
                set(level, above, fire);
            }
        }
        BlockPos chest = origin.offset(2, 1, 0);
        if (!level.getBlockState(chest).isAir()) {
            chest = origin.offset(0, 1, 2);
        }
        set(level, chest.below(), Blocks.NETHER_BRICKS.defaultBlockState());
        if (level.getBlockState(chest).isAir()) {
            placeChest(level, chest, lootSeed, ModLoot.FIRE_TOWER, Direction.NORTH);
        }
    }

    /**
     * Original Nether Overload {@code BlazeTowerGen}: fence cage, spiral slabs, blaze spawner and chest.
     */
    public static void placeBlazeTower(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        BlockPos base = origin.offset(-3, 0, -3);
        BlockState brick = Blocks.NETHER_BRICKS.defaultBlockState();
        BlockState fence = Blocks.NETHER_BRICK_FENCE.defaultBlockState();
        BlockState slab = Blocks.NETHER_BRICK_SLAB.defaultBlockState();
        BlockState fire = Blocks.FIRE.defaultBlockState();

        for (int x = 0; x <= 7; x++) {
            for (int z = 0; z <= 7; z++) {
                set(level, base.offset(x, -1, z), Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
                if (x == 0 || x == 7 || z == 0 || z == 7) {
                    stair(level, base.offset(x, 0, z), x == 0 ? Direction.WEST : x == 7 ? Direction.EAST : z == 0 ? Direction.NORTH : Direction.SOUTH);
                }
            }
        }

        for (int x = 1; x <= 6; x++) {
            for (int z = 1; z <= 6; z++) {
                if (x == 1 || x == 6 || z == 1 || z == 6) {
                    set(level, base.offset(x, 0, z), brick);
                }
            }
        }

        for (int y = 1; y <= 15; y++) {
            for (int x = 1; x <= 6; x++) {
                for (int z = 1; z <= 6; z++) {
                    if (x == 1 || x == 6 || z == 1 || z == 6) {
                        set(level, base.offset(x, y, z), fence);
                    }
                }
            }
        }

        int[][] spiral = {
                {3, 5}, {4, 5}, {5, 5}, {5, 4}, {5, 3}, {5, 2},
                {4, 2}, {3, 2}, {2, 2}, {2, 3}, {2, 4}, {2, 5}
        };
        for (int y = 1; y <= 14; y++) {
            int[] step = spiral[(y - 1) % spiral.length];
            set(level, base.offset(step[0], y, step[1]), slab);
        }

        for (int x = 1; x <= 6; x++) {
            for (int z = 1; z <= 6; z++) {
                if (x != 1 && x != 6 && z != 1 && z != 6) {
                    continue;
                }
                Direction facing = z == 1 ? Direction.SOUTH : z == 6 ? Direction.NORTH : x == 1 ? Direction.EAST : Direction.WEST;
                BlockState stairs = Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, facing);
                set(level, base.offset(x, 16, z), stairs);
                if ((x == 1 || x == 6) && (z == 1 || z == 6)) {
                    set(level, base.offset(x, 17, z), stairs);
                    set(level, base.offset(x, 18, z), stairs);
                }
            }
        }

        set(level, base.offset(0, 18, 0), slab);
        set(level, base.offset(7, 18, 0), slab);
        set(level, base.offset(0, 18, 7), slab);
        set(level, base.offset(7, 18, 7), slab);
        for (int i = 1; i <= 6; i++) {
            set(level, base.offset(i, 19, 0), slab);
            set(level, base.offset(i, 19, 7), slab);
            set(level, base.offset(0, 19, i), slab);
            set(level, base.offset(7, 19, i), slab);
        }

        for (int x = 1; x <= 6; x++) {
            for (int z = 1; z <= 6; z++) {
                set(level, base.offset(x, 19, z), fence);
                boolean inner = x >= 2 && x <= 5 && z >= 2 && z <= 5;
                if (inner) {
                    set(level, base.offset(x, 20, z), brick);
                    set(level, base.offset(x, 21, z), fire);
                }
            }
        }
        for (int x = 2; x <= 5; x++) {
            set(level, base.offset(x, 20, 1), slab);
            set(level, base.offset(x, 20, 6), slab);
        }
        for (int z = 2; z <= 5; z++) {
            set(level, base.offset(1, 20, z), slab);
            set(level, base.offset(6, 20, z), slab);
        }

        placeSpawner(level, base.offset(2, 16, 2), EntityType.BLAZE, random);
        placeChest(level, base.offset(3, 16, 2), lootSeed, ModLoot.BLAZE_TOWER);
        lantern(level, base.offset(4, 16, 4), false);
        connect(level, base, 0, 0, 0, 7, 21, 7);
    }

    public static void placeHugeFungus(WorldGenLevel level, BlockPos origin, boolean crimson, RandomSource random) {
        int height = 5 + random.nextInt(4);
        BlockState stem = (crimson ? Blocks.CRIMSON_STEM : Blocks.WARPED_STEM).defaultBlockState();
        BlockState hat = (crimson ? Blocks.NETHER_WART_BLOCK : Blocks.WARPED_WART_BLOCK).defaultBlockState();
        BlockState shroom = Blocks.SHROOMLIGHT.defaultBlockState();
        for (int y = 0; y < height; y++) {
            set(level, origin.above(y), stem);
        }
        int hatY = height - 1;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 && Math.abs(z) == 2 && random.nextBoolean()) {
                    continue;
                }
                set(level, origin.offset(x, hatY, z), hat);
            }
        }
        set(level, origin.offset(0, hatY + 1, 0), hat);
        set(level, origin.offset(1, hatY - 1, 0), shroom);
        set(level, origin.offset(-1, hatY - 1, 0), shroom);
    }

    public static void placeWitherTree(WorldGenLevel level, BlockPos origin, RandomSource random) {
        set(level, origin.below(), Blocks.SOUL_SOIL.defaultBlockState());
        int height = 4 + random.nextInt(3);
        for (int y = 0; y < height; y++) {
            set(level, origin.above(y), Blocks.CRYING_OBSIDIAN.defaultBlockState());
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) + Math.abs(z) <= 3) {
                    set(level, origin.offset(x, height, z), Blocks.NETHER_WART_BLOCK.defaultBlockState());
                }
            }
        }
        if (random.nextFloat() < 0.45F) {
            set(level, origin.offset(0, height + 1, 0), Blocks.WITHER_SKELETON_SKULL.defaultBlockState());
        }
        BlockPos hang = origin.offset(1, height - 1, 0);
        if (level.getBlockState(hang).isAir()) {
            set(level, hang, Blocks.CHAIN.defaultBlockState().setValue(ChainBlock.AXIS, Direction.Axis.Y));
        }
    }

    public static void placeGlowstoneTree(WorldGenLevel level, BlockPos origin, RandomSource random) {
        int height = 3 + random.nextInt(3);
        for (int y = 0; y < height; y++) {
            set(level, origin.above(y), Blocks.BASALT.defaultBlockState());
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                set(level, origin.offset(x, height, z), Blocks.GLOWSTONE.defaultBlockState());
            }
        }
        set(level, origin.offset(0, height + 1, 0), Blocks.GLOWSTONE.defaultBlockState());
        if (level.getBlockState(origin.offset(1, height - 1, 0)).isAir()) {
            set(level, origin.offset(1, height - 1, 0), Blocks.CHAIN.defaultBlockState().setValue(ChainBlock.AXIS, Direction.Axis.Y));
        }
    }

    public static void placeBoneRib(WorldGenLevel level, BlockPos origin) {
        set(level, origin, Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y));
        set(level, origin.above(), Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y));
        set(level, origin.above().east(), Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X));
        set(level, origin.above().west(), Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X));
        if (level.getBlockState(origin.above(2)).isAir()) {
            set(level, origin.above(2), Blocks.SKELETON_SKULL.defaultBlockState());
        }
    }

    public static void placeQuartzSpike(WorldGenLevel level, BlockPos origin, RandomSource random) {
        int height = 2 + random.nextInt(3);
        for (int y = 0; y < height; y++) {
            set(level, origin.above(y), Blocks.QUARTZ_PILLAR.defaultBlockState());
        }
        set(level, origin.above(height), Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState());
        if (random.nextBoolean() && level.getBlockState(origin.above(height + 1)).isAir()) {
            set(level, origin.above(height + 1), Blocks.END_ROD.defaultBlockState().setValue(EndRodBlock.FACING, Direction.UP));
        }
    }

    public static void placeGlowstoneBerryBush(WorldGenLevel level, BlockPos origin, RandomSource random) {
        BlockState ground = level.getBlockState(origin.below());
        if (!ground.is(Blocks.SOUL_SAND) && !ground.is(Blocks.SOUL_SOIL)) {
            set(level, origin.below(), Blocks.SOUL_SAND.defaultBlockState());
        }
        int age = random.nextInt(4);
        set(level, origin, ModBlocks.GLOWSTONE_CROP.get().defaultBlockState().setValue(GlowstoneCropBlock.AGE, age));
    }

    private static void placeHut(WorldGenLevel level, BlockPos origin, RandomSource random, int variant) {
        BlockState wall = switch (variant) {
            case 1 -> Blocks.WARPED_PLANKS.defaultBlockState();
            case 2 -> Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
            default -> Blocks.CRIMSON_PLANKS.defaultBlockState();
        };
        BlockState roof = switch (variant) {
            case 1 -> Blocks.WARPED_WART_BLOCK.defaultBlockState();
            case 2 -> Blocks.NETHER_BRICKS.defaultBlockState();
            default -> Blocks.RED_NETHER_BRICKS.defaultBlockState();
        };
        Block door = variant == 1 ? Blocks.WARPED_DOOR : Blocks.CRIMSON_DOOR;
        for (int y = 0; y <= 4; y++) {
            for (int x = 0; x <= 4; x++) {
                for (int z = 0; z <= 4; z++) {
                    boolean edge = x == 0 || z == 0 || x == 4 || z == 4;
                    boolean doorCell = z == 0 && x == 2 && y >= 1 && y <= 2;
                    boolean window = y == 2 && edge && ((x == 0 || x == 4) && z == 2 || (z == 4 && x == 2));
                    BlockPos pos = origin.offset(x, y, z);
                    if (y == 0) {
                        set(level, pos, variant == 2 ? Blocks.POLISHED_BLACKSTONE.defaultBlockState() : Blocks.CRIMSON_NYLIUM.defaultBlockState());
                    } else if (y == 4) {
                        set(level, pos, roof);
                    } else if (doorCell) {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    } else if (window) {
                        set(level, pos, Blocks.IRON_BARS.defaultBlockState());
                    } else if (edge) {
                        set(level, pos, wall);
                    } else {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        BlockState lower = door.defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        set(level, origin.offset(2, 1, 0), lower);
        set(level, origin.offset(2, 2, 0), lower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        lantern(level, origin.offset(2, 3, 2), true);
        if (variant == 2) {
            set(level, origin.offset(3, 1, 2), Blocks.BLAST_FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.WEST));
            set(level, origin.offset(1, 1, 3), Blocks.SMITHING_TABLE.defaultBlockState());
        } else if (random.nextBoolean()) {
            set(level, origin.offset(3, 1, 3), (variant == 1 ? Blocks.WARPED_FUNGUS : Blocks.CRIMSON_FUNGUS).defaultBlockState());
        }
    }

    private static void spawnRiftWarden(WorldGenLevel level, BlockPos pos) {
        if (!NetherOverhaulConfig.SPAWN_RIFT_WARDEN.get()) {
            return;
        }
        ServerLevel server = level.getLevel();
        WitherSkeleton skeleton = EntityType.WITHER_SKELETON.create(server);
        if (skeleton == null) {
            return;
        }
        skeleton.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 180.0F, 0.0F);
        skeleton.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null);
        skeleton.setCustomName(Component.translatable("entity.netheroverhaul.rift_warden"));
        skeleton.setCustomNameVisible(true);
        skeleton.setPersistenceRequired();
        skeleton.addTag("netheroverhaul_rift_warden");
        var health = skeleton.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(40.0D);
            skeleton.setHealth(40.0F);
        }
        skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
        skeleton.setDropChance(EquipmentSlot.HEAD, 0.35F);
        skeleton.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        skeleton.addEffect(new MobEffectInstance(MobEffects.GLOWING, MobEffectInstance.INFINITE_DURATION, 0, false, false, false));
        level.addFreshEntity(skeleton);
    }

    private static void spawnCinderWarden(WorldGenLevel level, BlockPos pos) {
        ServerLevel server = level.getLevel();
        Blaze blaze = EntityType.BLAZE.create(server);
        if (blaze == null) {
            return;
        }
        blaze.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        blaze.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null);
        blaze.setCustomName(Component.translatable("entity.netheroverhaul.cinder_warden"));
        blaze.setCustomNameVisible(true);
        blaze.setPersistenceRequired();
        blaze.addTag("netheroverhaul_cinder_warden");
        var health = blaze.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(36.0D);
            blaze.setHealth(36.0F);
        }
        blaze.addEffect(new MobEffectInstance(MobEffects.GLOWING, MobEffectInstance.INFINITE_DURATION, 0, false, false, false));
        level.addFreshEntity(blaze);
    }

    private static void placeFrame(WorldGenLevel level, BlockPos pos, Direction facing, ItemStack stack) {
        if (!level.getBlockState(pos).isAir()) {
            return;
        }
        ItemFrame frame = new ItemFrame(level.getLevel(), pos, facing);
        frame.setItem(stack.copy());
        level.addFreshEntity(frame);
    }

    private static BlockState brick(int x, int y, int z) {
        int roll = Math.floorMod(x * 13 + y * 17 + z * 19, 8);
        if (roll == 0) {
            return Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
        }
        if (roll == 1) {
            return Blocks.POLISHED_BLACKSTONE.defaultBlockState();
        }
        return Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
    }

    private static BlockState waterCauldron() {
        return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
    }

    private static void stair(WorldGenLevel level, BlockPos pos, Direction facing) {
        set(level, pos, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.defaultBlockState()
                .setValue(StairBlock.FACING, facing)
                .setValue(StairBlock.HALF, Half.BOTTOM));
    }

    private static void lantern(WorldGenLevel level, BlockPos pos, boolean hanging) {
        set(level, pos, Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, hanging));
    }

    private static void post(WorldGenLevel level, BlockPos pos) {
        set(level, pos, Blocks.NETHER_BRICK_FENCE.defaultBlockState());
        lantern(level, pos.above(), false);
    }

    private static void preparePad(WorldGenLevel level, BlockPos origin, int radius, int airHeight, BlockState floor, boolean round) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (round && x * x + z * z > radius * radius) {
                    continue;
                }
                set(level, origin.offset(x, -1, z), floor);
                set(level, origin.offset(x, 0, z), floor);
                for (int y = 1; y <= airHeight; y++) {
                    set(level, origin.offset(x, y, z), Blocks.AIR.defaultBlockState());
                }
                for (int y = -3; y <= -2; y++) {
                    BlockPos below = origin.offset(x, y, z);
                    if (!level.getBlockState(below).isSolid()) {
                        set(level, below, Blocks.BLACKSTONE.defaultBlockState());
                    }
                }
            }
        }
    }

    private static void connect(WorldGenLevel level, BlockPos origin, int x0, int y0, int z0, int x1, int y1, int z1) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                for (int z = z0; z <= z1; z++) {
                    cursor.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    BlockState state = level.getBlockState(cursor);
                    if (state.isAir()) {
                        continue;
                    }
                    BlockState updated = Block.updateFromNeighbourShapes(state, level, cursor);
                    if (updated != state) {
                        level.setBlock(cursor, updated, 2);
                    }
                }
            }
        }
    }

    private static void placeDoor(WorldGenLevel level, BlockPos lower, Direction facing, DoorHingeSide hinge) {
        BlockState state = Blocks.CRIMSON_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, facing)
                .setValue(DoorBlock.HINGE, hinge)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        set(level, lower, state);
        set(level, lower.above(), state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }

    private static void placeOptionalSleepingBag(WorldGenLevel level, BlockPos foot, Direction facing) {
        BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse("comforts:sleeping_bag_white")).ifPresent(block -> {
            BlockState base = block.defaultBlockState();
            if (!base.hasProperty(BedBlock.FACING) || !base.hasProperty(BedBlock.PART)) {
                return;
            }
            BlockPos head = foot.relative(facing);
            set(level, foot, base.setValue(BedBlock.FACING, facing).setValue(BedBlock.PART, BedPart.FOOT));
            set(level, head, base.setValue(BedBlock.FACING, facing).setValue(BedBlock.PART, BedPart.HEAD));
        });
    }

    private static void placeLectern(WorldGenLevel level, BlockPos pos, Direction facing, ItemStack book) {
        set(level, pos, Blocks.LECTERN.defaultBlockState()
                .setValue(LecternBlock.FACING, facing)
                .setValue(LecternBlock.HAS_BOOK, true));
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lectern) {
            lectern.setBook(book);
        }
    }

    private static void placeStandingSign(WorldGenLevel level, BlockPos pos, int rotation, String line0, String line1, String line2, String line3) {
        set(level, pos, Blocks.CRIMSON_SIGN.defaultBlockState().setValue(StandingSignBlock.ROTATION, rotation));
        writeSign(level, pos, line0, line1, line2, line3);
    }

    private static void placeSign(WorldGenLevel level, BlockPos pos, Direction facing, String line0, String line1, String line2, String line3) {
        set(level, pos, Blocks.CRIMSON_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING, facing));
        writeSign(level, pos, line0, line1, line2, line3);
    }

    private static void writeSign(WorldGenLevel level, BlockPos pos, String line0, String line1, String line2, String line3) {
        if (level.getBlockEntity(pos) instanceof SignBlockEntity sign) {
            SignText text = new SignText()
                    .setMessage(0, Component.literal(line0))
                    .setMessage(1, Component.literal(line1))
                    .setMessage(2, Component.literal(line2))
                    .setMessage(3, Component.literal(line3))
                    .setColor(DyeColor.ORANGE)
                    .setHasGlowingText(true);
            sign.setText(text, true);
            sign.setWaxed(true);
        }
    }

    private static void placeChest(WorldGenLevel level, BlockPos pos, long lootSeed, ResourceKey<LootTable> table) {
        placeChest(level, pos, lootSeed, table, Direction.NORTH);
    }

    private static void placeChest(WorldGenLevel level, BlockPos pos, long lootSeed, ResourceKey<LootTable> table, Direction facing) {
        set(level, pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing));
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(table, lootSeed);
        }
    }

    private static void placeSpawner(WorldGenLevel level, BlockPos pos, EntityType<?> type, RandomSource random) {
        set(level, pos, Blocks.SPAWNER.defaultBlockState());
        if (level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
            spawner.setEntityId(type, random);
        }
    }

    private static void set(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (level.isOutsideBuildHeight(pos.getY())) {
            return;
        }
        if (level.getBlockState(pos).is(Blocks.BEDROCK)) {
            return;
        }
        level.setBlock(pos, state, 2);
    }
}
