package com.grim3212.assorted.util.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.util.Constants;

import java.util.function.Supplier;

/** The parts that only change what one player sees or does, so each player decides for themselves. */
public class UtilClientConfig {

    public final Supplier<Boolean> autoItemReplacerEnabled;
    public final Supplier<Boolean> timeEnabled;
    public final Supplier<Boolean> lightOverlayEnabled;
    public final Supplier<Boolean> damageNumbersEnabled;

    public final Supplier<Boolean> timeTwentyFourHour;

    public final Supplier<Integer> lightOverlayRadius;
    public final Supplier<Boolean> lightOverlayShowSafe;

    public final Supplier<Boolean> damageNumbersShowSource;
    public final Supplier<Boolean> damageNumbersShowHealing;
    public final Supplier<Integer> damageNumbersRange;
    public final Supplier<Integer> damageNumbersLifetime;

    public UtilClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, Constants.MOD_ID + "-client");

        autoItemReplacerEnabled = builder.defineBoolean("parts.autoItemReplacerEnabled", true, "Set this to true to refill the selected hotbar slot from your inventory when what was in it runs out or breaks.");
        timeEnabled = builder.defineBoolean("parts.timeEnabled", true, "Set this to true to have a key that shows the time: in the world, on your computer, or both, pressing it again to step through them.");
        lightOverlayEnabled = builder.defineBoolean("parts.lightOverlayEnabled", true, "Set this to true to have a key that shows the light level where monsters could spawn.");
        damageNumbersEnabled = builder.defineBoolean("parts.damageNumbersEnabled", true, "Set this to true to show damage and healing as numbers popping off creatures.");

        timeTwentyFourHour = builder.defineBoolean("time.twentyFourHour", true, "Use a 24 hour clock. False uses AM and PM.");

        lightOverlayRadius = builder.defineInteger("lightOverlay.radius", 16, 4, 32, "How many blocks around you the light overlay reaches.");
        lightOverlayShowSafe = builder.defineBoolean("lightOverlay.showSafe", true, "Also show the light level where monsters can never spawn. False only marks where they can, at any time or in the dark.");

        damageNumbersShowSource = builder.defineBoolean("damageNumbers.showSource", true, "Show what did the damage beside the number, as in -3 (Fire).");
        damageNumbersShowHealing = builder.defineBoolean("damageNumbers.showHealing", true, "Show healing as well as damage.");
        damageNumbersRange = builder.defineInteger("damageNumbers.range", 24, 4, 64, "How far away, in blocks, damage numbers are shown.");
        damageNumbersLifetime = builder.defineInteger("damageNumbers.lifetime", 40, 10, 200, "How long a damage number stays, in ticks. There are 20 ticks a second.");

        builder.setup();
    }
}
