package dev.whoteva.netheroverhaul.item;

import dev.whoteva.netheroverhaul.ModAttachments;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;

public class RecallCharmItem extends Item {

    public RecallCharmItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }

        ServerLevel nether = serverPlayer.server.getLevel(Level.NETHER);
        if (nether == null) {
            return InteractionResultHolder.fail(stack);
        }

        BlockPos camp = ModAttachments.campPos(serverPlayer);
        if (camp.equals(BlockPos.ZERO)) {
            serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.recall_none"), true);
            return InteractionResultHolder.fail(stack);
        }

        int min = NetherOverhaulConfig.RECALL_MIN_DISTANCE.get();
        if (min > 0 && serverPlayer.level().dimension() == Level.NETHER && serverPlayer.distanceToSqr(camp.getX() + 0.5D, camp.getY(), camp.getZ() + 0.5D) <= (double) min * min) {
            serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.recall_here"), true);
            return InteractionResultHolder.fail(stack);
        }

        BlockPos dest = safeLanding(nether, camp);
        serverPlayer.stopRiding();
        level.playSound(null, serverPlayer.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.6F, 0.8F);
        serverPlayer.teleportTo(nether, dest.getX() + 0.5D, dest.getY(), dest.getZ() + 0.5D, Set.of(), serverPlayer.getYRot(), serverPlayer.getXRot());
        int fire = NetherOverhaulConfig.RECALL_FIRE_RESISTANCE_TICKS.get();
        if (fire > 0) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, fire, 0, false, true, true));
        }
        int slow = NetherOverhaulConfig.RECALL_SLOW_FALLING_TICKS.get();
        if (slow > 0) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, slow, 0, false, true, true));
        }
        nether.playSound(null, dest, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.8F, 1.1F);
        serverPlayer.getCooldowns().addCooldown(this, NetherOverhaulConfig.RECALL_COOLDOWN_TICKS.get());
        serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.recalled"), true);
        return InteractionResultHolder.success(stack);
    }

    private static BlockPos safeLanding(ServerLevel nether, BlockPos camp) {
        for (int i = 0; i < 8; i++) {
            BlockPos feet = camp.above(i);
            BlockState below = nether.getBlockState(feet.below());
            if (!nether.getBlockState(feet).isAir() || !nether.getBlockState(feet.above()).isAir()) {
                continue;
            }
            if (!below.isSolid() || below.is(Blocks.MAGMA_BLOCK) || below.is(Blocks.LAVA)) {
                continue;
            }
            return feet;
        }
        return camp;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheroverhaul.recall_charm.desc").withStyle(ChatFormatting.GRAY));
    }
}
