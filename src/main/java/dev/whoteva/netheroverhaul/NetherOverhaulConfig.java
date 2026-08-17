package dev.whoteva.netheroverhaul;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class NetherOverhaulConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue SPAWN_IN_NETHER;
    public static final ModConfigSpec.BooleanValue LOCK_OVERWORLD;
    public static final ModConfigSpec.BooleanValue CREATIVE_BYPASSES_LOCK;
    public static final ModConfigSpec.BooleanValue OPERATORS_BYPASS_LOCK;
    public static final ModConfigSpec.BooleanValue ALLOW_RIFT_KEY_CRAFTING;
    public static final ModConfigSpec.BooleanValue SPAWN_CAMP;
    public static final ModConfigSpec.BooleanValue GIVE_LORE_BOOK;
    public static final ModConfigSpec.BooleanValue GIVE_CAMP_COMPASS;
    public static final ModConfigSpec.BooleanValue GIVE_FLASK_AT_SPAWN;
    public static final ModConfigSpec.BooleanValue KEEP_INVENTORY_UNTIL_UNLOCKED;
    public static final ModConfigSpec.IntValue SPAWN_FIRE_RESISTANCE_TICKS;
    public static final ModConfigSpec.IntValue RECALL_COOLDOWN_TICKS;
    public static final ModConfigSpec.IntValue LOCK_MESSAGE_COOLDOWN_TICKS;
    public static final ModConfigSpec.BooleanValue GENERATE_STRUCTURES;
    public static final ModConfigSpec.BooleanValue PLACE_VANILLA_NETHER_STRUCTURES;
    public static final ModConfigSpec.IntValue LAVA_SEA_LEVEL;
    public static final ModConfigSpec.IntValue TOWER_SPACING;
    public static final ModConfigSpec.IntValue HAMLET_SPACING;
    public static final ModConfigSpec.IntValue RUIN_SPACING;
    public static final ModConfigSpec.IntValue NEST_SPACING;
    public static final ModConfigSpec.IntValue FORGE_SPACING;
    public static final ModConfigSpec.IntValue SHRINE_SPACING;
    public static final ModConfigSpec.IntValue EXTRA_ORE_ATTEMPTS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("progression");
        SPAWN_IN_NETHER = builder
                .comment("Teleport new players into the Nether on first join.")
                .define("spawnInNether", true);
        LOCK_OVERWORLD = builder
                .comment("Block Nether -> Overworld travel until the player uses a Rift Key.")
                .define("lockOverworld", true);
        CREATIVE_BYPASSES_LOCK = builder
                .comment("Creative-mode players ignore the Overworld lock.")
                .define("creativeBypassesLock", true);
        OPERATORS_BYPASS_LOCK = builder
                .comment("Permission level 2+ players ignore the Overworld lock.")
                .define("operatorsBypassLock", true);
        ALLOW_RIFT_KEY_CRAFTING = builder
                .comment("If true, a backup Rift Key recipe exists so players cannot soft-lock.")
                .define("allowRiftKeyCrafting", true);
        SPAWN_CAMP = builder
                .comment("Build a blackstone shelter, starter chest and nether-wart patch at first spawn.")
                .define("giveSpawnCamp", true);
        GIVE_LORE_BOOK = builder
                .comment("Give new players the Sealed Below written book.")
                .define("giveLoreBook", true);
        GIVE_CAMP_COMPASS = builder
                .comment("Give a lodestone compass bound to the spawn camp.")
                .define("giveCampCompass", true);
        GIVE_FLASK_AT_SPAWN = builder
                .comment("Give a Sealed Flask so cauldrons can be filled without overworld water.")
                .define("giveFlaskAtSpawn", true);
        KEEP_INVENTORY_UNTIL_UNLOCKED = builder
                .comment("Keep items and XP on death until the player unlocks the Overworld rift.")
                .define("keepInventoryUntilUnlocked", true);
        SPAWN_FIRE_RESISTANCE_TICKS = builder
                .comment("Fire resistance duration on first join, in ticks. 1200 is 60 seconds.")
                .defineInRange("spawnFireResistanceTicks", 1200, 0, 20 * 60 * 10);
        RECALL_COOLDOWN_TICKS = builder
                .comment("Cooldown on the Recall Charm, in ticks. 3600 is 3 minutes.")
                .defineInRange("recallCooldownTicks", 3600, 20, 20 * 60 * 30);
        LOCK_MESSAGE_COOLDOWN_TICKS = builder
                .comment("Minimum ticks between Overworld-lock titles when walking into a portal.")
                .defineInRange("lockMessageCooldownTicks", 80, 0, 20 * 30);
        builder.pop();

        builder.push("worldgen");
        GENERATE_STRUCTURES = builder
                .comment("Place watchtowers, hamlets, pits, nests, forges and shrines during chunk decoration.")
                .define("generateStructures", true);
        PLACE_VANILLA_NETHER_STRUCTURES = builder
                .comment("Also run vanilla fortress / bastion structure generation.")
                .define("placeVanillaNetherStructures", true);
        LAVA_SEA_LEVEL = builder
                .comment("Y level of the lava sea. Terrain sits above this.")
                .defineInRange("lavaSeaLevel", 24, 8, 64);
        TOWER_SPACING = builder
                .comment("Average chunks between watchtowers.")
                .defineInRange("towerSpacing", 8, 4, 32);
        HAMLET_SPACING = builder
                .comment("Average chunks between nether hamlets.")
                .defineInRange("hamletSpacing", 10, 4, 32);
        RUIN_SPACING = builder
                .comment("Average chunks between wither-ruin pits.")
                .defineInRange("ruinSpacing", 14, 6, 40);
        NEST_SPACING = builder
                .comment("Average chunks between ghast nests.")
                .defineInRange("nestSpacing", 12, 6, 40);
        FORGE_SPACING = builder
                .comment("Average chunks between basalt forges.")
                .defineInRange("forgeSpacing", 11, 6, 40);
        SHRINE_SPACING = builder
                .comment("Average chunks between quartz shrines.")
                .defineInRange("shrineSpacing", 13, 6, 40);
        EXTRA_ORE_ATTEMPTS = builder
                .comment("How many times per chunk to try placing a block from #netheroverhaul:extra_nether_ores.")
                .defineInRange("extraOreAttempts", 16, 0, 64);
        builder.pop();

        SPEC = builder.build();
    }

    private NetherOverhaulConfig() {
    }
}
