package com.grim3212.assorted.graves.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.graves.Constants;

import java.util.function.Supplier;

/** Server rules: a grave changes the world, so the server decides how it works. */
public class GravesCommonConfig {

    public final Supplier<Boolean> gravesKeepAllExperience;
    public final Supplier<Boolean> gravesOwnerOnly;
    public final Supplier<Integer> gravesSearchRadius;

    public GravesCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        gravesKeepAllExperience = builder.defineBoolean("graves.keepAllExperience", true, "Set this to true to keep all of a player's experience in their grave. False leaves experience to drop the way it normally does.");
        gravesOwnerOnly = builder.defineBoolean("graves.ownerOnly", false, "Set this to true so only the player who died, or a player in creative mode, can open or break a grave.");
        gravesSearchRadius = builder.defineInteger("graves.searchRadius", 4, 0, 16, "How far from where a player died to look for room for their grave. With no room, the items drop as usual.");

        builder.setup();
    }
}
