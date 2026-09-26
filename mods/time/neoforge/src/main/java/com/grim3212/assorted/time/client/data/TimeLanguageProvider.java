package com.grim3212.assorted.time.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.time.Constants;
import net.minecraft.data.PackOutput;

/** Generates the en_us.json of this mod. */
public class TimeLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public TimeLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("key.category.assortedtime.general", "Assorted Time");
        this.add("key.assortedtime.toggle_time", "Cycle the Time Panel");

        this.add("hud.assortedtime.time.game", "Day %s - %s");

        this.addManual();
    }

    /** The chapters in {@code assets/assortedtime/manual} name these keys. */
    private void addManual() {
        this.add("manual.assortedtime.title", "Assorted Time");
        this.add("manual.assortedtime.description", "A key that slides down a clock with the time in the world, the time where you are, or both.");

        this.add("manual.assortedtime.chapter.time", "Time");
        this.add("manual.assortedtime.chapter.time.info.title", "Time");
        this.add("manual.assortedtime.chapter.time.info",
                "Press the key default \"H\" to slide down a clock with the day and time in the world. Press it again for the date and time where you are, press it again for both, and once more to hide it." + BREAK
                        + "In the Nether the world's time cannot be told.");
    }
}
