package dev.whoteva.netheroverhaul.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.whoteva.netheroverhaul.NetherOverhaul;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import dev.whoteva.netheroverhaul.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OverhauledNetherChunkGenerator extends ChunkGenerator {

    public static final MapCodec<OverhauledNetherChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(instance, OverhauledNetherChunkGenerator::new));

    public static final TagKey<Block> EXTRA_NETHER_ORES =
            TagKey.create(Registries.BLOCK, NetherOverhaul.id("extra_nether_ores"));

    private static final ResourceLocation GRID_SALT = NetherOverhaul.id("terrain");

    public OverhauledNetherChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender,
                                                        RandomState randomState,
                                                        StructureManager structureManager,
                                                        ChunkAccess chunk) {
        NetherNoise noise = noise(randomState);
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight();
        int lava = NetherOverhaulConfig.LAVA_SEA_LEVEL.get();

        Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = minX + lx;
                int z = minZ + lz;
                int floor = Mth.clamp(noise.floorHeight(x, z), minY + 8, maxY - 40);
                int roof = Mth.clamp(noise.roofHeight(x, z), floor + 24, maxY - 6);
                boolean pillar = noise.pillar(x, z);

                for (int y = minY; y < maxY; y++) {
                    BlockState state = columnState(noise, x, y, z, minY, maxY, floor, roof, pillar, lava);
                    cursor.set(x, y, z);
                    chunk.setBlockState(cursor, state, false);
                    oceanFloor.update(lx, y, lz, state);
                    worldSurface.update(lx, y, lz, state);
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    private static BlockState columnState(NetherNoise noise, int x, int y, int z,
                                          int minY, int maxY, int floor, int roof,
                                          boolean pillar, int lava) {
        if (y <= minY + 4 || y >= maxY - 5) {
            return Blocks.BEDROCK.defaultBlockState();
        }

        boolean ravine = noise.ravine(x, y, z);
        boolean inFloor = y <= floor;
        boolean inRoof = y >= roof;
        boolean inPillar = pillar && y > floor - 4 && y < roof && (x * 31 + z) % 3 != 0;
        boolean cave = noise.cave(x, y, z) > 0.55 && y < floor && y > lava + 2;

        boolean solid = ((inFloor && !cave) || inRoof || inPillar) && !ravine;
        if (solid) {
            long roll = noise.hashPos(x, y, z);
            int ore = (int) (roll & 255);
            if (ore < 4 && y < 22) {
                return Blocks.ANCIENT_DEBRIS.defaultBlockState();
            }
            if (ore < 18) {
                return Blocks.NETHER_QUARTZ_ORE.defaultBlockState();
            }
            if (ore < 28) {
                return Blocks.NETHER_GOLD_ORE.defaultBlockState();
            }
            if (ore < 34) {
                return Blocks.MAGMA_BLOCK.defaultBlockState();
            }
            if (NetherOverhaulConfig.PACK_VANILLA_ORES.get()) {
                if (ore < 42) {
                    return ModBlocks.NETHER_IRON_ORE.get().defaultBlockState();
                }
                if (ore < 50) {
                    return ModBlocks.NETHER_COPPER_ORE.get().defaultBlockState();
                }
                if (ore < 55) {
                    return ModBlocks.NETHER_COAL_ORE.get().defaultBlockState();
                }
                if (ore < 58) {
                    return ModBlocks.NETHER_REDSTONE_ORE.get().defaultBlockState();
                }
            }
            return Blocks.NETHERRACK.defaultBlockState();
        }
        if (y <= lava) {
            return Blocks.LAVA.defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void buildSurface(WorldGenRegion region,
                             StructureManager structureManager,
                             RandomState randomState,
                             ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = minX + lx;
                int z = minZ + lz;
                Holder<Biome> biome = region.getBiome(cursor.set(x, 80, z));
                BlockState surface = surfaceFor(biome);
                BlockState soil = soilFor(biome);

                boolean placed = false;
                for (int y = maxY - 6; y > minY + 5; y--) {
                    cursor.set(x, y, z);
                    BlockState current = chunk.getBlockState(cursor);
                    BlockState above = chunk.getBlockState(cursor.above());
                    if (!placed && current.is(Blocks.NETHERRACK) && above.isAir()) {
                        chunk.setBlockState(cursor, surface, false);
                        if (!soil.isAir()) {
                            cursor.set(x, y - 1, z);
                            if (chunk.getBlockState(cursor).is(Blocks.NETHERRACK)) {
                                chunk.setBlockState(cursor, soil, false);
                            }
                        }
                        placed = true;
                    }
                }
            }
        }
    }

    private static BlockState surfaceFor(Holder<Biome> biome) {
        String path = biome.unwrapKey().map(key -> key.location().getPath()).orElse("");
        return switch (path) {
            case "crimson_forest", "weeping_hollow" -> Blocks.CRIMSON_NYLIUM.defaultBlockState();
            case "warped_forest" -> Blocks.WARPED_NYLIUM.defaultBlockState();
            case "soul_sand_valley", "wither_ruins" -> Blocks.SOUL_SOIL.defaultBlockState();
            case "basalt_deltas" -> Blocks.BASALT.defaultBlockState();
            case "ashen_swamp" -> Blocks.SOUL_SOIL.defaultBlockState();
            case "quartz_peaks" -> Blocks.SMOOTH_QUARTZ.defaultBlockState();
            case "ember_fields" -> Blocks.MAGMA_BLOCK.defaultBlockState();
            default -> Blocks.NETHERRACK.defaultBlockState();
        };
    }

    private static BlockState soilFor(Holder<Biome> biome) {
        String path = biome.unwrapKey().map(key -> key.location().getPath()).orElse("");
        return switch (path) {
            case "soul_sand_valley", "wither_ruins", "ashen_swamp" -> Blocks.SOUL_SAND.defaultBlockState();
            case "basalt_deltas", "ember_fields" -> Blocks.BLACKSTONE.defaultBlockState();
            default -> Blocks.AIR.defaultBlockState();
        };
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        super.applyBiomeDecoration(level, chunk, structureManager);
        decorate(level, chunk);
        if (NetherOverhaulConfig.GENERATE_STRUCTURES.get()) {
            placeStructure(level, chunk);
        }
    }

    private void decorate(WorldGenLevel level, ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        RandomSource random = RandomSource.create(chunkPos.toLong() ^ level.getSeed());
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int lava = NetherOverhaulConfig.LAVA_SEA_LEVEL.get();

        for (int i = 0; i < 40; i++) {
            int x = minX + random.nextInt(16);
            int z = minZ + random.nextInt(16);
            int y = lava + 8 + random.nextInt(110);
            cursor.set(x, y, z);
            if (!level.getBlockState(cursor).isAir() || !level.getBlockState(cursor.below()).isSolid()) {
                continue;
            }
            if (level.getBlockState(cursor.below()).is(Blocks.LAVA) || level.getBlockState(cursor.below()).is(Blocks.BEDROCK)) {
                continue;
            }
            Holder<Biome> biome = level.getBiome(cursor);
            String path = biome.unwrapKey().map(key -> key.location().getPath()).orElse("");
            switch (path) {
                case "crimson_forest" -> {
                    if (random.nextFloat() < 0.12F) {
                        NetherStructures.placeHugeFungus(level, cursor.immutable(), true, random);
                    } else {
                        level.setBlock(cursor, random.nextFloat() < 0.45F
                                ? Blocks.CRIMSON_FUNGUS.defaultBlockState()
                                : Blocks.CRIMSON_ROOTS.defaultBlockState(), 2);
                    }
                }
                case "warped_forest" -> {
                    if (random.nextFloat() < 0.12F) {
                        NetherStructures.placeHugeFungus(level, cursor.immutable(), false, random);
                    } else if (random.nextFloat() < 0.35F) {
                        hangTwistingVines(level, cursor.immutable(), 2 + random.nextInt(4));
                    } else {
                        level.setBlock(cursor, random.nextBoolean()
                                ? Blocks.WARPED_FUNGUS.defaultBlockState()
                                : Blocks.WARPED_ROOTS.defaultBlockState(), 2);
                    }
                }
                case "soul_sand_valley", "wither_ruins" -> {
                    if (random.nextFloat() < 0.18F) {
                        level.setBlock(cursor.below(), ModBlocks.QUICK_SOUL_SAND.get().defaultBlockState(), 2);
                    } else if (random.nextFloat() < 0.25F) {
                        level.setBlock(cursor.below(), Blocks.SOUL_SAND.defaultBlockState(), 2);
                    }
                    if (random.nextFloat() < 0.12F) {
                        NetherStructures.placeGlowstoneBerryBush(level, cursor.immutable(), random);
                    } else if (path.equals("wither_ruins") && random.nextFloat() < 0.08F) {
                        NetherStructures.placeWitherTree(level, cursor.immutable(), random);
                    } else if (random.nextFloat() < 0.4F) {
                        level.setBlock(cursor, Blocks.NETHER_SPROUTS.defaultBlockState(), 2);
                    } else if (random.nextFloat() < 0.15F) {
                        level.setBlock(cursor, Blocks.BONE_BLOCK.defaultBlockState(), 2);
                    }
                }
                case "basalt_deltas" -> {
                    if (random.nextFloat() < 0.5F) {
                        int h = 2 + random.nextInt(4);
                        for (int dy = 0; dy < h; dy++) {
                            if (level.getBlockState(cursor.above(dy)).isAir()) {
                                level.setBlock(cursor.above(dy), Blocks.BASALT.defaultBlockState(), 2);
                            }
                        }
                    }
                }
                case "quartz_peaks" -> {
                    if (random.nextFloat() < 0.12F) {
                        NetherStructures.placeGlowstoneTree(level, cursor.immutable(), random);
                    } else if (random.nextFloat() < 0.35F) {
                        level.setBlock(cursor.below(), Blocks.QUARTZ_BLOCK.defaultBlockState(), 2);
                    }
                }
                case "ashen_swamp", "weeping_hollow" -> {
                    if (random.nextFloat() < 0.1F) {
                        NetherStructures.placeGlowstoneBerryBush(level, cursor.immutable(), random);
                    } else {
                        level.setBlock(cursor, random.nextBoolean()
                                ? Blocks.RED_MUSHROOM.defaultBlockState()
                                : Blocks.BROWN_MUSHROOM.defaultBlockState(), 2);
                    }
                }
                case "ember_fields" -> {
                    if (random.nextFloat() < 0.35F) {
                        level.setBlock(cursor.below(), Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
                    } else if (random.nextFloat() < 0.2F) {
                        level.setBlock(cursor, Blocks.FIRE.defaultBlockState(), 2);
                    }
                }
                default -> {
                    if (random.nextFloat() < 0.08F) {
                        level.setBlock(cursor, Blocks.CRIMSON_ROOTS.defaultBlockState(), 2);
                    }
                }
            }
        }

        for (int i = 0; i < 16; i++) {
            int x = minX + random.nextInt(16);
            int z = minZ + random.nextInt(16);
            int startY = 168 + random.nextInt(50);
            for (int y = startY; y >= 150; y--) {
                cursor.set(x, y, z);
                BlockState here = level.getBlockState(cursor);
                BlockState above = level.getBlockState(cursor.above());
                if (!here.isAir() || !above.isSolid() || above.is(Blocks.BEDROCK)) {
                    continue;
                }
                if (random.nextFloat() < 0.55F) {
                    level.setBlock(cursor, Blocks.GLOWSTONE.defaultBlockState(), 2);
                    if (random.nextBoolean() && level.getBlockState(cursor.below()).isAir()) {
                        level.setBlock(cursor.below(), Blocks.GLOWSTONE.defaultBlockState(), 2);
                    }
                } else {
                    hangWeepingVines(level, cursor.immutable(), 2 + random.nextInt(5));
                }
                break;
            }
        }

        var extras = level.registryAccess().registryOrThrow(Registries.BLOCK).getTag(EXTRA_NETHER_ORES);
        extras.ifPresent(tag -> {
            if (tag.size() == 0) {
                return;
            }
            for (int i = 0; i < NetherOverhaulConfig.EXTRA_ORE_ATTEMPTS.get(); i++) {
                int x = minX + random.nextInt(16);
                int z = minZ + random.nextInt(16);
                int y = 16 + random.nextInt(90);
                cursor.set(x, y, z);
                BlockState current = level.getBlockState(cursor);
                if (current.is(Blocks.NETHERRACK) || current.is(Blocks.BLACKSTONE) || current.is(Blocks.BASALT)) {
                    Block ore = tag.get(random.nextInt(tag.size())).value();
                    level.setBlock(cursor, ore.defaultBlockState(), 2);
                }
            }
        });
    }

    private static void hangWeepingVines(WorldGenLevel level, BlockPos start, int length) {
        BlockPos.MutableBlockPos cursor = start.mutable();
        for (int i = 0; i < length; i++) {
            if (!level.getBlockState(cursor).isAir()) {
                break;
            }
            boolean tip = i == length - 1 || !level.getBlockState(cursor.below()).isAir();
            level.setBlock(cursor, (tip ? Blocks.WEEPING_VINES : Blocks.WEEPING_VINES_PLANT).defaultBlockState(), 2);
            if (tip) {
                break;
            }
            cursor.move(0, -1, 0);
        }
    }

    private static void hangTwistingVines(WorldGenLevel level, BlockPos start, int length) {
        BlockPos.MutableBlockPos cursor = start.mutable();
        for (int i = 0; i < length; i++) {
            if (!level.getBlockState(cursor).isAir()) {
                break;
            }
            boolean tip = i == length - 1 || !level.getBlockState(cursor.above()).isAir();
            level.setBlock(cursor, (tip ? Blocks.TWISTING_VINES : Blocks.TWISTING_VINES_PLANT).defaultBlockState(), 2);
            if (tip) {
                break;
            }
            cursor.move(0, 1, 0);
        }
    }

    private void placeStructure(WorldGenLevel level, ChunkAccess chunk) {
        ChunkPos pos = chunk.getPos();
        long seed = level.getSeed() ^ pos.toLong();
        RandomSource random = RandomSource.create(seed);
        int x = pos.getMinBlockX() + 8;
        int z = pos.getMinBlockZ() + 8;
        int y = floorSurfaceY(chunk, 8, 8);
        if (y < 32) {
            return;
        }
        BlockPos origin = new BlockPos(x, y, z);
        BlockState ground = level.getBlockState(origin.below());
        if (!ground.isSolid() || ground.is(Blocks.LAVA) || ground.is(Blocks.MAGMA_BLOCK) || ground.is(Blocks.BEDROCK)) {
            return;
        }

        int towerEvery = NetherOverhaulConfig.TOWER_SPACING.get();
        int hamletEvery = NetherOverhaulConfig.HAMLET_SPACING.get();
        int ruinEvery = NetherOverhaulConfig.RUIN_SPACING.get();

        NetherStructureLayout.Kind kind = NetherStructureLayout.kindAt(pos);
        if (kind == null) {
            return;
        }
        switch (kind) {
            case TOWER -> NetherStructures.placeWatchtower(level, origin, random, seed);
            case HAMLET -> NetherStructures.placeHamlet(level, origin, random, seed + 7);
            case PIT -> NetherStructures.placeWitherPit(level, origin, random, seed + 13);
            case NEST -> NetherStructures.placeGhastNest(level, origin, random, seed + 17);
            case FORGE -> NetherStructures.placeBasaltForge(level, origin, random, seed + 19);
            case SHRINE -> NetherStructures.placeQuartzShrine(level, origin, random, seed + 23);
            case WELL -> NetherStructures.placeObsidianWell(level, origin, random, seed + 29);
            case DUNGEON -> NetherStructures.placeNetherDungeon(level, origin, random, seed + 31);
            case RING -> NetherStructures.placeFireRing(level, origin, random);
            case BLAZE -> NetherStructures.placeBlazeTower(level, origin, random, seed + 37);
            case FIRE_TOWER -> NetherStructures.placeFireTower(level, origin, random);
        }
    }

    /**
     * Walks the walkable nether floor only. Searching from the world top matches
     * roof pockets and high ceiling ledges, which is how towers used to spawn on the roof.
     */
    private static int floorSurfaceY(ChunkAccess chunk, int lx, int lz) {
        int lava = NetherOverhaulConfig.LAVA_SEA_LEVEL.get();
        int min = Math.max(chunk.getMinBuildHeight() + 8, lava + 4);
        int max = 155;
        int worldX = chunk.getPos().getMinBlockX() + lx;
        int worldZ = chunk.getPos().getMinBlockZ() + lz;
        for (int y = max; y >= min; y--) {
            BlockState state = chunk.getBlockState(new BlockPos(worldX, y, worldZ));
            BlockState above = chunk.getBlockState(new BlockPos(worldX, y + 1, worldZ));
            if (state.isSolid() && above.isAir() && !state.is(Blocks.LAVA) && !state.is(Blocks.BEDROCK) && !state.is(Blocks.GLOWSTONE)) {
                return y + 1;
            }
        }
        return -1;
    }

    @Override
    public void createStructures(RegistryAccess registryAccess,
                                 ChunkGeneratorStructureState structureState,
                                 StructureManager structureManager,
                                 ChunkAccess chunk,
                                 StructureTemplateManager templateManager) {
        if (NetherOverhaulConfig.PLACE_VANILLA_NETHER_STRUCTURES.get()) {
            super.createStructures(registryAccess, structureState, structureManager, chunk, templateManager);
        }
    }

    @Override
    public void applyCarvers(WorldGenRegion region,
                             long seed,
                             RandomState randomState,
                             BiomeManager biomeManager,
                             StructureManager structureManager,
                             ChunkAccess chunk,
                             GenerationStep.Carving step) {
        // Ravines are carved in fillFromNoise.
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // Regular nether spawning still runs from biome spawn lists.
    }

    @Override
    public int getGenDepth() {
        return 256;
    }

    @Override
    public int getSeaLevel() {
        return NetherOverhaulConfig.LAVA_SEA_LEVEL.get();
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        return Mth.clamp(noise(randomState).floorHeight(x, z) + 1, level.getMinBuildHeight() + 8, level.getMaxBuildHeight() - 16);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        NetherNoise noise = noise(randomState);
        int minY = level.getMinBuildHeight();
        int height = level.getHeight();
        int floor = noise.floorHeight(x, z);
        int roof = noise.roofHeight(x, z);
        boolean pillar = noise.pillar(x, z);
        int lava = NetherOverhaulConfig.LAVA_SEA_LEVEL.get();
        BlockState[] states = new BlockState[height];
        for (int i = 0; i < height; i++) {
            int y = minY + i;
            states[i] = columnState(noise, x, y, z, minY, minY + height, floor, roof, pillar, lava);
        }
        return new NoiseColumn(minY, states);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        info.add("WT Nether Overhaul | floor " + noise(randomState).floorHeight(pos.getX(), pos.getZ()));
    }

    private static NetherNoise noise(RandomState randomState) {
        long seed = randomState.getOrCreateRandomFactory(GRID_SALT).at(0, 0, 0).nextLong();
        return new NetherNoise(seed);
    }
}
