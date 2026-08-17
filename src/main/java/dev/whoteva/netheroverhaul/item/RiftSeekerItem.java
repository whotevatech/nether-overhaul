package dev.whoteva.netheroverhaul.item;

import dev.whoteva.netheroverhaul.worldgen.NetherStructureLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class RiftSeekerItem extends Item {

    public RiftSeekerItem(Properties properties) {
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

        NetherStructureLayout.Hit hit = NetherStructureLayout.nearest(serverPlayer.blockPosition(), null, 48);
        if (hit == null) {
            serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.seeker_none"), true);
            return InteractionResultHolder.fail(stack);
        }

        serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.seeker", hit.describe(serverPlayer.blockPosition())), false);
        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 0.6F, 1.35F);
        serverPlayer.getCooldowns().addCooldown(this, 40);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheroverhaul.rift_seeker.desc").withStyle(ChatFormatting.GRAY));
    }
}
