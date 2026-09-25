package com.grim3212.assorted.util.common;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.util.UtilCommonMod;
import com.grim3212.assorted.util.client.UtilClient;

/**
 * Graves and double doors are server rules in the common config. The other four only change one
 * player's screen, so they are read from that player's client config and are only ever asked on a client.
 */
public class UtilParts {

    public static final String GRAVES = "graves";
    public static final String DOUBLE_DOORS = "double_doors";
    public static final String AUTO_ITEM_REPLACER = "auto_item_replacer";
    public static final String TIME = "time";
    public static final String LIGHT_OVERLAY = "light_overlay";
    public static final String DAMAGE_NUMBERS = "damage_numbers";

    public static void init() {
        Services.CONDITIONS.registerPartCondition(GRAVES, () -> UtilCommonMod.COMMON_CONFIG.gravesEnabled.get());
        Services.CONDITIONS.registerPartCondition(DOUBLE_DOORS, () -> UtilCommonMod.COMMON_CONFIG.doubleDoorsEnabled.get());
        Services.CONDITIONS.registerPartCondition(AUTO_ITEM_REPLACER, () -> UtilClient.CONFIG.autoItemReplacerEnabled.get());
        Services.CONDITIONS.registerPartCondition(TIME, () -> UtilClient.CONFIG.timeEnabled.get());
        Services.CONDITIONS.registerPartCondition(LIGHT_OVERLAY, () -> UtilClient.CONFIG.lightOverlayEnabled.get());
        Services.CONDITIONS.registerPartCondition(DAMAGE_NUMBERS, () -> UtilClient.CONFIG.damageNumbersEnabled.get());
    }
}
