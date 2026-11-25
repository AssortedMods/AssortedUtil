package com.grim3212.assorted.time.client.time;

import com.grim3212.assorted.lib.conditions.PartToggles;
import com.grim3212.assorted.time.Constants;
import com.grim3212.assorted.time.client.TimeClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Grim Util's time panel, sliding down from the top of the screen in its 1.12 frame. Top left rather
 * than 1.12's top right, which status effect icons have since taken.
 */
public final class TimeHud {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "time");
    private static final ResourceLocation PANEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "time_panel");

    // The sprite's frame is 8 pixels; the text sits 3 inside it.
    private static final int INSET = 11;
    private static final int MARGIN = 2;
    private static final int LINE_HEIGHT = 10;
    private static final int TEXT_COLOR = 0xFFC6C6C6;
    private static final float SLIDE_PER_TICK = 0.2F;

    private static TimeDisplay display = TimeDisplay.HIDDEN;
    // What is drawn while the panel slides away, after display has gone back to hidden.
    private static TimeDisplay drawn = TimeDisplay.GAME;
    private static float slide;
    private static float lastSlide;

    private TimeHud() {
    }

    /** Steps to the next of hidden, game time, real time and both. */
    public static void cycle() {
        display = display.next();
        if (display != TimeDisplay.HIDDEN) {
            drawn = display;
        }
    }

    public static TimeDisplay display() {
        return display;
    }

    public static void tick() {
        lastSlide = slide;
        slide = Mth.clamp(slide + (display != TimeDisplay.HIDDEN ? SLIDE_PER_TICK : -SLIDE_PER_TICK), 0.0F, 1.0F);
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!PartToggles.isEnabled(Constants.MOD_ID)) {
            return;
        }

        float progress = Mth.lerp(deltaTracker.getGameTimeDeltaPartialTick(true), lastSlide, slide);
        Minecraft minecraft = Minecraft.getInstance();
        if (progress <= 0.0F || minecraft.level == null) {
            return;
        }

        List<Component> lines = lines(minecraft.level, drawn);
        Font font = minecraft.font;
        int textWidth = 0;
        for (Component line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }
        int width = textWidth + INSET * 2;
        int height = lines.size() * LINE_HEIGHT - 2 + INSET * 2;

        // Eased, so it lands rather than stops.
        float eased = 1.0F - (1.0F - progress) * (1.0F - progress);
        int y = Math.round(Mth.lerp(eased, -height, MARGIN));

        graphics.blitSprite(PANEL, MARGIN, y, width, height);
        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(font, lines.get(i), MARGIN + INSET, y + INSET + i * LINE_HEIGHT, TEXT_COLOR);
        }
    }

    private static List<Component> lines(ClientLevel level, TimeDisplay display) {
        boolean twentyFourHour = TimeClient.CONFIG.timeTwentyFourHour.get();
        List<Component> lines = new ArrayList<>();
        if (display.showsGame()) {
            lines.add(ClockText.gameLine(level.dimensionType(), level.getDayTime(), twentyFourHour));
        }
        if (display.showsReal()) {
            lines.add(Component.literal(ClockText.realTime(ZonedDateTime.now(), twentyFourHour)));
        }
        return lines;
    }
}
