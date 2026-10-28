package dev.whoteva.netheroverhaul.condition;

import com.mojang.serialization.MapCodec;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

public final class RiftShardCraftingCondition implements ICondition {

    public static final RiftShardCraftingCondition INSTANCE = new RiftShardCraftingCondition();
    public static final MapCodec<RiftShardCraftingCondition> CODEC = MapCodec.unit(INSTANCE);

    private RiftShardCraftingCondition() {
    }

    @Override
    public boolean test(IContext context) {
        try {
            return NetherOverhaulConfig.ALLOW_RIFT_SHARD_ASSEMBLY.get();
        } catch (IllegalStateException ignored) {
            return true;
        }
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
