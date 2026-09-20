package dev.whoteva.netheroverhaul;

import dev.whoteva.netheroverhaul.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NetherOverhaul.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.netheroverhaul"))
                    .icon(() -> ModItems.RIFT_KEY.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.RIFT_KEY.get());
                        output.accept(ModItems.RIFT_SEEKER.get());
                        output.accept(ModItems.RECALL_CHARM.get());
                        output.accept(ModItems.SEALED_FLASK.get());
                        output.accept(ModItems.GLOWSTONE_SEED.get());
                        output.accept(ModItems.GLOWSTONE_BERRY.get());
                        output.accept(ModItems.QUICK_SOUL_SAND.get());
                        output.accept(ModItems.NETHER_IRON_ORE.get());
                        output.accept(ModItems.NETHER_COPPER_ORE.get());
                        output.accept(ModItems.NETHER_COAL_ORE.get());
                        output.accept(ModItems.NETHER_REDSTONE_ORE.get());
                        output.accept(ModItems.NETHER_CREEPER_EGG.get());
                        output.accept(ModItems.NETHER_SPIDER_EGG.get());
                        output.accept(ModItems.NETHER_ZOMBIE_EGG.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }

    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.RIFT_KEY.get());
            event.accept(ModItems.RIFT_SEEKER.get());
            event.accept(ModItems.RECALL_CHARM.get());
            event.accept(ModItems.SEALED_FLASK.get());
            event.accept(ModItems.GLOWSTONE_SEED.get());
            event.accept(ModItems.GLOWSTONE_BERRY.get());
        }
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModItems.QUICK_SOUL_SAND.get());
            event.accept(ModItems.NETHER_IRON_ORE.get());
            event.accept(ModItems.NETHER_COPPER_ORE.get());
            event.accept(ModItems.NETHER_COAL_ORE.get());
            event.accept(ModItems.NETHER_REDSTONE_ORE.get());
        }
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.NETHER_CREEPER_EGG.get());
            event.accept(ModItems.NETHER_SPIDER_EGG.get());
            event.accept(ModItems.NETHER_ZOMBIE_EGG.get());
        }
    }
}
