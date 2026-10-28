package dev.whoteva.netheroverhaul;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Inventory snapshot taken before a sealed player dies, so respawn can put it back.
 */
public record KeptGear(boolean saved, int selected, List<SlotStack> slots, List<ItemStack> extra,
                       int level, float progress, int total) {

    public static final KeptGear EMPTY = new KeptGear(false, 0, List.of(), List.of(), 0, 0.0F, 0);

    public static final Codec<KeptGear> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("saved").forGetter(KeptGear::saved),
            Codec.INT.fieldOf("selected").forGetter(KeptGear::selected),
            SlotStack.CODEC.listOf().fieldOf("slots").forGetter(KeptGear::slots),
            ItemStack.CODEC.listOf().fieldOf("extra").forGetter(KeptGear::extra),
            Codec.INT.fieldOf("level").forGetter(KeptGear::level),
            Codec.FLOAT.fieldOf("progress").forGetter(KeptGear::progress),
            Codec.INT.fieldOf("total").forGetter(KeptGear::total)
    ).apply(instance, KeptGear::new));

    public record SlotStack(int slot, ItemStack stack) {
        public static final Codec<SlotStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(SlotStack::slot),
                ItemStack.CODEC.fieldOf("stack").forGetter(SlotStack::stack)
        ).apply(instance, SlotStack::new));
    }
}
