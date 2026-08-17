package dev.whoteva.netheroverhaul.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Nether-safe water: fills cauldrons, or places a source if the dimension allows it.
 */
public class SealedFlaskItem extends Item {

    public SealedFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();

        if (state.is(Blocks.CAULDRON) || state.is(Blocks.WATER_CAULDRON)) {
            if (!level.isClientSide) {
                level.setBlock(pos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), 3);
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 0.8F, 1.15F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        BlockPos place = pos.relative(context.getClickedFace());
        if (level.getBlockState(place).isAir()) {
            if (!level.isClientSide) {
                if (level.dimensionType().ultraWarm()) {
                    if (context.getPlayer() != null) {
                        context.getPlayer().displayClientMessage(Component.translatable("message.netheroverhaul.flask_cauldron"), true);
                    }
                    return InteractionResult.FAIL;
                }
                level.setBlock(place, Blocks.WATER.defaultBlockState(), 3);
                level.playSound(null, place, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.8F, 1.0F);
                if (context.getPlayer() != null) {
                    context.getPlayer().getCooldowns().addCooldown(this, 40);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheroverhaul.sealed_flask.desc").withStyle(ChatFormatting.GRAY));
    }
}
