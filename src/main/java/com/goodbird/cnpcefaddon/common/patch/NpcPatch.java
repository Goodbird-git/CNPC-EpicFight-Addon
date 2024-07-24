package com.goodbird.cnpcefaddon.common.patch;

import com.goodbird.cnpcefaddon.common.provider.NpcPatchProvider;
import com.goodbird.cnpcefaddon.mixin.impl.ISynchedEntityData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.CustomMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.Faction;

public class NpcPatch<T extends PathfinderMob> extends CustomMobPatch<T> implements INpcPatch {
    NpcPatchProvider provider;

    public NpcPatch(Faction faction, NpcPatchProvider provider) {
        super(faction, provider);
        this.provider = provider;
    }

    public void onConstructed(T entityIn) {
        this.original = entityIn;
        this.armature = provider.armature.deepCopy();
        this.animator = EpicFightMod.getAnimator(this);
        this.animator.init();
        if(((ISynchedEntityData)this.original.getEntityData()).invokeGetItem(STUN_SHIELD)==null) {
            this.original.getEntityData().define(STUN_SHIELD, Float.valueOf(0.0F));
            this.original.getEntityData().define(MAX_STUN_SHIELD, Float.valueOf(0.0F));
            this.original.getEntityData().define(EXECUTION_RESISTANCE, Integer.valueOf(1));
            this.original.getEntityData().define(AIRBORNE, Boolean.valueOf(false));
        }
    }

    public OpenMatrix4f getModelMatrix(float partialTicks) {
        float scale = ((EntityNPCInterface)original).display.getSize()/5f;
        return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
    }
}