package com.grim3212.assorted.lightoverlay.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lightoverlay.Constants;
import com.grim3212.assorted.lightoverlay.client.light.LightOverlay;
import com.grim3212.assorted.lightoverlay.config.LightOverlayClientConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/** Client-only startup, called by both loaders' client entry points. */
public class LightOverlayClient {

    public static final LightOverlayClientConfig CONFIG = new LightOverlayClientConfig();
    public static final Identifier KEY_CATEGORY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "general");

    public static KeyMapping TOGGLE_LIGHT_OVERLAY;

    public static void init() {
        // F7 is where light overlays have always lived.
        TOGGLE_LIGHT_OVERLAY = ClientServices.KEYBINDS.createNew("key.assortedlightoverlay.toggle_light_overlay", ClientServices.KEYBINDS.getInGameKeyConflictContext(), InputConstants.Type.KEYSYM, InputConstants.KEY_F7, KEY_CATEGORY);
        ClientServices.CLIENT.registerKeyMapping(TOGGLE_LIGHT_OVERLAY);

        ClientServices.CLIENT.registerClientTickEnd(LightOverlayClient::tick);
        ClientServices.CLIENT.registerLevelSubmit(LightOverlay::submit);
    }

    private static void tick(Minecraft minecraft) {
        while (TOGGLE_LIGHT_OVERLAY.consumeClick()) {
            LightOverlay.toggle(minecraft);
        }

        LightOverlay.tick(minecraft);
    }
}
