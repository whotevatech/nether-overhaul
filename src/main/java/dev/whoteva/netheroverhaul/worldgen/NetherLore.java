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
        return book("Sealed Below", "A Lost Miner", List.of(
                page("You woke in the Nether.\n\nThe Overworld is sealed. Portals refuse you until a Rift Key is used."),
                page("This camp has a lodestone in the floor, a compass, water cauldrons, a charged respawn anchor and a Sealed Flask. The flask fills cauldrons, and can place water here.\n\nThe chest holds stone tools, a golden pick for netherrack, wool, flint and gravel. Smelt gravel into sand. Four soul soil makes clay."),
                page("Sneak-use a Rift Seeker to choose what it hunts, then use it again. Soul flames point the way. A Recall Charm brings you back to this lodestone."),
                page("Rift Shards gather in wither pits, shrines, nests and ash pyres. Four shards around crying obsidian assemble a key.\n\nA rift altar's obsidian spire holds a finished key. A Rift Warden guards it. Ash pyres may burn under a Cinder Warden."),
                page("Glowstone berries grow on soul sand. Eat one to glow and see in the dark. Mature roots also shed glowstone dust.\n\nQuick soul sand is darker, cracked with soul light, and swallows you like cobweb."),
                page("Iron, copper, coal and redstone bake into netherrack. Nether creepers burst into flame. Nether spiders and zombies set you alight, and they do not fear fire.\n\nThe rift is waiting.")
        ));
    }

    public static ItemStack shrineNote() {
        return book("Quartz Remembers", "A Pilgrim", List.of(
                page("Quartz remembers a sky this place no longer has.\n\nShards of the seal gather where wither pits go down. Four of them, set around crying obsidian, assemble a Rift Key."),
                page("If you see an obsidian spire burning with soul light, that is a rift altar. The chest there already holds a finished key. The figure in gold is not decoration.")
        ));
    }

    public static ItemStack altarNote() {
        return book("The Spire", "A Warden", List.of(
                page("This spire is a finished rift. The chest holds a key.\n\nThe Rift Warden drops shards if the lock is not enough. Do not open a portal until you mean to leave."),
                page("Ash pyres mark where a Cinder Warden still tends the old fire. They are not the way home. They are how the seal was broken into pieces.")
        ));
    }

    public static void showTitle(ServerPlayer player, String titleKey, String subtitleKey) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
        player.connection.send(new ClientboundSetTitleTextPacket(Component.translatable(titleKey)));
        player.connection.send(new ClientboundSetSubtitleTextPacket(Component.translatable(subtitleKey)));
    }

    private static ItemStack book(String title, String author, List<Filterable<Component>> pages) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
                Filterable.passThrough(title),
                author,
                0,
                pages,
                true
        ));
        return book;
    }

    private static Filterable<Component> page(String text) {
        return Filterable.passThrough(Component.literal(text));
    }
}
