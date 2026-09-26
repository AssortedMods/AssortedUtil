package com.grim3212.assorted.doubledoors;

import com.grim3212.assorted.doubledoors.common.door.DoubleDoors;
import com.grim3212.assorted.doubledoors.config.DoubleDoorsCommonConfig;

/**
 * Loader-agnostic startup. Both loader entry points call {@link #init()} and nothing else; anything
 * a loader needs beyond it goes through AssortedLib's {@code Services}.
 */
public class DoubleDoorsCommonMod {

    public static final DoubleDoorsCommonConfig COMMON_CONFIG = new DoubleDoorsCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        DoubleDoors.init();
    }
}
