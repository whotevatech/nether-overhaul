package dev.whoteva.netheroverhaul;

import com.mojang.serialization.MapCodec;
import dev.whoteva.netheroverhaul.block.ModBlocks;
import dev.whoteva.netheroverhaul.command.ModCommands;
import dev.whoteva.netheroverhaul.condition.RiftKeyCraftingCondition;
import dev.whoteva.netheroverhaul.entity.ModEntities;
import dev.whoteva.netheroverhaul.event.QolEvents;
import dev.whoteva.netheroverhaul.event.SpawnAndLockEvents;
import dev.whoteva.netheroverhaul.item.ModItems;
import dev.whoteva.netheroverhaul.worldgen.OverhauledNetherBiomeSource;
import dev.whoteva.netheroverhaul.worldgen.OverhauledNetherChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(NetherOverhaul.MOD_ID)
public final class NetherOverhaul {

    public static final String MOD_ID = "netheroverhaul";

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MOD_ID);

    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, MOD_ID);

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MOD_ID);

    static {
        CHUNK_GENERATORS.register("overhauled", () -> OverhauledNetherChunkGenerator.CODEC);
        BIOME_SOURCES.register("layered", () -> OverhauledNetherBiomeSource.CODEC);
        CONDITION_CODECS.register("rift_key_crafting", () -> RiftKeyCraftingCondition.CODEC);
    }

    public NetherOverhaul(IEventBus modBus, ModContainer container) {
        CHUNK_GENERATORS.register(modBus);
        BIOME_SOURCES.register(modBus);
        CONDITION_CODECS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        ModAttachments.ATTACHMENTS.register(modBus);

        modBus.addListener(ModCreativeTabs::buildContents);
        container.registerConfig(ModConfig.Type.SERVER, NetherOverhaulConfig.SPEC);

        NeoForge.EVENT_BUS.register(SpawnAndLockEvents.class);
        NeoForge.EVENT_BUS.register(QolEvents.class);
        NeoForge.EVENT_BUS.register(ModCommands.class);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
