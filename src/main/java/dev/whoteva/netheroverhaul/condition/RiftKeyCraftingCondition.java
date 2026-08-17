package dev.whoteva.netheroverhaul.condition;

import com.mojang.serialization.MapCodec;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

public final class RiftKeyCraftingCondition implements ICondition {

    public static final RiftKeyCraftingCondition INSTANCE = new RiftKeyCraftingCondition();
    public static final MapCodec<RiftKeyCraftingCondition> CODEC = MapCodec.unit(INSTANCE);

    private RiftKeyCraftingCondition() {
    }

    @Override
    public boolean test(IContext context) {
        try {
            return NetherOverhaulConfig.ALLOW_RIFT_KEY_CRAFTING.get();
        } catch (IllegalStateException ignored) {
            return true;
        }
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
