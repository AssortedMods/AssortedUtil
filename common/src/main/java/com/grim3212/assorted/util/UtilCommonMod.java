package com.grim3212.assorted.util;

import com.grim3212.assorted.util.common.UtilParts;
import com.grim3212.assorted.util.common.block.UtilBlocks;
import com.grim3212.assorted.util.common.block.entity.UtilBlockEntityTypes;
import com.grim3212.assorted.util.common.door.DoubleDoors;
import com.grim3212.assorted.util.common.grave.Graves;
import com.grim3212.assorted.util.config.UtilCommonConfig;

/**
 * Loader-agnostic startup. Both loader entry points call {@link #init()} and nothing else; anything
 * a loader needs beyond it goes through AssortedLib's {@code Services}.
 */
public class UtilCommonMod {

    public static final UtilCommonConfig COMMON_CONFIG = new UtilCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        UtilBlocks.init();
        UtilBlockEntityTypes.init();
        UtilParts.init();

        Graves.init();
        DoubleDoors.init();
    }
}
