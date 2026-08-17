package dev.whoteva.netheroverhaul.worldgen;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.List;

public final class NetherLore {

    private NetherLore() {
    }

    public static ItemStack sealedBelowBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        WrittenBookContent content = new WrittenBookContent(
                Filterable.passThrough("Sealed Below"),
                "A Lost Miner",
                0,
                List.of(
                        page("You woke in the Nether.\n\nThe Overworld is sealed. Portals will not take you home until you use a Rift Key."),
                        page("Your camp has a lodestone compass, water cauldrons, a respawn anchor and a Sealed Flask. Right-click the flask on a cauldron to refill it."),
                        page("Search watchtowers, hamlets, wither pits, ghast nests, basalt forges, quartz shrines, obsidian wells, nether dungeons, blaze towers and fire towers. A Rift Seeker points to the nearest one."),
                        page("Glowstone berries grow on soul sand and keep you fed. Watch for quick soul sand — it looks like the real thing and will swallow you. Fire-immune creepers, spiders and zombies hunt the wastes.\n\nThe rift is waiting.")
                ),
                true
        );
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        return book;
    }

    public static void showTitle(ServerPlayer player, String titleKey, String subtitleKey) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 50, 20));
        player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable(titleKey)));
        player.connection.send(new ClientboundSetSubtitleTextPacket(Component.translatable(subtitleKey)));
    }

    private static Filterable<Component> page(String text) {
        return Filterable.passThrough(Component.literal(text));
    }
}
