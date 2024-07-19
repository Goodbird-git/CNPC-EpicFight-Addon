package com.goodbird.cnpcefaddon.mixin.impl;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;
import java.util.Map;

@Mixin(MobPatchReloadListener.CustomMobPatchProvider.class)
public interface ICustomMobPatchProvider {

    @Accessor(remap = false)
    void setCombatBehaviorsBuilder(CombatBehaviors.Builder<?> builder);
    @Accessor(remap = false)
    void setDefaultAnimations(List<Pair<LivingMotion, StaticAnimation>> list);
    @Accessor(remap = false)
    void setStunAnimations(Map<StunType, StaticAnimation> map);
    @Accessor(remap = false)
    void setAttributeValues(Map<Attribute, Double> map);
    @Accessor(remap = false)
    void setFaction(Faction faction);
    @Accessor(remap = false)
    void setChasingSpeed(double speed);
    @Accessor(remap = false)
    void setScale(float scale);
}
