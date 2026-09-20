package dev.whoteva.netheroverhaul.block;

import dev.whoteva.netheroverhaul.NetherOverhaul;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NetherOverhaul.MOD_ID);

    public static final DeferredBlock<GlowstoneCropBlock> GLOWSTONE_CROP =
            BLOCKS.register("glowstone_crop", GlowstoneCropBlock::new);

    public static final DeferredBlock<QuickSoulSandBlock> QUICK_SOUL_SAND =
            BLOCKS.register("quick_soul_sand", QuickSoulSandBlock::new);

    public static final DeferredBlock<Block> NETHER_IRON_ORE =
            BLOCKS.register("nether_iron_ore", ModBlocks::packOre);

    public static final DeferredBlock<Block> NETHER_COPPER_ORE =
            BLOCKS.register("nether_copper_ore", ModBlocks::packOre);

    public static final DeferredBlock<Block> NETHER_COAL_ORE =
            BLOCKS.register("nether_coal_ore", () -> new DropExperienceBlock(
                    UniformInt.of(0, 2),
                    packOreProperties()));

    public static final DeferredBlock<RedStoneOreBlock> NETHER_REDSTONE_ORE =
            BLOCKS.register("nether_redstone_ore", () -> new RedStoneOreBlock(
                    packOreProperties()
                            .randomTicks()
                            .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 9 : 0)));

    private ModBlocks() {
    }

    private static Block packOre() {
        return new Block(packOreProperties());
    }

    private static BlockBehaviour.Properties packOreProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.NETHER)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 3.0F)
                .sound(SoundType.NETHER_ORE);
    }
}
