package com.sharkclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class SharkClient implements ClientModInitializer {
    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("sharkclient", "main"));
    private static KeyBinding openGui;

    // Kata kunci buat Auto GG, bisa lu ubah sendiri
    private static final List<String> GG_TRIGGERS = List.of("game over", "you won", "victory", "game ended");
    private static long lastGgTime = 0;

    @Override
    public void onInitializeClient() {
        openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sharkclient.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openGui.wasPressed()) mc.setScreen(new ClickGui());
            if (mc.player == null) return;

            // Auto Sprint
            if (Modules.AUTO_SPRINT.enabled && mc.options.forwardKey.isPressed()
                    && !mc.player.isSneaking() && !mc.player.horizontalCollision
                    && mc.player.getHungerManager().getFoodLevel() > 6) {
                mc.player.setSprinting(true);
            }

            // Fullbright (night vision lokal, cuma visual di sisi client)
            if (Modules.FULLBRIGHT.enabled) {
                mc.player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION, 420, 0, false, false, false));
            } else {
                StatusEffectInstance e = mc.player.getStatusEffect(StatusEffects.NIGHT_VISION);
                if (e != null && e.getDuration() <= 420 && !e.shouldShowIcon()) {
                    mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                }
            }
        });

        // Auto GG: cek chat yang masuk
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!Modules.AUTO_GG.enabled || overlay) return;
            String text = message.getString().toLowerCase();
            long now = System.currentTimeMillis();
            for (String trigger : GG_TRIGGERS) {
                if (text.contains(trigger) && now - lastGgTime > 10_000) {
                    lastGgTime = now;
                    MinecraftClient.getInstance().execute(() -> {
                        var handler = MinecraftClient.getInstance().getNetworkHandler();
                        if (handler != null) handler.sendChatMessage("gg");
                    });
                    break;
                }
            }
        });

        // HUD
        HudRenderCallback.EVENT.register((ctx, tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden || mc.getDebugHud().shouldShowDebugHud()) return;

            int y = 4;
            if (Modules.FPS.enabled) {
                ctx.drawTextWithShadow(mc.textRenderer, "FPS: " + mc.getCurrentFps(), 4, y, 0xFFFFFFFF);
                y += 11;
            }
            if (Modules.PING.enabled && mc.getNetworkHandler() != null) {
                var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                String ping = entry != null ? entry.getLatency() + " ms" : "-";
                ctx.drawTextWithShadow(mc.textRenderer, "Ping: " + ping, 4, y, 0xFFFFFFFF);
                y += 11;
            }
            if (Modules.COORDS.enabled) {
                String c = String.format("XYZ: %.0f / %.0f / %.0f",
                        mc.player.getX(), mc.player.getY(), mc.player.getZ());
                ctx.drawTextWithShadow(mc.textRenderer, c, 4, y, 0xFFFFFFFF);
            }

            if (Modules.KEYSTROKES.enabled) {
                drawKeystrokes(ctx, mc);
            }
        });
    }

    private void drawKeystrokes(net.minecraft.client.gui.DrawContext ctx, MinecraftClient mc) {
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int size = 22, gap = 2;
        int baseX = sw - (size * 3 + gap * 2) - 8;
        int baseY = sh - (size * 2 + gap) - 40;

        drawKey(ctx, mc, "W", mc.options.forwardKey.isPressed(), baseX + size + gap, baseY);
        drawKey(ctx, mc, "A", mc.options.leftKey.isPressed(), baseX, baseY + size + gap);
        drawKey(ctx, mc, "S", mc.options.backKey.isPressed(), baseX + size + gap, baseY + size + gap);
        drawKey(ctx, mc, "D", mc.options.rightKey.isPressed(), baseX + (size + gap) * 2, baseY + size + gap);
        drawKey(ctx, mc, "SPACE", mc.options.jumpKey.isPressed(), baseX, baseY + (size + gap) * 2 - 4 - 6, size * 3 + gap * 2);
    }

    private void drawKey(net.minecraft.client.gui.DrawContext ctx, MinecraftClient mc,
                         String label, boolean pressed, int x, int y) {
        drawKey(ctx, mc, label, pressed, x, y, size());
    }

    private void drawKey(net.minecraft.client.gui.DrawContext ctx, MinecraftClient mc,
                         String label, boolean pressed, int x, int y, int w) {
        int h = size();
        ctx.fill(x, y, x + w, y + h, pressed ? 0xDDFFFFFF : 0x80000000);
        int textW = mc.textRenderer.getWidth(label);
        ctx.drawTextWithShadow(mc.textRenderer, label, x + (w - textW) / 2, y + (h - 8) / 2,
                pressed ? 0xFF000000 : 0xFFFFFFFF);
    }

    private static int size() { return 22; }
                                       }
