package com.goodbird.cnpcefaddon;

import com.goodbird.cnpcefaddon.common.AdvNpcPatchReloader;
import com.goodbird.cnpcefaddon.common.NpcPatchReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;

@Mod(CNPCEpicFightAddon.MODID)
public class CNPCEpicFightAddon {
    public static final String MODID = "cnpcefaddon";

    public CNPCEpicFightAddon(){
        MinecraftForge.EVENT_BUS.addListener(this::reloadListenerEvent);
    }

    private void reloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new NpcPatchReloadListener());
        if(ModList.get().isLoaded("indestructible")){
            event.addListener(new AdvNpcPatchReloader());
        }
    }
}
