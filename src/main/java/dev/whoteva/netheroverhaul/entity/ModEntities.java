package dev.whoteva.netheroverhaul.entity;

import dev.whoteva.netheroverhaul.NetherOverhaul;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = NetherOverhaul.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NetherOverhaul.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<NetherCreeper>> NETHER_CREEPER = ENTITIES.register(
            "nether_creeper",
            () -> EntityType.Builder.of(NetherCreeper::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("nether_creeper"));

    public static final DeferredHolder<EntityType<?>, EntityType<NetherSpider>> NETHER_SPIDER = ENTITIES.register(
            "nether_spider",
            () -> EntityType.Builder.of(NetherSpider::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("nether_spider"));

    public static final DeferredHolder<EntityType<?>, EntityType<NetherZombie>> NETHER_ZOMBIE = ENTITIES.register(
            "nether_zombie",
            () -> EntityType.Builder.of(NetherZombie::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("nether_zombie"));

    private ModEntities() {
    }

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(NETHER_CREEPER.get(), NetherCreeper.createAttributes().build());
        event.put(NETHER_SPIDER.get(), NetherSpider.createAttributes().build());
        event.put(NETHER_ZOMBIE.get(), NetherZombie.createAttributes().build());
    }

    @SubscribeEvent
    public static void spawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(NETHER_CREEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(NETHER_SPIDER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(NETHER_ZOMBIE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
