package com.goodbird.cnpcefaddon.common;

import com.goodbird.cnpcefaddon.mixin.IDataDisplay;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import yesman.epicfight.data.conditions.entity.HasCustomTag;

public class ResLocPredicate extends HasCustomTag {
    public ResourceLocation resourceLocation;

    public ResLocPredicate(ResourceLocation resLoc){
        super(new ListTag());
        this.resourceLocation = resLoc;
    }

    @Override
    public boolean predicate(Entity paramT) {
        if(!(paramT instanceof EntityNPCInterface)) return false;
        EntityNPCInterface npc = (EntityNPCInterface) paramT;
        if(npc.display!=null && ((IDataDisplay)npc.display).hasEFModel()){
            return resourceLocation.equals(((IDataDisplay)npc.display).getEFModel());
        }
        return false;
    }
}
