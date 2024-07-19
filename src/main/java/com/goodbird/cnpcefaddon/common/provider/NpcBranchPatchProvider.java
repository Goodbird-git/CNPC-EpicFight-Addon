package com.goodbird.cnpcefaddon.common.provider;

import com.goodbird.cnpcefaddon.common.ResLocPredicate;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;

public class NpcBranchPatchProvider extends MobPatchReloadListener.BranchProvider {
    public NpcBranchPatchProvider(){
        defaultProvider = new MobPatchReloadListener.NullPatchProvider();
    }

    public void addProvider(ResourceLocation resLoc, MobPatchReloadListener.AbstractMobPatchProvider newProv){
        providers.add(new Pair<>(new ResLocPredicate(resLoc), newProv));
    }
}
