package com.grim3212.assorted.time.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.time.Constants;
import com.grim3212.assorted.time.client.time.TimeHud;
import com.grim3212.assorted.time.config.TimeClientConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/** Client-only startup, called by both loaders' client entry points. */
public class TimeClient {

    public static final TimeClientConfig CONFIG = new TimeClientConfig();
    public static final Identifier KEY_CATEGORY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "general");

    public static KeyMapping TOGGLE_TIME;

    public static void init() {
        // G, the 1.12 time key, is vanilla's quick actions now.
        TOGGLE_TIME = ClientServices.KEYBINDS.createNew("key.assortedtime.toggle_time", ClientServices.KEYBINDS.getInGameKeyConflictContext(), InputConstants.Type.KEYSYM, InputConstants.KEY_H, KEY_CATEGORY);
        ClientServices.CLIENT.registerKeyMapping(TOGGLE_TIME);

        ClientServices.CLIENT.registerClientTickEnd(TimeClient::tick);
        ClientServices.CLIENT.registerHudElement(TimeHud.ID, TimeHud::extract);
    }

    private static void tick(Minecraft minecraft) {
        while (TOGGLE_TIME.consumeClick()) {
            TimeHud.cycle();
        }

        TimeHud.tick();
    }
}
