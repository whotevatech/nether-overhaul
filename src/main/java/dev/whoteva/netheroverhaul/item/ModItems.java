package dev.whoteva.netheroverhaul.item;

import dev.whoteva.netheroverhaul.NetherOverhaul;
import dev.whoteva.netheroverhaul.block.ModBlocks;
import dev.whoteva.netheroverhaul.entity.ModEntities;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NetherOverhaul.MOD_ID);

    public static final DeferredHolder<Item, RiftKeyItem> RIFT_KEY = ITEMS.register(
            "rift_key",
            () -> new RiftKeyItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    public static final DeferredHolder<Item, RiftSeekerItem> RIFT_SEEKER = ITEMS.register(
            "rift_seeker",
            () -> new RiftSeekerItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final DeferredHolder<Item, RecallCharmItem> RECALL_CHARM = ITEMS.register(
            "recall_charm",
            () -> new RecallCharmItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant()));

    public static final DeferredHolder<Item, SealedFlaskItem> SEALED_FLASK = ITEMS.register(
            "sealed_flask",
            () -> new SealedFlaskItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final DeferredHolder<Item, RiftShardItem> RIFT_SHARD = ITEMS.register(
            "rift_shard",
            () -> new RiftShardItem(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));

    public static final DeferredItem<Item> GLOWSTONE_SEED = ITEMS.register(
            "glowstone_seed",
            () -> new ItemNameBlockItem(ModBlocks.GLOWSTONE_CROP.get(), new Item.Properties()));

    public static final DeferredItem<GlowstoneBerryItem> GLOWSTONE_BERRY = ITEMS.register(
            "glowstone_berry",
            () -> new GlowstoneBerryItem(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.4F)
                    .alwaysEdible()
                    .fast()
                    .effect(() -> new MobEffectInstance(MobEffects.GLOWING, 600, 0), 1.0F)
                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0), 1.0F)
                    .build())));

    public static final DeferredItem<QuickSoulSandItem> QUICK_SOUL_SAND = ITEMS.register(
            "quick_soul_sand",
            () -> new QuickSoulSandItem(ModBlocks.QUICK_SOUL_SAND.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> NETHER_IRON_ORE = ITEMS.registerSimpleBlockItem(
            "nether_iron_ore", ModBlocks.NETHER_IRON_ORE);

    public static final DeferredItem<BlockItem> NETHER_COPPER_ORE = ITEMS.registerSimpleBlockItem(
            "nether_copper_ore", ModBlocks.NETHER_COPPER_ORE);

    public static final DeferredItem<BlockItem> NETHER_COAL_ORE = ITEMS.registerSimpleBlockItem(
            "nether_coal_ore", ModBlocks.NETHER_COAL_ORE);

    public static final DeferredItem<BlockItem> NETHER_REDSTONE_ORE = ITEMS.registerSimpleBlockItem(
            "nether_redstone_ore", ModBlocks.NETHER_REDSTONE_ORE);

    public static final DeferredItem<SpawnEggItem> NETHER_CREEPER_EGG = ITEMS.register(
            "nether_creeper_spawn_egg",
            () -> new SpawnEggItem(ModEntities.NETHER_CREEPER.get(), 0x3A1E1A, 0xFF6A1A, new Item.Properties()));

    public static final DeferredItem<SpawnEggItem> NETHER_SPIDER_EGG = ITEMS.register(
            "nether_spider_spawn_egg",
            () -> new SpawnEggItem(ModEntities.NETHER_SPIDER.get(), 0x2C1214, 0xE25822, new Item.Properties()));

    public static final DeferredItem<SpawnEggItem> NETHER_ZOMBIE_EGG = ITEMS.register(
            "nether_zombie_spawn_egg",
            () -> new SpawnEggItem(ModEntities.NETHER_ZOMBIE.get(), 0x4A2A24, 0xC45A32, new Item.Properties()));

    private ModItems() {
    }
}
