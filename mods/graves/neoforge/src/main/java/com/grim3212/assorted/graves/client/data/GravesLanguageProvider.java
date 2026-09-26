package com.grim3212.assorted.graves.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.graves.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. The grave's name is its id in title case, so it needs no line here 
 * (see {@link LibLanguageProvider}).
 */
public class GravesLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public GravesLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("message.assortedgraves.grave.dug", "Your belongings are in a grave at %s, %s, %s.");
        this.add("message.assortedgraves.grave.no_room", "There was no room for a grave, so your belongings were dropped.");
        this.add("message.assortedgraves.grave.not_yours", "This is %s's grave.");

        this.addManual();
    }

    /** The chapters in {@code assets/assortedutil/manual} name these keys. */
    private void addManual() {
        this.add("manual.assortedutil.title", "Assorted Util");
        this.add("manual.assortedutil.description", "Graves, double doors, an item replacer, a clock, a light level overlay and damage numbers.");

        this.add("manual.assortedutil.chapter.graves", "Graves");
        this.add("manual.assortedutil.chapter.graves.info.title", "Graves");
        this.add("manual.assortedutil.chapter.graves.info",
                "When you die, everything you carried and all of your experience goes into a grave where you died." + BREAK
                        + "Items with Curse of Vanishing still vanish.");
        this.add("manual.assortedutil.chapter.graves.restore.title", "Getting It Back");
        this.add("manual.assortedutil.chapter.graves.restore",
                "Right-click on your grave to get everything back where it was." + BREAK
                        + "Breaking it spills it all on the ground instead. Explosions, pistons and bosses cannot move or break a grave.");
    }
}
