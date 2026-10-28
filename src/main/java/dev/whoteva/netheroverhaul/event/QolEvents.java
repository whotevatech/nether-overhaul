package dev.whoteva.netheroverhaul.event;

import dev.whoteva.netheroverhaul.KeptGear;
import dev.whoteva.netheroverhaul.ModAttachments;
import dev.whoteva.netheroverhaul.NetherOverhaul;
import dev.whoteva.netheroverhaul.NetherOverhaulConfig;
import dev.whoteva.netheroverhaul.RiftScoreboard;
import dev.whoteva.netheroverhaul.entity.ModEntities;
import dev.whoteva.netheroverhaul.entity.NetherCreeper;
import dev.whoteva.netheroverhaul.item.ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.ArrayList;
import java.util.List;

public final class QolEvents {

    private QolEvents() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        RiftScoreboard.ensure(event.getServer());
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RiftScoreboard.sync(player, ModAttachments.isUnlocked(player));
            restoreGear(player);
        }
    }

    /**
     * Player items are removed in {@code Inventory.dropAll} before the clone event, and that path
     * does not honour a cancelled {@link LivingDropsEvent}. Snapshot first, then empty the inventory
     * so nothing hits the ground.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer player) || !shouldKeep(player)) {
            awardSlain(event);
            return;
        }
        snapshot(player);
        awardSlain(event);
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && shouldKeep(player)) {
            event.getDrops().clear();
            event.setCanceled(true);
            return;
        }
        int shards = 0;
        if (event.getEntity().getTags().contains("netheroverhaul_cinder_warden")) {
            shards = 1;
        } else if (event.getEntity().getTags().contains("netheroverhaul_rift_warden")) {
            shards = 2;
        }
        if (shards <= 0 || !(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }
        ItemStack stack = new ItemStack(ModItems.RIFT_SHARD.get(), shards);
        event.getDrops().add(new ItemEntity(level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack));
    }

    @SubscribeEvent
    public static void onXp(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && shouldKeep(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!(event.getOriginal() instanceof ServerPlayer previous)) {
            return;
        }
        KeptGear gear = previous.getData(ModAttachments.KEPT_GEAR);
        if (!gear.saved()) {
            gear = player.getData(ModAttachments.KEPT_GEAR);
        }
        if (!gear.saved()) {
            return;
        }
        apply(player, gear);
    }

    private static void snapshot(ServerPlayer player) {
        List<KeptGear.SlotStack> slots = new ArrayList<>();
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                slots.add(new KeptGear.SlotStack(i, stack.copy()));
            }
        }
        List<ItemStack> extra = new ArrayList<>();
        for (Slot slot : player.inventoryMenu.slots) {
            if (slot instanceof ResultSlot) {
                slot.set(ItemStack.EMPTY);
                continue;
            }
            if (slot.container == inventory) {
                continue;
            }
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty()) {
                extra.add(stack.copy());
                slot.set(ItemStack.EMPTY);
            }
        }
        player.setData(ModAttachments.KEPT_GEAR, new KeptGear(
                true,
                inventory.selected,
                slots,
                extra,
                player.experienceLevel,
                player.experienceProgress,
                player.totalExperience
        ));
        inventory.clearContent();
    }

    private static void restoreGear(ServerPlayer player) {
        KeptGear gear = player.getData(ModAttachments.KEPT_GEAR);
        if (!gear.saved()) {
            return;
        }
        apply(player, gear);
    }

    private static void apply(ServerPlayer player, KeptGear gear) {
        var inventory = player.getInventory();
        for (KeptGear.SlotStack slot : gear.slots()) {
            if (slot.slot() >= 0 && slot.slot() < inventory.getContainerSize()) {
                inventory.setItem(slot.slot(), slot.stack().copy());
            }
        }
        for (ItemStack extra : gear.extra()) {
            if (!inventory.add(extra.copy())) {
                player.drop(extra.copy(), false);
            }
        }
        int selected = gear.selected();
        if (selected >= 0 && selected < 9) {
            inventory.selected = selected;
        }
        player.experienceLevel = gear.level();
        player.experienceProgress = gear.progress();
        player.totalExperience = gear.total();
        player.setData(ModAttachments.KEPT_GEAR, KeptGear.EMPTY);
    }

    @SubscribeEvent
    public static void onCreeperBlast(ExplosionEvent.Detonate event) {
        if (!(event.getExplosion().getDirectSourceEntity() instanceof NetherCreeper creeper)) {
            return;
        }
        double x = creeper.getX();
        double y = creeper.getY();
        double z = creeper.getZ();
        event.getAffectedBlocks().removeIf(pos -> {
            double dx = pos.getX() + 0.5D - x;
            double dy = pos.getY() + 0.5D - y;
            double dz = pos.getZ() + 0.5D - z;
            return dx * dx + dy * dy + dz * dz > 4.0D;
        });
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos origin = BlockPos.containing(x, y, z);
        level.getServer().execute(() -> igniteCrater(level, origin));
    }

    private static void igniteCrater(ServerLevel level, BlockPos origin) {
        RandomSource random = level.getRandom();
        for (int i = 0; i < 8; i++) {
            BlockPos pos = origin.offset(random.nextInt(5) - 2, random.nextInt(3) - 1, random.nextInt(5) - 2);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolid()) {
                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            }
        }
    }

    private static void awardSlain(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) {
            return;
        }
        EntityType<?> type = event.getEntity().getType();
        if (type == ModEntities.NETHER_CREEPER.get() || type == ModEntities.NETHER_SPIDER.get() || type == ModEntities.NETHER_ZOMBIE.get()) {
            award(killer, "nether_mob");
        }
        if (event.getEntity().getTags().contains("netheroverhaul_cinder_warden")) {
            award(killer, "cinder_warden");
        }
        if (event.getEntity().getTags().contains("netheroverhaul_rift_warden")) {
            award(killer, "rift_warden");
        }
    }

    public static void award(ServerPlayer player, String path) {
        AdvancementHolder holder = player.server.getAdvancements().get(NetherOverhaul.id(path));
        if (holder != null && !player.getAdvancements().getOrStartProgress(holder).isDone()) {
            player.getAdvancements().award(holder, "done");
        }
    }

    private static boolean shouldKeep(ServerPlayer player) {
        if (player == null || !NetherOverhaulConfig.KEEP_INVENTORY_UNTIL_UNLOCKED.get()) {
            return false;
        }
        return !ModAttachments.isUnlocked(player);
    }
}
