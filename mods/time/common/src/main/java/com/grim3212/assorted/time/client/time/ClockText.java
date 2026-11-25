package com.grim3212.assorted.time.client.time;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.dimension.DimensionType;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * The words on the time panel, kept apart from the drawing so a dedicated server's tests can read them.
 * Nothing here touches a client class.
 */
public final class ClockText {

    public static final String NO_TIME = "--:--";

    private static final DateTimeFormatter REAL_24 = DateTimeFormatter.ofPattern("MMMM d - HH:mm:ss - z");
    private static final DateTimeFormatter REAL_12 = DateTimeFormatter.ofPattern("MMMM d - h:mm:ss a - z");

    private ClockText() {
    }

    /** Where a clock only spins, as in the Nether and the End, the panel cannot tell the time either. */
    public static boolean tellsTime(DimensionType dimension) {
        return dimension.natural();
    }

    /** The game time line: the day, counted from one, then the hour or {@link #NO_TIME}. */
    public static Component gameLine(DimensionType dimension, long dayTime, boolean twentyFourHour) {
        String time = tellsTime(dimension) ? gameTime(dayTime, twentyFourHour) : NO_TIME;
        return Component.translatable("hud.assortedtime.time.game", day(dayTime), time);
    }

    public static long day(long dayTime) {
        return Math.floorDiv(dayTime, 24000L) + 1;
    }

    /** Tick 0 is 6 in the morning. */
    public static String gameTime(long dayTime, boolean twentyFourHour) {
        long ticks = Math.floorMod(dayTime, 24000L);
        int hours = (int) ((ticks / 1000L + 6L) % 24L);
        int minutes = (int) (ticks % 1000L * 60L / 1000L);
        if (twentyFourHour) {
            return String.format(Locale.ROOT, "%02d:%02d", hours, minutes);
        }
        return String.format(Locale.ROOT, "%d:%02d %s", hours % 12 == 0 ? 12 : hours % 12, minutes, hours < 12 ? "AM" : "PM");
    }

    public static String realTime(ZonedDateTime now, boolean twentyFourHour) {
        return now.format(twentyFourHour ? REAL_24 : REAL_12);
    }
}
