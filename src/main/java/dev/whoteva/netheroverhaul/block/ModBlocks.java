package dev.whoteva.netheroverhaul.block;

import dev.whoteva.netheroverhaul.NetherOverhaul;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NetherOverhaul.MOD_ID);

    public static final DeferredBlock<GlowstoneCropBlock> GLOWSTONE_CROP =
            BLOCKS.register("glowstone_crop", GlowstoneCropBlock::new);

    public static final DeferredBlock<QuickSoulSandBlock> QUICK_SOUL_SAND =
            BLOCKS.register("quick_soul_sand", QuickSoulSandBlock::new);

    private ModBlocks() {
    }
}
