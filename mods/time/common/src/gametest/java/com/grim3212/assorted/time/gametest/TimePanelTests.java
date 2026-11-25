package com.grim3212.assorted.time.gametest;

import com.grim3212.assorted.time.client.time.ClockText;
import com.grim3212.assorted.time.client.time.TimeDisplay;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * What the time panel says and in which order the time key steps through it, which a dedicated server can
 * still check. Drawing the panel needs a client.
 */
final class TimePanelTests {

    private TimePanelTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("time_key_steps_through_the_panels", TimePanelTests::timeKeyStepsThroughThePanels);
        out.accept("time_panel_reads_the_clock", TimePanelTests::timePanelReadsTheClock);
    }

    /** Hidden, the world's time, the real time, both, and hidden again. */
    private static void timeKeyStepsThroughThePanels(GameTestHelper helper) {
        TimeDisplay display = TimeDisplay.HIDDEN;
        display = display.next();
        helper.assertValueEqual(display, TimeDisplay.GAME, "one press");
        helper.assertTrue(display.showsGame() && !display.showsReal(), "one press shows only the world's time");
        display = display.next();
        helper.assertValueEqual(display, TimeDisplay.REAL, "two presses");
        helper.assertTrue(!display.showsGame() && display.showsReal(), "two presses show only the real time");
        display = display.next();
        helper.assertValueEqual(display, TimeDisplay.BOTH, "three presses");
        helper.assertTrue(display.showsGame() && display.showsReal(), "three presses show both times");
        display = display.next();
        helper.assertValueEqual(display, TimeDisplay.HIDDEN, "four presses");
        helper.assertTrue(!display.showsGame() && !display.showsReal(), "four presses hide the panel");
        helper.succeed();
    }

    private static void timePanelReadsTheClock(GameTestHelper helper) {
        helper.assertValueEqual(ClockText.gameTime(0L, true), "06:00", "tick 0, 24 hour");
        helper.assertValueEqual(ClockText.gameTime(6000L, true), "12:00", "noon, 24 hour");
        helper.assertValueEqual(ClockText.gameTime(6000L, false), "12:00 PM", "noon, 12 hour");
        helper.assertValueEqual(ClockText.gameTime(18000L, true), "00:00", "midnight, 24 hour");
        helper.assertValueEqual(ClockText.gameTime(18000L, false), "12:00 AM", "midnight, 12 hour");
        helper.assertValueEqual(ClockText.gameTime(13500L, false), "7:30 PM", "half past seven in the evening");
        helper.assertValueEqual(ClockText.gameTime(24000L * 5 + 1000L, true), "07:00", "a later day wraps round");

        helper.assertValueEqual(ClockText.day(0L), 1L, "the first day");
        helper.assertValueEqual(ClockText.day(23999L), 1L, "the end of the first day");
        helper.assertValueEqual(ClockText.day(24000L), 2L, "the second day");

        DimensionType overworld = dimension(helper, BuiltinDimensionTypes.OVERWORLD);
        DimensionType nether = dimension(helper, BuiltinDimensionTypes.NETHER);
        DimensionType end = dimension(helper, BuiltinDimensionTypes.END);
        helper.assertTrue(ClockText.tellsTime(overworld), "the overworld's time cannot be told");
        helper.assertFalse(ClockText.tellsTime(nether), "the Nether's time can be told, where a clock spins");
        helper.assertFalse(ClockText.tellsTime(end), "the End's time can be told, where a clock spins");

        // The words themselves come from the client's language file; the day and the hour are what is checked.
        expectLine(helper, ClockText.gameLine(overworld, 24000L * 2 + 6000L, true), 3L, "12:00", "noon on the third day");
        expectLine(helper, ClockText.gameLine(nether, 6000L, true), 1L, ClockText.NO_TIME, "the Nether");

        ZonedDateTime evening = ZonedDateTime.of(2024, 3, 5, 19, 4, 9, 0, ZoneId.of("UTC"));
        helper.assertTrue(ClockText.realTime(evening, true).contains("19:04:09"), "the real time, 24 hour, reads " + ClockText.realTime(evening, true));
        helper.assertTrue(ClockText.realTime(evening, false).contains("7:04:09"), "the real time, 12 hour, reads " + ClockText.realTime(evening, false));
        helper.succeed();
    }

    private static void expectLine(GameTestHelper helper, Component line, long day, String time, String when) {
        if (!(line.getContents() instanceof TranslatableContents contents)) {
            throw new GameTestAssertException("the game line for " + when + " is not translatable");
        }
        helper.assertValueEqual(contents.getKey(), "hud.assortedtime.time.game", "the game line's key for " + when);
        helper.assertValueEqual(List.of(contents.getArgs()), List.<Object>of(day, time), "the game line's day and hour for " + when);
    }

    private static DimensionType dimension(GameTestHelper helper, ResourceKey<DimensionType> key) {
        return helper.getLevel().registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).getOrThrow(key).value();
    }
}
