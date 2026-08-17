package dev.whoteva.netheroverhaul.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class NetherZombie extends Zombie {

    public NetherZombie(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes();
    }
}
