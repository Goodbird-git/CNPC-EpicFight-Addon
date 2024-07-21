package com.goodbird.cnpcefaddon.common.patch;

import com.goodbird.cnpcefaddon.common.provider.NpcHumanoidPatchProvider;
import com.goodbird.cnpcefaddon.mixin.impl.ISynchedEntityData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.CustomHumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.Faction;

public class NpcHumanoidPatch<T extends PathfinderMob> extends CustomHumanoidMobPatch<T> implements INpcPatch {
    NpcHumanoidPatchProvider provider;
    public NpcHumanoidPatch(Faction faction, NpcHumanoidPatchProvider provider) {
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
}