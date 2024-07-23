package com.goodbird.cnpcefaddon.client.render;

import com.goodbird.cnpcefaddon.mixin.impl.IMixinRenderEngine;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomEntities;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PCustomEntityRenderer;
import yesman.epicfight.client.renderer.patched.entity.PCustomHumanoidEntityRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;

import java.util.HashMap;
import java.util.Map;

public class RenderStorage {
    public static Map<ResourceLocation, PatchedEntityRenderer> renderersMap = new HashMap<>();

    public static void registerRenderer(ResourceLocation resourceLocation, String renderer, CompoundTag compound) {
        RenderEngine engine = ClientEngine.getInstance().renderEngine;
        IMixinRenderEngine renderEngine = (IMixinRenderEngine) ClientEngine.getInstance().renderEngine;
        if ("".equals(renderer))
            return;
        if ("player".equals(renderer)) {
            renderersMap.put(resourceLocation, renderEngine.getBasicHumanoidRenderer());
        } else if ("epicfight:custom".equals(renderer)) {
            EntityRenderDispatcher erd = engine.minecraft.getEntityRenderDispatcher();
            EntityRendererProvider.Context context = new EntityRendererProvider.Context(erd, engine.minecraft.getItemRenderer(), engine.minecraft.getBlockRenderer(), erd.getItemInHandRenderer(), engine.minecraft.getResourceManager(), engine.minecraft.getEntityModels(), engine.minecraft.font);
            if (compound.getBoolean("humanoid")) {
                HumanoidMesh mesh = Meshes.getOrCreateAnimatedMesh(engine.minecraft.getResourceManager(), new ResourceLocation(compound.getString("model")), HumanoidMesh::new);
                renderersMap.put(resourceLocation, new PCustomHumanoidEntityRenderer(mesh, context, CustomEntities.entityCustomNpc));
            } else {
                AnimatedMesh mesh = Meshes.getOrCreateAnimatedMesh(engine.minecraft.getResourceManager(), new ResourceLocation(compound.getString("model")), AnimatedMesh::new);
                renderersMap.put(resourceLocation, new PCustomEntityRenderer(mesh, context, CustomEntities.entityCustomNpc));
            }
        }  else {
            EntityType<?> presetEntityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(renderer));
            if (renderEngine.getEntityRendererProvider().containsKey(presetEntityType)) {
                renderersMap.put(resourceLocation, renderEngine.getEntityRendererProvider().get(presetEntityType).apply(presetEntityType));
            } else {
                throw new IllegalArgumentException("Datapack Mob Patch Crash: Invalid Renderer type " + renderer);
            }
        }
    }


}
