package com.sharkclient.mixin;

import com.sharkclient.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getLeftText", at = @At("RETURN"))
    private void sharkclient$addName(CallbackInfoReturnable<List<String>> cir) {
        if (Modules.F3_NAME.enabled) {
            cir.getReturnValue().add("Player: " + MinecraftClient.getInstance().getSession().getUsername());
        }
    }
}
