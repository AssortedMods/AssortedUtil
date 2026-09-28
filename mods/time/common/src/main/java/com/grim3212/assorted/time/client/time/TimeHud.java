package com.grim3212.assorted.time.client.time;

import com.grim3212.assorted.lib.conditions.PartToggles;
import com.grim3212.assorted.time.Constants;
import com.grim3212.assorted.time.client.TimeClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Grim Util's time panel, sliding down from the top of the screen in its 1.12 frame. Top left rather
 * than 1.12's top right, which status effect icons have since taken.
 */
public final class TimeHud {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "time");
    private static final Identifier PANEL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "time_panel");

    // The sprite's frame is 8 pixels; the text sits 3 inside it.
    private static final int INSET = 11;
    private static final int MARGIN = 2;
    private static final int LINE_HEIGHT = 10;
    private static final int TEXT_COLOR = 0xFFC6C6C6;
    private static final float SLIDE_PER_TICK = 0.2F;
    private static final DateTimeFormatter REAL_24 = DateTimeFormatter.ofPattern("MMMM d - HH:mm:ss - z");
    private static final DateTimeFormatter REAL_12 = DateTimeFormatter.ofPattern("MMMM d - h:mm:ss a - z");

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

    public static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
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

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL, MARGIN, y, width, height);
        for (int i = 0; i < lines.size(); i++) {
            graphics.text(font, lines.get(i), MARGIN + INSET, y + INSET + i * LINE_HEIGHT, TEXT_COLOR);
        }
    }

    private static List<Component> lines(ClientLevel level, TimeDisplay display) {
        boolean twentyFourHour = TimeClient.CONFIG.timeTwentyFourHour.get();
        List<Component> lines = new ArrayList<>();
        if (display.showsGame()) {
            // Where a clock only spins, as in the Nether, so does this.
            String time = level.dimensionType().defaultClock().isPresent() ? gameTime(level.getDefaultClockTime(), twentyFourHour) : "--:--";
            lines.add(Component.translatable("hud.assortedtime.time.game", level.getOverworldClockTime() / 24000L + 1, time));
        }
        if (display.showsReal()) {
            lines.add(Component.literal(ZonedDateTime.now().format(twentyFourHour ? REAL_24 : REAL_12)));
        }
        return lines;
    }

    /** Tick 0 is 6 in the morning. */
    private static String gameTime(long clockTime, boolean twentyFourHour) {
        long ticks = Math.floorMod(clockTime, 24000L);
        int hours = (int) ((ticks / 1000L + 6L) % 24L);
        int minutes = (int) (ticks % 1000L * 60L / 1000L);
        if (twentyFourHour) {
            return String.format("%02d:%02d", hours, minutes);
        }
        return String.format("%d:%02d %s", hours % 12 == 0 ? 12 : hours % 12, minutes, hours < 12 ? "AM" : "PM");
    }
}
