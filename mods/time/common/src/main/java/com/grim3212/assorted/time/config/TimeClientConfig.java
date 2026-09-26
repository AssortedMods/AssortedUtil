package com.grim3212.assorted.time.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.time.Constants;

import java.util.function.Supplier;

/** Only changes what one player sees, so each player decides for themselves. */
public class TimeClientConfig {

    public final Supplier<Boolean> timeTwentyFourHour;

    public TimeClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, Constants.MOD_ID + "-client");

        timeTwentyFourHour = builder.defineBoolean("time.twentyFourHour", true, "Use a 24 hour clock. False uses AM and PM.");

        builder.setup();
    }
}
