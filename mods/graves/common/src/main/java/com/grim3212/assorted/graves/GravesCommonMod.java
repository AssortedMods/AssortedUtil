package com.grim3212.assorted.graves;

import com.grim3212.assorted.graves.common.block.GravesBlocks;
import com.grim3212.assorted.graves.common.block.entity.GravesBlockEntityTypes;
import com.grim3212.assorted.graves.common.grave.Graves;
import com.grim3212.assorted.graves.config.GravesCommonConfig;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.Identifier;

/**
 * Loader-agnostic startup. Both loader entry points call {@link #init()} and nothing else; anything
 * a loader needs beyond it goes through AssortedLib's {@code Services}.
 */
public class GravesCommonMod {

    public static final GravesCommonConfig COMMON_CONFIG = new GravesCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.withDefaultNamespace("clock"), 10)
                .manualOrder(160);

        GravesBlocks.init();
        GravesBlockEntityTypes.init();
        Graves.init();
    }
}
