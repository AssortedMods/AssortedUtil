package com.grim3212.assorted.util.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.util.Constants;
import com.grim3212.assorted.util.client.autoitem.AutoItemReplacer;
import com.grim3212.assorted.util.client.damage.DamageNumbers;
import com.grim3212.assorted.util.client.light.LightOverlay;
import com.grim3212.assorted.util.client.render.GraveRenderer;
import com.grim3212.assorted.util.client.time.TimeHud;
import com.grim3212.assorted.util.common.block.entity.UtilBlockEntityTypes;
import com.grim3212.assorted.util.config.UtilClientConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/** Client-only startup, called by both loaders' client entry points. */
public class UtilClient {

    public static final UtilClientConfig CONFIG = new UtilClientConfig();
    public static final Identifier KEY_CATEGORY = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "general");

    public static KeyMapping TOGGLE_TIME;
    public static KeyMapping TOGGLE_LIGHT_OVERLAY;

    public static void init() {
        // G, the 1.12 time key, is vanilla's quick actions now. F7 is where light overlays have always lived.
        TOGGLE_TIME = ClientServices.KEYBINDS.createNew("key.assortedutil.toggle_time", ClientServices.KEYBINDS.getInGameKeyConflictContext(), InputConstants.Type.KEYSYM, InputConstants.KEY_H, KEY_CATEGORY);
        TOGGLE_LIGHT_OVERLAY = ClientServices.KEYBINDS.createNew("key.assortedutil.toggle_light_overlay", ClientServices.KEYBINDS.getInGameKeyConflictContext(), InputConstants.Type.KEYSYM, InputConstants.KEY_F7, KEY_CATEGORY);
        ClientServices.CLIENT.registerKeyMapping(TOGGLE_TIME);
        ClientServices.CLIENT.registerKeyMapping(TOGGLE_LIGHT_OVERLAY);

        ClientServices.CLIENT.registerClientTickEnd(UtilClient::tick);
        ClientServices.CLIENT.registerHudElement(TimeHud.ID, TimeHud::extract);
        ClientServices.CLIENT.registerLevelSubmit(LightOverlay::submit);
        ClientServices.CLIENT.registerLevelSubmit(DamageNumbers::submit);

        ClientServices.CLIENT.registerBlockEntityRenderer(() -> UtilBlockEntityTypes.GRAVE.get(), GraveRenderer::new);
    }

    private static void tick(Minecraft minecraft) {
        while (TOGGLE_TIME.consumeClick()) {
            TimeHud.cycle();
        }
        while (TOGGLE_LIGHT_OVERLAY.consumeClick()) {
            LightOverlay.toggle(minecraft);
        }

        TimeHud.tick();
        AutoItemReplacer.tick(minecraft);
        LightOverlay.tick(minecraft);
        DamageNumbers.tick(minecraft);
    }
}
