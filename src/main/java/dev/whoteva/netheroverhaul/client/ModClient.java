package dev.whoteva.netheroverhaul.client;

import dev.whoteva.netheroverhaul.NetherOverhaul;
import dev.whoteva.netheroverhaul.entity.ModEntities;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = NetherOverhaul.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClient {

    private ModClient() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.NETHER_CREEPER.get(), NetherCreeperRenderer::new);
        event.registerEntityRenderer(ModEntities.NETHER_SPIDER.get(), NetherSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.NETHER_ZOMBIE.get(), NetherZombieRenderer::new);
    }

    public static final class NetherCreeperRenderer extends CreeperRenderer {
        private static final ResourceLocation TEXTURE = NetherOverhaul.id("textures/entity/nether_creeper.png");

        public NetherCreeperRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(Creeper entity) {
            return TEXTURE;
        }
    }

    public static final class NetherSpiderRenderer extends SpiderRenderer<Spider> {
        private static final ResourceLocation TEXTURE = NetherOverhaul.id("textures/entity/nether_spider.png");

        public NetherSpiderRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(Spider entity) {
            return TEXTURE;
        }
    }

    public static final class NetherZombieRenderer extends ZombieRenderer {
        private static final ResourceLocation TEXTURE = NetherOverhaul.id("textures/entity/nether_zombie.png");

        public NetherZombieRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(Zombie entity) {
            return TEXTURE;
        }
    }
}
