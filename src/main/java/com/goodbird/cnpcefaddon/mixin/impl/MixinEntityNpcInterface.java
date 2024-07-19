package com.goodbird.cnpcefaddon.mixin.impl;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(EntityNPCInterface.class)
public class MixinEntityNpcInterface extends PathfinderMob {

    protected MixinEntityNpcInterface(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Inject(method = "addRegularEntries", at=@At("TAIL"), remap = false)
    public void addRegularEntries(CallbackInfo ci) {
        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
        if(patch instanceof HumanoidMobPatch){
            ((HumanoidMobPatch<?>) patch).setAIAsInfantry(this.getMainHandItem().getItem() instanceof net.minecraft.world.item.ProjectileWeaponItem);
        }
    }
}
