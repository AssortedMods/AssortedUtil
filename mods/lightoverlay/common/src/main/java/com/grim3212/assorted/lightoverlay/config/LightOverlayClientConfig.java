package com.grim3212.assorted.lightoverlay.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lightoverlay.Constants;

import java.util.function.Supplier;

/** Only changes what one player sees, so each player decides for themselves. */
public class LightOverlayClientConfig {

    public final Supplier<Integer> lightOverlayRadius;
    public final Supplier<Boolean> lightOverlayShowSafe;

    public LightOverlayClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, Constants.MOD_ID + "-client");

        lightOverlayRadius = builder.defineInteger("lightOverlay.radius", 16, 4, 32, "How many blocks around you the light overlay reaches.");
        lightOverlayShowSafe = builder.defineBoolean("lightOverlay.showSafe", true, "Also show the light level where monsters can never spawn. False only marks where they can, at any time or in the dark.");

        builder.setup();
    }
}
