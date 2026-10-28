# Changelog

## 1.4.1

- Structure signs no longer crash chunk generation. Camp and hamlet signs write their text through the sign's NBT load path while the block entity is still unattached, instead of `setText`, which calls `markUpdated` and NPEs when `level` is null.

## 1.4.0

- Spawn camp is a finished shelter: brick walls, crimson posts, gilded trim, stair eaves, lectern with the lore book, smithing table, smoker, anvil, charged respawn anchor, fenced mature wart and a glowstone berry, yard banner and lantern posts. The starter chest always has food, a stone shovel, a golden pick, shears, bone meal and a mushroom stew kit.
- Keep-inventory until the rift opens now actually keeps the inventory. Deaths used to empty it before the respawn copy.
- Hamlet huts have doors and windows. The chest sits under a market stall you can walk up to. Ghast nests no longer replace their chain with a chest, and the lantern hangs from a real roof. Obsidian wells have a floor, a ladder and a water source. Wither pits have a ladder, cobwebs to break the fall, and a floor.
- Fences, bars and stairs in these builds connect, so they read as builds instead of loose posts.
- Rift Seeker: sneak-use cycles the structure it hunts, soul flames point the way, range and cooldown are config. Recall Charm refuses a point-blank teleport, picks a safe landing, and grants fire resistance plus slow falling. Sealed Flask water placement can be turned off.
- Rift Shards are the mid-game key. Wither pits always drop some. Four shards around crying obsidian craft a Rift Key. Rift altars, new and rare, hold a finished key and a Rift Warden. Ash pyres can be guarded by a Cinder Warden.
- Spacing for wells, dungeons, pyres, blaze towers, fire towers and altars is configurable. New worlds only.
- Glowstone berries are a light snack (glow plus a short night vision) and mature roots can drop glowstone dust. Quick soul sand has its own cracked texture and ash motes.
- Nether creepers detonate smaller and set fire. Nether spiders and zombies are tougher and set you alight. Zombies ignore sunlight.
- Advancements for shards, berries, quick soul sand, the nether mobs and both wardens. Bastion hoglin stables get the same chest inject as the other bastion rooms. Piglins can barter glowstone berries.
- Item, block and mob textures redrawn in an ash, gold and obsidian palette.

## 1.3.4

- Spawn camp is a real hut: yard, doors, windows, glowstone roof, lookout hatch, fenced wart farm, stonecutter, grindstone, barrel, wool bedroll.
- Comforts sleeping bag is placed at camp when that mod is loaded.

## 1.3.3

- Restore nether iron, copper, coal and redstone baked into netherrack (`packVanillaOres`, on by default).
- Smelt or blast gravel into sand. Four soul soil crafts a clay block. Piglins can barter gravel, sand, glass and clay.
- Spawn camp chest always includes wool, flint, gravel and string. Crimson sign marks the lodestone under the floor.
- First-join teleport runs as soon as the player loads, so the Overworld flash is shorter.
- Quick soul sand sheds soul wisps so it is no longer identical to real soul sand.
- Extra-ore attempts default to 24. Drop duplicate Mekanism lead/uranium and deepslate extra ores from the tag.

## 1.3.1

- Port original Nether Overload fire towers (nether-brick trunk, fire-tipped branches).
- Port original blaze towers (fence cage, spiral slabs, blaze spawner, original loot table).
- Watchtowers no longer share a mid-tower blaze spawner with blaze towers.
- `/netheroverhaul locate blaze` and `locate firetower`.

## 1.3.0

- Glowstone berries, seeds and roots on soul sand, using original textures.
- Quick soul sand (looks like soul sand, cobweb physics).
- Fire-immune nether creeper, spider and zombie.
- Obsidian wells, nether dungeons, fire rings, wither trees and glowstone trees.

## 1.2.0

- Camp QOL, Rift Seeker, Recall Charm, Sealed Flask.
- Extra structures: ghast nest, basalt forge, quartz shrine.
- Ember Fields and Weeping Hollow biomes.
- Optional extra nether ores and vanilla loot injects.

## 1.1.0

- Floor-only structures, spawn camp, lore book, Rift Key UX.

## 1.0.0

- Nether spawn, Overworld lock until Rift Key, custom 256-high nether.
