package dev.whoteva.netheroverhaul.item;

import dev.whoteva.netheroverhaul.ModDataComponents;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import dev.whoteva.netheroverhaul.worldgen.NetherStructureLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

        if (player.isShiftKeyDown()) {
            int next = nextFilter(stack.getOrDefault(ModDataComponents.SEEKER_FILTER.get(), 0));
            stack.set(ModDataComponents.SEEKER_FILTER.get(), next);
            serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.seeker_filter", filterName(next)), true);
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 0.35F, 1.7F);
            return InteractionResultHolder.success(stack);
        }

        if (!NetherOverhaulConfig.GENERATE_STRUCTURES.get()) {
            serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.seeker_none"), true);
            return InteractionResultHolder.fail(stack);
        }

        NetherStructureLayout.Kind filter = filterKind(stack.getOrDefault(ModDataComponents.SEEKER_FILTER.get(), 0));
        int range = NetherOverhaulConfig.SEEKER_RANGE_CHUNKS.get();
        NetherStructureLayout.Hit hit = NetherStructureLayout.nearest(serverPlayer.blockPosition(), filter, range);
        if (hit == null) {
            serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.seeker_none"), true);
            return InteractionResultHolder.fail(stack);
        }

        serverPlayer.displayClientMessage(Component.translatable("message.netheroverhaul.seeker", hit.describe(serverPlayer.blockPosition())), false);
        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 0.6F, 1.35F);
        if (NetherOverhaulConfig.SEEKER_PARTICLES.get() && serverPlayer.level() instanceof ServerLevel server) {
            point(server, serverPlayer, hit);
        }
        serverPlayer.getCooldowns().addCooldown(this, NetherOverhaulConfig.SEEKER_COOLDOWN_TICKS.get());
        return InteractionResultHolder.success(stack);
    }

    private static void point(ServerLevel level, ServerPlayer player, NetherStructureLayout.Hit hit) {
        double dx = hit.origin().getX() + 0.5D - player.getX();
        double dz = hit.origin().getZ() + 0.5D - player.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0D) {
            return;
        }
        dx /= length;
        dz /= length;
        for (int i = 1; i <= 10; i++) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    player.getX() + dx * i,
                    player.getY() + 1.2D,
                    player.getZ() + dz * i,
                    1, 0.02D, 0.04D, 0.02D, 0.0D);
        }
    }

    private static int nextFilter(int current) {
        int count = NetherStructureLayout.Kind.values().length + 1;
        return Math.floorMod(current + 1, count);
    }

    private static NetherStructureLayout.Kind filterKind(int filter) {
        NetherStructureLayout.Kind[] kinds = NetherStructureLayout.Kind.values();
        if (filter <= 0 || filter > kinds.length) {
            return null;
        }
        return kinds[filter - 1];
    }

    static String filterName(int filter) {
        NetherStructureLayout.Kind kind = filterKind(filter);
        return kind == null ? "anything" : kind.label();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheroverhaul.rift_seeker.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.netheroverhaul.rift_seeker.sneak").withStyle(ChatFormatting.DARK_GRAY));
        int filter = stack.getOrDefault(ModDataComponents.SEEKER_FILTER.get(), 0);
        tooltip.add(Component.translatable("item.netheroverhaul.rift_seeker.filter", filterName(filter)).withStyle(ChatFormatting.DARK_AQUA));
    }
}
