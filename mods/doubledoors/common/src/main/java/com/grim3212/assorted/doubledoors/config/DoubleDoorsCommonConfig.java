package com.grim3212.assorted.doubledoors.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.doubledoors.Constants;

import java.util.function.Supplier;

/** Server rules: which blocks open together, and how many at once. */
public class DoubleDoorsCommonConfig {

    public final Supplier<Boolean> doubleDoorsDoors;
    public final Supplier<Boolean> doubleDoorsTrapdoors;
    public final Supplier<Boolean> doubleDoorsFenceGates;
    public final Supplier<Integer> doubleDoorsMaxConnected;

    public DoubleDoorsCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        doubleDoorsDoors = builder.defineBoolean("doubleDoors.doors", true, "Set this to true to open both doors of a double door together.");
        doubleDoorsTrapdoors = builder.defineBoolean("doubleDoors.trapdoors", true, "Set this to true to open every trapdoor of a hatch together.");
        doubleDoorsFenceGates = builder.defineBoolean("doubleDoors.fenceGates", true, "Set this to true to open fence gates stacked on or beside each other together.");
        doubleDoorsMaxConnected = builder.defineInteger("doubleDoors.maxConnected", 16, 1, 64, "The most trapdoors or fence gates one click opens.");

        builder.setup();
    }
}
