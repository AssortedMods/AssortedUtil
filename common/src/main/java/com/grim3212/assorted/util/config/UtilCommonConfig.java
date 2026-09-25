package com.grim3212.assorted.util.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.util.Constants;

import java.util.function.Supplier;

/**
 * The parts that act on the world. The grave block registers whether graves are on or not, so a
 * world with graves in it keeps loading when the part is switched off.
 */
public class UtilCommonConfig {

    public final Supplier<Boolean> gravesEnabled;
    public final Supplier<Boolean> doubleDoorsEnabled;

    public final Supplier<Boolean> gravesKeepAllExperience;
    public final Supplier<Boolean> gravesOwnerOnly;
    public final Supplier<Integer> gravesSearchRadius;

    public final Supplier<Boolean> doubleDoorsDoors;
    public final Supplier<Boolean> doubleDoorsTrapdoors;
    public final Supplier<Boolean> doubleDoorsFenceGates;
    public final Supplier<Integer> doubleDoorsMaxConnected;

    public UtilCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        gravesEnabled = builder.defineBoolean("parts.gravesEnabled", true, "Set this to true if a player's items and experience should go into a grave where they die.");
        doubleDoorsEnabled = builder.defineBoolean("parts.doubleDoorsEnabled", true, "Set this to true if opening one of a pair of doors, a hatch of trapdoors or a run of fence gates should open all of them.");

        gravesKeepAllExperience = builder.defineBoolean("graves.keepAllExperience", true, "Set this to true to keep all of a player's experience in their grave. False leaves experience to drop the way it normally does.");
        gravesOwnerOnly = builder.defineBoolean("graves.ownerOnly", false, "Set this to true so only the player who died, or a player in creative mode, can open or break a grave.");
        gravesSearchRadius = builder.defineInteger("graves.searchRadius", 4, 0, 16, "How far from where a player died to look for room for their grave. With no room, the items drop as usual.");

        doubleDoorsDoors = builder.defineBoolean("doubleDoors.doors", true, "Set this to true to open both doors of a double door together.");
        doubleDoorsTrapdoors = builder.defineBoolean("doubleDoors.trapdoors", true, "Set this to true to open every trapdoor of a hatch together.");
        doubleDoorsFenceGates = builder.defineBoolean("doubleDoors.fenceGates", true, "Set this to true to open fence gates stacked on or beside each other together.");
        doubleDoorsMaxConnected = builder.defineInteger("doubleDoors.maxConnected", 16, 1, 64, "The most trapdoors or fence gates one click opens.");

        builder.setup();
    }
}
