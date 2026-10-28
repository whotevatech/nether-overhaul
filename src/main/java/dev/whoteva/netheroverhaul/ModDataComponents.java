package dev.whoteva.netheroverhaul;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, NetherOverhaul.MOD_ID);

    /** 0 = nearest of any structure. 1+ is {@code NetherStructureLayout.Kind} ordinal + 1. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SEEKER_FILTER =
            COMPONENTS.register("seeker_filter", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    private ModDataComponents() {
    }
}
