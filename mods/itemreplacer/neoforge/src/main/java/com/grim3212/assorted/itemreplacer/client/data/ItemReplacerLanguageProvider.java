package com.grim3212.assorted.itemreplacer.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.itemreplacer.Constants;
import net.minecraft.data.PackOutput;

/** Generates the en_us.json of this mod. */
public class ItemReplacerLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public ItemReplacerLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.addManual();
    }

    /** The chapters in {@code assets/assortedutil/manual} name these keys. */
    private void addManual() {
        this.add("manual.assortedutil.title", "Assorted Util");
        this.add("manual.assortedutil.description", "Graves, double doors, an item replacer, a clock, a light level overlay and damage numbers.");

        this.add("manual.assortedutil.chapter.auto_item_replacer", "Item Replacer");
        this.add("manual.assortedutil.chapter.auto_item_replacer.info.title", "Item Replacer");
        this.add("manual.assortedutil.chapter.auto_item_replacer.info",
                "When the stack in your hand runs out or your tool breaks, the same item from your inventory takes its place. "
                        + "It takes from your inventory before your hotbar, so your hotbar stays the way you set it up." + BREAK
                        + "A broken tool is swapped for one with the same enchantments and name if you have one, and the most worn one "
                        + "goes first so your tools get used up in order. Blocks, food and potions only swap for an exact match, so a "
                        + "Potion of Healing is never refilled with Poison." + BREAK
                        + "Finish a potion or a bowl of stew and a full one takes the place of the empty bottle or bowl.");
    }
}
