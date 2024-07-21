package com.goodbird.cnpcefaddon;

import com.goodbird.cnpcefaddon.common.NpcPatchReloadListener;
import com.goodbird.cnpcefaddon.common.network.SPDatapackSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import yesman.epicfight.network.EpicFightNetworkManager;

import java.util.Objects;
import java.util.stream.Stream;

@Mod(CNPCEpicFightAddon.MODID)
public class CNPCEpicFightAddon {
    public static final String MODID = "cnpcefaddon";

    public CNPCEpicFightAddon(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::doCommonStuff);
        MinecraftForge.EVENT_BUS.addListener(this::reloadListenerEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onDatapackSync);
    }

    private void doCommonStuff(FMLCommonSetupEvent event) {
        EpicFightNetworkManager.INSTANCE.registerMessage(99, SPDatapackSync.class, SPDatapackSync::toBytes, SPDatapackSync::fromBytes, SPDatapackSync::handle);
    }

    private void reloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new NpcPatchReloadListener());
    }

    private void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        SPDatapackSync mobPatchPacket = new SPDatapackSync(NpcPatchReloadListener.TAGMAP.size());
        for(CompoundTag tag : NpcPatchReloadListener.getDataStream().toList()){
            mobPatchPacket.write(tag);
        }
        if (player != null) {
            if (!player.getServer().isSingleplayerOwner(player.getGameProfile())) {
                EpicFightNetworkManager.sendToPlayer(mobPatchPacket, player);
            }
        } else {
            event.getPlayerList().getPlayers().forEach((serverPlayer) -> {
                EpicFightNetworkManager.sendToPlayer(mobPatchPacket, serverPlayer);
            });
        }

    }

}
