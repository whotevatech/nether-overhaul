package dev.whoteva.netheroverhaul;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class ModLoot {

    public static final ResourceKey<LootTable> SPAWN_CAMP = key("chests/spawn_camp");
    public static final ResourceKey<LootTable> WATCHTOWER = key("chests/watchtower");
    public static final ResourceKey<LootTable> HAMLET = key("chests/hamlet");
    public static final ResourceKey<LootTable> WITHER_PIT = key("chests/wither_pit");
    public static final ResourceKey<LootTable> GHAST_NEST = key("chests/ghast_nest");
    public static final ResourceKey<LootTable> BASALT_FORGE = key("chests/basalt_forge");
    public static final ResourceKey<LootTable> QUARTZ_SHRINE = key("chests/quartz_shrine");
    public static final ResourceKey<LootTable> WELL = key("chests/obsidian_well");
    public static final ResourceKey<LootTable> DUNGEON = key("chests/nether_dungeon");
    public static final ResourceKey<LootTable> BLAZE_TOWER = key("chests/blaze_tower");
    public static final ResourceKey<LootTable> RIFT_CACHE = key("chests/rift_cache");
    public static final ResourceKey<LootTable> FORTRESS_BONUS = key("chests/fortress_bonus");
    public static final ResourceKey<LootTable> BASTION_BONUS = key("chests/bastion_bonus");
    public static final ResourceKey<LootTable> PIGLIN_BONUS = key("gameplay/piglin_bonus");

    private ModLoot() {
    }

    private static ResourceKey<LootTable> key(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, NetherOverhaul.id(path));
    }
}
