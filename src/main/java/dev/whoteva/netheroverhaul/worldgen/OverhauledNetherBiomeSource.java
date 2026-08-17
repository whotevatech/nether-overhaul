package dev.whoteva.netheroverhaul.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import java.util.stream.Stream;

public class OverhauledNetherBiomeSource extends BiomeSource {

    public static final MapCodec<OverhauledNetherBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.CODEC.listOf().fieldOf("biomes").forGetter(source -> source.biomes)
            ).apply(instance, OverhauledNetherBiomeSource::new));

    private final List<Holder<Biome>> biomes;
    private final NetherNoise noise;

    public OverhauledNetherBiomeSource(List<Holder<Biome>> biomes) {
        this.biomes = List.copyOf(biomes);
        this.noise = new NetherNoise(0x4E4F495345L);
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return biomes.stream();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        if (biomes.isEmpty()) {
            throw new IllegalStateException("netheroverhaul:layered biome source has no biomes");
        }
        return biomes.get(noise.biomeIndex(x, z, biomes.size()));
    }
}
