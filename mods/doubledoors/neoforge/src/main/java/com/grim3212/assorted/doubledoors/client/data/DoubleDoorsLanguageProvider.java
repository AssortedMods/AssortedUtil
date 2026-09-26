package com.grim3212.assorted.doubledoors.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.doubledoors.Constants;
import net.minecraft.data.PackOutput;

/** Generates the en_us.json of this mod. */
public class DoubleDoorsLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public DoubleDoorsLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("tag.block.assorteddoubledoors.ignored_by_double_doors", "Ignored by Double Doors");

        this.addManual();
    }

    /** The chapters in {@code assets/assorteddoubledoors/manual} name these keys. */
    private void addManual() {
        this.add("manual.assorteddoubledoors.title", "Assorted Double Doors");
        this.add("manual.assorteddoubledoors.description", "One click opens both doors of a pair, a whole trapdoor hatch, or a run of fence gates.");

        this.add("manual.assorteddoubledoors.chapter.double_doors", "Double Doors");
        this.add("manual.assorteddoubledoors.chapter.double_doors.info.title", "Double Doors");
        this.add("manual.assorteddoubledoors.chapter.double_doors.info",
                "You can have two doors side by side and opening one door will open the other. The same goes for a large group of trapdoors, and for fence gates stacked on or beside each other." + BREAK
                        + "Sneak to open just one. Doors, trapdoors and gates from other mods should work too.");
    }
}
