package dev.whoteva.netheroverhaul.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class GlowstoneBerryItem extends Item {

    public GlowstoneBerryItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheroverhaul.glowstone_berry.desc").withStyle(ChatFormatting.GRAY));
    }
}
