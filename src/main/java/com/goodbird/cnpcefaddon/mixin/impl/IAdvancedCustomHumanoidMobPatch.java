package com.goodbird.cnpcefaddon.mixin.impl;

import com.nameless.indestructible.server.AdvancedBossInfo;
import com.nameless.indestructible.world.capability.AdvancedCustomHumanoidMobPatch;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancedCustomHumanoidMobPatch.class)
public interface IAdvancedCustomHumanoidMobPatch {

    @Accessor
    void setBossInfo(AdvancedBossInfo info);

    @Accessor
    static EntityDataAccessor<Float> getSTAMINA() {
        return null;
    }

    @Accessor
    static EntityDataAccessor<Float> getATTACK_SPEED() {
        return null;
    }

    @Accessor
    static EntityDataAccessor<Boolean> getIS_BLOCKING() {
        return null;
    }
}
