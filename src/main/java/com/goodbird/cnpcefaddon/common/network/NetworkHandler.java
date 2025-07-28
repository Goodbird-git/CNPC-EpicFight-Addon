package com.goodbird.cnpcefaddon.common.network;

import com.goodbird.cnpcefaddon.CNPCEpicFightAddon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL = "CNPCEFADDON";

    public static SimpleChannel CHANNEL;

    public static int index = 0;

    public static void register() {
        CHANNEL = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(CNPCEpicFightAddon.MODID, "packets"))
                .clientAcceptedVersions(PROTOCOL::equals)
                .serverAcceptedVersions(PROTOCOL::equals)
                .networkProtocolVersion(() -> PROTOCOL)
                .simpleChannel();

        index = 0;
        CHANNEL.registerMessage(index++, SPDatapackSync.class, SPDatapackSync::toBytes, SPDatapackSync::fromBytes, SPDatapackSync::handle);
    }

    public static <MSG> void send(ServerPlayer player, MSG msg) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
