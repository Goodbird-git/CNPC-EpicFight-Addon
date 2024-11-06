package com.goodbird.cnpcefaddon.common.patch;

import com.goodbird.cnpcefaddon.common.provider.AdvNpcPatchProvider;
import com.goodbird.cnpcefaddon.mixin.impl.IAdvancedCustomHumanoidMobPatch;
import com.nameless.indestructible.server.AdvancedBossInfo;
import com.nameless.indestructible.world.capability.AdvancedCustomHumanoidMobPatch;
import net.minecraft.world.entity.PathfinderMob;
import noppes.npcs.entity.EntityNPCInterface;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.Faction;

public class AdvNpcPatch<T extends PathfinderMob> extends AdvancedCustomHumanoidMobPatch<T> implements INpcPatch {
    AdvNpcPatchProvider provider;

    public AdvNpcPatch(Faction faction, AdvNpcPatchProvider provider) {
        super(faction, provider);
        this.provider = provider;
    }

    public void onConstructed(T entityIn) {
        this.original = entityIn;
        this.armature = provider.armature.deepCopy();
        this.animator = EpicFightMod.getAnimator(this);
        this.animator.init();
        if(!entityIn.getEntityData().hasItem(IAdvancedCustomHumanoidMobPatch.getSTAMINA()))
            entityIn.getEntityData().define(IAdvancedCustomHumanoidMobPatch.getSTAMINA(), 0.0F);
        if(!entityIn.getEntityData().hasItem(IAdvancedCustomHumanoidMobPatch.getATTACK_SPEED()))
            entityIn.getEntityData().define(IAdvancedCustomHumanoidMobPatch.getATTACK_SPEED(), 1.0F);
        if(!entityIn.getEntityData().hasItem(IAdvancedCustomHumanoidMobPatch.getIS_BLOCKING()))
            entityIn.getEntityData().define(IAdvancedCustomHumanoidMobPatch.getIS_BLOCKING(), false);
        if (this.hasBossBar) {
            ((IAdvancedCustomHumanoidMobPatch)this).setBossInfo(new AdvancedBossInfo(this));
        }
    }

    public OpenMatrix4f getModelMatrix(float partialTicks) {
        float scale = ((EntityNPCInterface)original).display.getSize()/5f;
        return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
    }
}
