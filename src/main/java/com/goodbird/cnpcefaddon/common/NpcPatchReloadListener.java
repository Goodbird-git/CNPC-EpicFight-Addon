package com.goodbird.cnpcefaddon.common;

import com.goodbird.cnpcefaddon.client.render.RenderStorage;
import com.goodbird.cnpcefaddon.common.network.SPDatapackSync;
import com.goodbird.cnpcefaddon.common.patch.INpcPatch;
import com.goodbird.cnpcefaddon.common.provider.INpcPatchProvider;
import com.goodbird.cnpcefaddon.common.provider.NpcBranchPatchProvider;
import com.goodbird.cnpcefaddon.common.provider.NpcHumanoidPatchProvider;
import com.goodbird.cnpcefaddon.common.provider.NpcPatchProvider;
import com.goodbird.cnpcefaddon.mixin.impl.ICustomHumanoidMobPatchProvider;
import com.goodbird.cnpcefaddon.mixin.impl.ICustomMobPatchProvider;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomEntities;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.provider.EntityPatchProvider;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class NpcPatchReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).create();

    public static final NpcBranchPatchProvider branchPatchProvider = new NpcBranchPatchProvider();
    public static final Set<ResourceLocation> AVAILABLE_MODELS = new HashSet<>();
    public static final Map<ResourceLocation, CompoundTag> TAGMAP = Maps.newHashMap();

    public NpcPatchReloadListener() {
        super(GSON, "npc_epicfight_mobpatch");
    }

    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            CompoundTag tag = null;
            try {
                tag = TagParser.parseTag((entry.getValue()).toString());
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            branchPatchProvider.addProvider(entry.getKey(), deserializeMobPatchProvider(tag, false, resourceManagerIn));
            AVAILABLE_MODELS.add(entry.getKey());
            CompoundTag filteredTag = MobPatchReloadListener.filterClientData(tag);
            filteredTag.putString("patchType", "NORMAL");
            TAGMAP.put(entry.getKey(), MobPatchReloadListener.filterClientData(tag));
            EntityPatchProvider.putCustomEntityPatch(CustomEntities.entityCustomNpc, entity -> ()->branchPatchProvider.get(entity));
            if (EpicFightMod.isPhysicalClient())
                RenderStorage.registerRenderer(entry.getKey(), tag.contains("preset") ? tag.getString("preset") : tag.getString("renderer"), tag);
        }
    }

    public static MobPatchReloadListener.AbstractMobPatchProvider deserializeMobPatchProvider(CompoundTag tag, boolean clientSide, ResourceManager resourceManager) {
        boolean disabled = (tag.contains("disabled") && tag.getBoolean("disabled"));
        if (disabled)
            return new MobPatchReloadListener.NullPatchProvider();
        if (tag.contains("preset")) {
            String presetName = tag.getString("preset");
            Function<Entity, Supplier<EntityPatch<?>>> preset = EntityPatchProvider.get(presetName);
            Armatures.registerEntityTypeArmature(CustomEntities.entityCustomNpc, patch -> {
                if(patch instanceof INpcPatch) {
                    return ((INpcPatch) patch).getArmature().deepCopy();
                }
                return Armatures.getRegistry(patch.getOriginal().getType()).apply(patch).deepCopy();
            });
            MobPatchReloadListener.MobPatchPresetProvider mobPatchPresetProvider = new MobPatchReloadListener.MobPatchPresetProvider(preset);
            return mobPatchPresetProvider;
        }
        boolean humanoid = tag.getBoolean("isHumanoid");

        MobPatchReloadListener.AbstractMobPatchProvider provider = humanoid ? new NpcHumanoidPatchProvider() : new NpcPatchProvider();
        final ICustomMobPatchProvider npcPatchProvider = (ICustomMobPatchProvider) provider;
        npcPatchProvider.setAttributeValues(MobPatchReloadListener.deserializeAttributes(tag.getCompound("attributes")));
        ResourceLocation modelLocation = new ResourceLocation(tag.getString("model"));
        ResourceLocation armatureLocation = new ResourceLocation(tag.getString("armature"));
        modelLocation = new ResourceLocation(modelLocation.getNamespace(), "animmodels/" + modelLocation.getPath() + ".json");
        armatureLocation = new ResourceLocation(armatureLocation.getNamespace(), "animmodels/" + armatureLocation.getPath() + ".json");
        if (EpicFightMod.isPhysicalClient()) {
            Meshes.getOrCreateAnimatedMesh(Minecraft.getInstance().getResourceManager(), modelLocation, !humanoid ? yesman.epicfight.api.client.model.AnimatedMesh::new : yesman.epicfight.client.mesh.HumanoidMesh::new);
        }
        Armature armature = Armatures.getOrCreateArmature(resourceManager, armatureLocation, !humanoid ? Armature::new : yesman.epicfight.model.armature.HumanoidArmature::new);
        ((INpcPatchProvider)provider).setArmature(armature);
        Armatures.registerEntityTypeArmature(CustomEntities.entityCustomNpc, patch -> {
            if(patch instanceof INpcPatch) {
                return ((INpcPatch) patch).getArmature().deepCopy();
            }
            return Armatures.getRegistry(patch.getOriginal().getType()).apply(patch).deepCopy();
        });
        npcPatchProvider.setDefaultAnimations(MobPatchReloadListener.deserializeDefaultAnimations(tag.getCompound("default_livingmotions")));
        npcPatchProvider.setFaction(Faction.valueOf(tag.getString("faction").toUpperCase(Locale.ROOT)));
        npcPatchProvider.setScale(tag.getCompound("attributes").contains("scale") ? (float)tag.getCompound("attributes").getDouble("scale") : 1.0F);
        if (tag.contains("swing_sound"))
            npcPatchProvider.setSwingSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(tag.getString("swing_sound"))));
        if (tag.contains("hit_sound"))
            npcPatchProvider.setHitSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(tag.getString("hit_sound"))));
        if (tag.contains("hit_particle"))
            npcPatchProvider.setHitParticle((HitParticleType)ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(tag.getString("hit_particle"))));
        if (!clientSide) {
            npcPatchProvider.setStunAnimations(MobPatchReloadListener.deserializeStunAnimations(tag.getCompound("stun_animations")));
            if (tag.getCompound("attributes").contains("chasing_speed")) {
                npcPatchProvider.setChasingSpeed(tag.getCompound("attributes").getDouble("chasing_speed"));
            }
            if (humanoid) {
                MobPatchReloadListener.CustomHumanoidMobPatchProvider humanoidProvider = (MobPatchReloadListener.CustomHumanoidMobPatchProvider) npcPatchProvider;
                ((ICustomHumanoidMobPatchProvider) humanoidProvider).setHumanoidCombatBehaviors(MobPatchReloadListener.deserializeHumanoidCombatBehaviors(tag.getList("combat_behavior", 10)));
                ((ICustomHumanoidMobPatchProvider) humanoidProvider).setHumanoidWeaponMotions(MobPatchReloadListener.deserializeHumanoidWeaponMotions(tag.getList("humanoid_weapon_motions", 10)));
            } else {
                npcPatchProvider.setCombatBehaviorsBuilder(MobPatchReloadListener.deserializeCombatBehaviorsBuilder(tag.getList("combat_behavior", 10)));
            }
        }
        return provider;
    }

    public static Stream<CompoundTag> getDataStream() {
        Stream<CompoundTag> tagStream = TAGMAP.entrySet().stream().map((entry) -> {
            entry.getValue().putString("id", entry.getKey().toString());
            return entry.getValue();
        });
        return tagStream;
    }

    @OnlyIn(Dist.CLIENT)
    public static void processServerPacket(SPDatapackSync packet) {
        for (CompoundTag tag : packet.getTags()) {
            boolean disabled = false;
            if (tag.contains("disabled"))
                disabled = tag.getBoolean("disabled");
            ResourceLocation key = new ResourceLocation(tag.getString("id"));
            MobPatchReloadListener.AbstractMobPatchProvider provider = null;
            if(ModList.get().isLoaded("indestructible") && tag.getString("patchType").equals("ADVANCED")){
                provider = AdvNpcPatchReloader.deserializeMobPatchProvider(tag, false);
            }else{
                provider = deserializeMobPatchProvider(tag, false, Minecraft.getInstance().getResourceManager());
            }
            branchPatchProvider.addProvider(key, provider);
            AVAILABLE_MODELS.add(key);
            EntityPatchProvider.putCustomEntityPatch(CustomEntities.entityCustomNpc, entity -> ()->branchPatchProvider.get(entity));
            if (!disabled) {
                if (tag.contains("preset")) {
                    //Armatures.registerEntityTypeArmature(entityType, tag.getString("preset"));

                } else {
                    Minecraft mc = Minecraft.getInstance();
                    ResourceLocation armatureLocation = new ResourceLocation(tag.getString("armature"));
                    armatureLocation = new ResourceLocation(armatureLocation.getNamespace(), "animmodels/" + armatureLocation.getPath() + ".json");
                    boolean humanoid = tag.getBoolean("isHumanoid");
                    Armature armature = Armatures.getOrCreateArmature(mc.getResourceManager(), armatureLocation, humanoid ? Armature::new : yesman.epicfight.model.armature.HumanoidArmature::new);
                    ((INpcPatchProvider)provider).setArmature(armature);
                }
                Armatures.registerEntityTypeArmature(CustomEntities.entityCustomNpc, patch -> {
                    if(patch instanceof INpcPatch) {
                        return ((INpcPatch) patch).getArmature().deepCopy();
                    }
                    return Armatures.getRegistry(patch.getOriginal().getType()).apply(patch).deepCopy();
                });
                RenderStorage.registerRenderer(key, tag.contains("preset") ? tag.getString("preset") : tag.getString("renderer"), tag);
            }
        }
    }
}

/*
function interact(e){
    var npc = e.npc.getMCEntity()
    var RL = Java.type("net.minecraft.resources.ResourceLocation")
    npc.display.setEFModel(new RL("customnpcs:skeleton"))
}
 */