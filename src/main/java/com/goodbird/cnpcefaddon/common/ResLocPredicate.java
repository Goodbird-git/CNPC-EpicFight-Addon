package com.goodbird.cnpcefaddon.common;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import yesman.epicfight.api.data.reloader.EpicFightPredicates;

public class ResLocPredicate extends EpicFightPredicates<Entity> {
    public ResourceLocation resourceLocation;

    public ResLocPredicate(ResourceLocation resLoc){
        this.resourceLocation = resLoc;
    }

    @Override
    public boolean test(Entity paramT) {
        if(!(paramT instanceof EntityNPCInterface)) return false;
        EntityNPCInterface npc = (EntityNPCInterface) paramT;
        if(npc.display!=null && ((IDataDisplay)npc.display).hasEFModel()){
            return resourceLocation.equals(((IDataDisplay)npc.display).getEFModel());
        }
        return false;
    }
}
