package com.sharkclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class ClickGui extends Screen {
    private int selected = 0;
    private static final int PANEL_W = 340, PANEL_H = 200;
    private static final int SIDEBAR_W = 90, PAD = 8, ROW_H = 22;

    public ClickGui() {
        super(Text.literal("Shark Client"));
    }

    private int left() { return width / 2 - PANEL_W / 2; }
    private int top()  { return height / 2 - PANEL_H / 2; }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        int x = left(), y = top();
        ctx.fill(x, y, x + PANEL_W, y + PANEL_H, 0xE0101018);
        ctx.fill(x, y, x + SIDEBAR_W, y + PANEL_H, 0xF0181822);
        ctx.drawTextWithShadow(textRenderer, "Shark Client", x + PAD, y + PAD, 0xFF55FFFF);

        for (int i = 0; i < Modules.CATEGORIES.length; i++) {
            int cy = y + 30 + i * ROW_H;
            if (i == selected) ctx.fill(x, cy, x + SIDEBAR_W, cy + ROW_H - 2, 0xFF2A2A44);
            ctx.drawTextWithShadow(textRenderer, Modules.CATEGORIES[i], x + PAD, cy + 7, 0xFFFFFFFF);
        }

        List<Module> mods = currentModules();
        int mw = PANEL_W - SIDEBAR_W - PAD * 2;
        for (int i = 0; i < mods.size(); i++) {
            Module m = mods.get(i);
            int rx = x + SIDEBAR_W + PAD, ry = y + PAD + i * ROW_H;
            boolean hover = mx >= rx && mx <= rx + mw && my >= ry && my <= ry + ROW_H - 2;
            ctx.fill(rx, ry, rx + mw, ry + ROW_H - 2, hover ? 0xFF2C2C3C : 0xFF202030);
            ctx.drawTextWithShadow(textRenderer, m.name, rx + 6, ry + 7, 0xFFFFFFFF);
            ctx.drawTextWithShadow(textRenderer, m.enabled ? "ON" : "OFF",
                    rx + mw - 24, ry + 7, m.enabled ? 0xFF55FF55 : 0xFFFF5555);
        }
    }

    private List<Module> currentModules() {
        String cat = Modules.CATEGORIES[selected];
        return Modules.ALL.stream().filter(m -> m.category.equals(cat)).toList();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int x = left(), y = top();
        for (int i = 0; i < Modules.CATEGORIES.length; i++) {
            int cy = y + 30 + i * ROW_H;
            if (mx >= x && mx <= x + SIDEBAR_W && my >= cy && my <= cy + ROW_H - 2) {
                selected = i;
                return true;
            }
        }
        List<Module> mods = currentModules();
        int mw = PANEL_W - SIDEBAR_W - PAD * 2;
        for (int i = 0; i < mods.size(); i++) {
            int rx = x + SIDEBAR_W + PAD, ry = y + PAD + i * ROW_H;
            if (mx >= rx && mx <= rx + mw && my >= ry && my <= ry + ROW_H - 2) {
                mods.get(i).toggle();
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
