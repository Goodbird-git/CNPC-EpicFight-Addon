package com.goodbird.cnpcefaddon.mixin.impl;

import com.goodbird.cnpcefaddon.mixin.IClientConfig;
import net.minecraftforge.client.event.RenderHandEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.config.ConfigManager;

@Mixin(RenderEngine.Events.class)
public class MixinRenderEngineEvents {

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true, remap = false)
    private static void renderHand(RenderHandEvent event, CallbackInfo ci) {
        if(!((IClientConfig) ConfigManager.INGAME_CONFIG).isFPRenderEnabled()){
            ci.cancel();
        }
    }
}
