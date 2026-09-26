package com.grim3212.assorted.lightoverlay.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.lightoverlay.Constants;
import net.minecraft.data.PackOutput;

/** Generates the en_us.json of this mod. */
public class LightOverlayLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public LightOverlayLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("key.category.assortedlightoverlay.general", "Assorted Light Overlay");
        this.add("key.assortedlightoverlay.toggle_light_overlay", "Light Level Overlay");

        this.add("hud.assortedlightoverlay.light_overlay.on", "Light level overlay on");
        this.add("hud.assortedlightoverlay.light_overlay.off", "Light level overlay off");

        this.addManual();
    }

    /** The chapters in {@code assets/assortedlightoverlay/manual} name these keys. */
    private void addManual() {
        this.add("manual.assortedlightoverlay.title", "Assorted Light Overlay");
        this.add("manual.assortedlightoverlay.description", "A key that shows the light level wherever a monster could spawn.");

        this.add("manual.assortedlightoverlay.chapter.light_overlay", "Light Overlay");
        this.add("manual.assortedlightoverlay.chapter.light_overlay.info.title", "Light Overlay");
        this.add("manual.assortedlightoverlay.chapter.light_overlay.info",
                "Press the key default \"F7\" to show the block light on the ground around you." + BREAK
                        + "Red is where a monster can spawn at any time, yellow where one can only at night or a storm, and green where one never can.");
    }
}
