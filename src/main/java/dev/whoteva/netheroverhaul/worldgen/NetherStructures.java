package dev.whoteva.netheroverhaul.worldgen;

import dev.whoteva.netheroverhaul.ModLoot;
import dev.whoteva.netheroverhaul.block.GlowstoneCropBlock;
import dev.whoteva.netheroverhaul.block.ModBlocks;
import dev.whoteva.netheroverhaul.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.storage.loot.LootTable;

public final class NetherStructures {

    private NetherStructures() {
    }

    public static void placeSpawnCamp(WorldGenLevel level, BlockPos origin, long lootSeed) {
        int hut = 4;
        int pad = 7;

        for (int x = -pad; x <= pad; x++) {
            for (int z = -pad; z <= pad; z++) {
                set(level, origin.offset(x, -1, z), Blocks.BLACKSTONE.defaultBlockState());
                boolean insideHut = Math.abs(x) <= hut && Math.abs(z) <= hut;
                set(level, origin.offset(x, 0, z), (insideHut ? Blocks.POLISHED_BLACKSTONE : Blocks.BLACKSTONE).defaultBlockState());
                if (!insideHut) {
                    for (int y = 1; y <= 4; y++) {
                        set(level, origin.offset(x, y, z), Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }

        for (int x = -hut; x <= hut; x++) {
            for (int z = -hut; z <= hut; z++) {
                boolean wall = Math.abs(x) == hut || Math.abs(z) == hut;
                boolean door = z == hut && (x == 0 || x == -1);
                boolean window = !door && ((Math.abs(x) == hut && z == 0) || (Math.abs(z) == hut && x == 0));
                for (int y = 1; y <= 4; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (wall && door && y <= 2) {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    } else if (wall && window && y == 2) {
                        set(level, pos, Blocks.IRON_BARS.defaultBlockState());
                    } else if (wall) {
                        boolean pillar = Math.abs(x) == hut && Math.abs(z) == hut;
                        set(level, pos, (pillar ? Blocks.POLISHED_BLACKSTONE : Blocks.BLACKSTONE).defaultBlockState());
                    } else {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    }
                }
                boolean glow = (x == 0 && z == 0) || (Math.abs(x) == hut - 1 && Math.abs(z) == hut - 1);
                set(level, origin.offset(x, 5, z), glow
                        ? Blocks.GLOWSTONE.defaultBlockState()
                        : Blocks.POLISHED_BLACKSTONE.defaultBlockState());
            }
        }

        set(level, origin, Blocks.LODESTONE.defaultBlockState());
        set(level, origin.offset(0, 4, 0), Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));

        placeChest(level, origin.offset(0, 1, -3), lootSeed, ModLoot.SPAWN_CAMP, Direction.SOUTH);
        placeCampSign(level, origin.offset(1, 2, -3));
        set(level, origin.offset(-3, 1, -3), Blocks.CRAFTING_TABLE.defaultBlockState());
        set(level, origin.offset(-2, 1, -3), Blocks.STONECUTTER.defaultBlockState());
        set(level, origin.offset(2, 1, -3), Blocks.FURNACE.defaultBlockState());
        set(level, origin.offset(3, 1, -3), Blocks.BARREL.defaultBlockState());
        set(level, origin.offset(-3, 1, 0), Blocks.GRINDSTONE.defaultBlockState());
        set(level, origin.offset(3, 1, 0), Blocks.RESPAWN_ANCHOR.defaultBlockState().setValue(RespawnAnchorBlock.CHARGE, 2));
        set(level, origin.offset(-3, 1, 2), Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
        set(level, origin.offset(3, 1, 2), Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
        set(level, origin.offset(-3, 1, 3), Blocks.COMPOSTER.defaultBlockState());
        set(level, origin.offset(-2, 1, 3), Blocks.WHITE_WOOL.defaultBlockState());
        set(level, origin.offset(-1, 1, 3), Blocks.WHITE_WOOL.defaultBlockState());
        set(level, origin.offset(-2, 2, 3), Blocks.WHITE_CARPET.defaultBlockState());
        set(level, origin.offset(-1, 2, 3), Blocks.WHITE_CARPET.defaultBlockState());
        placeOptionalSleepingBag(level, origin.offset(-2, 1, 2), Direction.EAST);

        placeDoor(level, origin.offset(-1, 1, hut), Direction.SOUTH, DoorHingeSide.LEFT);
        placeDoor(level, origin.offset(0, 1, hut), Direction.SOUTH, DoorHingeSide.RIGHT);
        set(level, origin.offset(-1, 0, hut + 1), Blocks.POLISHED_BLACKSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH));
        set(level, origin.offset(0, 0, hut + 1), Blocks.POLISHED_BLACKSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH));
        set(level, origin.offset(0, 1, hut + 3), Blocks.SOUL_CAMPFIRE.defaultBlockState());

        BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
        for (int y = 1; y <= 4; y++) {
            set(level, origin.offset(3, y, 3), ladder);
        }
        set(level, origin.offset(3, 5, 3), Blocks.CRIMSON_TRAPDOOR.defaultBlockState()
                .setValue(TrapDoorBlock.FACING, Direction.WEST)
                .setValue(TrapDoorBlock.HALF, Half.TOP)
                .setValue(TrapDoorBlock.OPEN, true));
        set(level, origin.offset(2, 6, -3), Blocks.NETHER_BRICKS.defaultBlockState());
        set(level, origin.offset(2, 7, -3), Blocks.SOUL_CAMPFIRE.defaultBlockState());

        RandomSource random = RandomSource.create(lootSeed);
        int farmMin = hut + 3;
        int farmMax = hut + 6;
        for (int x = farmMin; x <= farmMax; x++) {
            for (int z = -2; z <= 2; z++) {
                boolean fence = x == farmMin || x == farmMax || z == -2 || z == 2;
                boolean gate = x == farmMin && z == 0;
                if (fence && gate) {
                    set(level, origin.offset(x, 1, z), Blocks.CRIMSON_FENCE_GATE.defaultBlockState()
                            .setValue(FenceGateBlock.FACING, Direction.WEST));
                } else if (fence) {
                    set(level, origin.offset(x, 1, z), Blocks.NETHER_BRICK_FENCE.defaultBlockState());
                } else {
                    set(level, origin.offset(x, 0, z), Blocks.SOUL_SAND.defaultBlockState());
                    if (x == farmMin + 2 && z == 0) {
                        placeGlowstoneBerryBush(level, origin.offset(x, 1, z), random);
                    } else {
                        set(level, origin.offset(x, 1, z), Blocks.NETHER_WART.defaultBlockState());
                    }
                }
            }
        }
        set(level, origin.offset(farmMax, 2, 0), Blocks.SOUL_LANTERN.defaultBlockState());
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

    public static void placeWatchtower(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        int height = 18 + random.nextInt(10);
        int radius = 3;
        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    boolean edge = Math.abs(x) == radius || Math.abs(z) == radius;
                    boolean corner = Math.abs(x) == radius && Math.abs(z) == radius;
                    BlockPos pos = origin.offset(x, y, z);
                    if (y == 0) {
                        set(level, pos, Blocks.POLISHED_BLACKSTONE.defaultBlockState());
                    } else if (edge && (y < height - 2 || corner)) {
                        set(level, pos, (y % 5 == 0 ? Blocks.NETHER_BRICKS : Blocks.BLACKSTONE).defaultBlockState());
                    } else if (!edge) {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
            BlockPos ladder = origin.offset(radius - 1, y, 0);
            set(level, ladder, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST));
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                set(level, origin.offset(x, height, z), Blocks.POLISHED_BLACKSTONE_SLAB.defaultBlockState());
            }
        }

        placeChest(level, origin.offset(0, height - 1, 0), lootSeed, ModLoot.WATCHTOWER);
        set(level, origin.offset(0, height - 1, 1), Blocks.SOUL_LANTERN.defaultBlockState());
        if (random.nextFloat() < 0.5F) {
            set(level, origin.offset(0, height + 1, 0), Blocks.SOUL_LANTERN.defaultBlockState());
        }
    }

    public static void placeHamlet(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        int huts = 3 + random.nextInt(3);
        for (int i = 0; i < huts; i++) {
            int ox = (i - huts / 2) * 7;
            int oz = (i % 2 == 0 ? 0 : 6);
            placeHut(level, origin.offset(ox, 0, oz), random);
        }

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos farm = origin.offset(x, 0, z - 8);
                set(level, farm, Blocks.SOUL_SAND.defaultBlockState());
                if (Math.abs(x) != 2 || Math.abs(z) != 2) {
                    if (random.nextFloat() < 0.35F) {
                        placeGlowstoneBerryBush(level, farm.above(), random);
                    } else {
                        set(level, farm.above(), Blocks.NETHER_WART.defaultBlockState());
                    }
                }
            }
        }

        int firstHutX = (0 - huts / 2) * 7;
        placeChest(level, origin.offset(firstHutX + 2, 1, 2), lootSeed, ModLoot.HAMLET);
        set(level, origin.offset(1, 1, -8), Blocks.CRIMSON_FENCE.defaultBlockState());
    }

    public static void placeWitherPit(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        int depth = 18 + random.nextInt(8);
        int radius = 4;
        BlockState shell = Blocks.REINFORCED_DEEPSLATE.defaultBlockState();
        for (int y = 0; y >= -depth; y--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    boolean wall = Math.abs(x) == radius || Math.abs(z) == radius || y == -depth;
                    if (wall) {
                        set(level, pos, shell);
                    } else {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        BlockPos chestPos = origin.offset(0, -depth + 1, 0);
        placeChest(level, chestPos, lootSeed, ModLoot.WITHER_PIT);
        set(level, origin.offset(1, -depth + 1, 0), Blocks.SOUL_SOIL.defaultBlockState());
        set(level, origin.offset(1, -depth + 2, 0), Blocks.SOUL_FIRE.defaultBlockState());
        set(level, origin.offset(0, 1, 0), Blocks.WITHER_SKELETON_SKULL.defaultBlockState());
    }

    public static void placeGhastNest(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        int radius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x) == radius || Math.abs(z) == radius) {
                    set(level, origin.offset(x, 0, z), Blocks.NETHER_BRICK_FENCE.defaultBlockState());
                } else {
                    set(level, origin.offset(x, 0, z), Blocks.NETHER_BRICKS.defaultBlockState());
                }
            }
        }
        set(level, origin, Blocks.GLOWSTONE.defaultBlockState());
        set(level, origin.above(), Blocks.CHAIN.defaultBlockState());
        set(level, origin.above(2), Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        placeChest(level, origin.offset(0, 1, 0), lootSeed, ModLoot.GHAST_NEST);
        if (random.nextBoolean()) {
            set(level, origin.offset(2, 1, 0), Blocks.CRYING_OBSIDIAN.defaultBlockState());
        }
    }

    public static void placeBasaltForge(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, origin.offset(x, 0, z), Blocks.POLISHED_BASALT.defaultBlockState());
                boolean wall = Math.abs(x) == 2 || Math.abs(z) == 2;
                if (wall && !(z == 2 && Math.abs(x) == 0)) {
                    set(level, origin.offset(x, 1, z), Blocks.BASALT.defaultBlockState());
                    set(level, origin.offset(x, 2, z), Blocks.BASALT.defaultBlockState());
                }
            }
        }
        set(level, origin.offset(0, 1, -1), Blocks.BLAST_FURNACE.defaultBlockState());
        set(level, origin.offset(-1, 1, -1), Blocks.LAVA_CAULDRON.defaultBlockState());
        set(level, origin.offset(1, 1, 0), Blocks.MAGMA_BLOCK.defaultBlockState());
        placeChest(level, origin.offset(1, 1, -1), lootSeed, ModLoot.BASALT_FORGE);
    }

    public static void placeQuartzShrine(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, origin.offset(x, 0, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
            }
        }
        for (int[] pillar : new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}}) {
            for (int y = 1; y <= 3; y++) {
                set(level, origin.offset(pillar[0], y, pillar[1]), Blocks.QUARTZ_PILLAR.defaultBlockState());
            }
            set(level, origin.offset(pillar[0], 4, pillar[1]), Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState());
        }
        placeChest(level, origin.above(), lootSeed, ModLoot.QUARTZ_SHRINE);
        set(level, origin.offset(0, 1, 1), Blocks.AMETHYST_BLOCK.defaultBlockState());
        if (random.nextBoolean()) {
            set(level, origin.offset(0, 1, -1), Blocks.END_ROD.defaultBlockState());
        }
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

    public static void placeObsidianWell(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                boolean rim = Math.abs(x) == 2 || Math.abs(z) == 2;
                set(level, origin.offset(x, 0, z), Blocks.OBSIDIAN.defaultBlockState());
                if (rim) {
                    set(level, origin.offset(x, 1, z), Blocks.OBSIDIAN.defaultBlockState());
                } else {
                    for (int y = 0; y >= -8; y--) {
                        if (Math.abs(x) == 1 && Math.abs(z) == 1 || x == 0 || z == 0) {
                            set(level, origin.offset(x, y, z), Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
        placeChest(level, origin.offset(0, -7, 0), lootSeed, ModLoot.WELL);
        set(level, origin.offset(0, 2, -2), Blocks.SOUL_LANTERN.defaultBlockState());
    }

    public static void placeNetherDungeon(WorldGenLevel level, BlockPos origin, RandomSource random, long lootSeed) {
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -2; z <= 2; z++) {
                    boolean wall = Math.abs(x) == 2 || Math.abs(z) == 2 || y == 0 || y == 4;
                    set(level, origin.offset(x, y, z), wall ? Blocks.NETHER_BRICKS.defaultBlockState() : Blocks.AIR.defaultBlockState());
                }
            }
        }
        set(level, origin.offset(0, 1, 2), Blocks.AIR.defaultBlockState());
        set(level, origin.offset(0, 2, 2), Blocks.AIR.defaultBlockState());
        placeChest(level, origin.offset(0, 1, -1), lootSeed, ModLoot.DUNGEON);
        EntityType<?> mob = switch (random.nextInt(5)) {
            case 0 -> ModEntities.NETHER_CREEPER.get();
            case 1 -> ModEntities.NETHER_SPIDER.get();
            case 2 -> ModEntities.NETHER_ZOMBIE.get();
            case 3 -> EntityType.BLAZE;
            default -> EntityType.WITHER_SKELETON;
        };
        placeSpawner(level, origin.offset(0, 1, 0), mob, random);
    }

    public static void placeFireRing(WorldGenLevel level, BlockPos origin, RandomSource random) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                int dist = Math.abs(x) + Math.abs(z);
                if (Math.max(Math.abs(x), Math.abs(z)) == 3) {
                    set(level, origin.offset(x, 0, z), Blocks.NETHER_BRICKS.defaultBlockState());
                    if (random.nextBoolean()) {
                        set(level, origin.offset(x, 1, z), Blocks.FIRE.defaultBlockState());
                    }
                } else if (dist <= 2) {
                    set(level, origin.offset(x, 0, z), Blocks.NETHERRACK.defaultBlockState());
                    if (random.nextFloat() < 0.4F) {
                        set(level, origin.offset(x, 1, z), Blocks.FIRE.defaultBlockState());
                    }
                }
            }
        }
    }

    /**
     * Original Nether Overload {@code FireTower}: nether-brick trunk, short roots, fire-tipped branches.
     */
    public static void placeFireTower(WorldGenLevel level, BlockPos origin, RandomSource random) {
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
    }

    public static void placeWitherTree(WorldGenLevel level, BlockPos origin, RandomSource random) {
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
        if (random.nextFloat() < 0.35F) {
            set(level, origin.offset(0, height + 1, 0), Blocks.WITHER_SKELETON_SKULL.defaultBlockState());
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
    }

    public static void placeGlowstoneBerryBush(WorldGenLevel level, BlockPos origin, RandomSource random) {
        BlockState ground = level.getBlockState(origin.below());
        if (!ground.is(Blocks.SOUL_SAND) && !ground.is(Blocks.SOUL_SOIL)) {
            set(level, origin.below(), Blocks.SOUL_SAND.defaultBlockState());
        }
        int age = random.nextInt(4);
        set(level, origin, ModBlocks.GLOWSTONE_CROP.get().defaultBlockState().setValue(GlowstoneCropBlock.AGE, age));
    }

    private static void placeHut(WorldGenLevel level, BlockPos origin, RandomSource random) {
        int w = 4;
        int h = 4;
        for (int y = 0; y <= h; y++) {
            for (int x = 0; x <= w; x++) {
                for (int z = 0; z <= w; z++) {
                    boolean wall = x == 0 || z == 0 || x == w || z == w;
                    boolean roof = y == h;
                    BlockPos pos = origin.offset(x, y, z);
                    if (y == 0) {
                        set(level, pos, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                    } else if (roof) {
                        set(level, pos, Blocks.RED_NETHER_BRICKS.defaultBlockState());
                    } else if (wall) {
                        if (y == 2 && (x == w / 2 || z == w / 2) && !(x == 0 && z == 0)) {
                            set(level, pos, Blocks.AIR.defaultBlockState());
                        } else {
                            set(level, pos, Blocks.CRIMSON_PLANKS.defaultBlockState());
                        }
                    } else {
                        set(level, pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        set(level, origin.offset(2, 1, 2), Blocks.CRIMSON_FUNGUS.defaultBlockState());
        if (random.nextBoolean()) {
            set(level, origin.offset(1, 2, 1), Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        }
    }

    private static void placeCampSign(WorldGenLevel level, BlockPos pos) {
        set(level, pos, Blocks.CRIMSON_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING, Direction.SOUTH));
        if (level.getBlockEntity(pos) instanceof SignBlockEntity sign) {
            SignText text = new SignText()
                    .setMessage(0, Component.literal("CAMP"))
                    .setMessage(1, Component.literal("Lodestone"))
                    .setMessage(2, Component.literal("under floor"))
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
        level.setBlock(pos, state, 2);
    }
}
