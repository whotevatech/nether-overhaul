package dev.whoteva.netheroverhaul.item;

import dev.whoteva.netheroverhaul.ModAttachments;
import dev.whoteva.netheroverhaul.NetherOverhaul;
import dev.whoteva.netheroverhaul.worldgen.NetherLore;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
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

public class RiftKeyItem extends Item {

    public RiftKeyItem(Properties properties) {
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

        if (ModAttachments.isUnlocked(serverPlayer)) {
            serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.already_unlocked"), true);
            return InteractionResultHolder.fail(stack);
        }

        ModAttachments.setUnlocked(serverPlayer, true);
        if (!serverPlayer.getAbilities().instabuild) {
            stack.shrink(1);
        }
        level.playSound(null, serverPlayer.blockPosition(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 0.7F, 1.15F);
        NetherLore.showTitle(serverPlayer, "title.netheroverhaul.unlocked", "subtitle.netheroverhaul.unlocked");
        serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.unlocked"), false);

        AdvancementHolder opened = serverPlayer.server.getAdvancements().get(NetherOverhaul.id("open_rift"));
        if (opened != null) {
            serverPlayer.getAdvancements().award(opened, "used_key");
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheroverhaul.rift_key.desc").withStyle(ChatFormatting.GRAY));
    }
}
