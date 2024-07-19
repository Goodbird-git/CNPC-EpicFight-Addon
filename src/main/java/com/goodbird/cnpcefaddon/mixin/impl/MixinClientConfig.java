package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IClientConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.config.ClientConfig;

@Mixin(ClientConfig.class)
public class MixinClientConfig implements IClientConfig {
    @Unique
    public ForgeConfigSpec.BooleanValue firstPersonRenderEnabled;

    @Inject(method = "<init>", at=@At("TAIL"), remap = false)
    public void ClientConfigInit(ForgeConfigSpec.Builder config, CallbackInfo ci){
        firstPersonRenderEnabled = config.define("ingame.firstPersonRenderEnabled", () -> Boolean.valueOf(true));
    }

    @Unique
    @Override
    public boolean isFPRenderEnabled() {
        return firstPersonRenderEnabled.get();
    }
}
