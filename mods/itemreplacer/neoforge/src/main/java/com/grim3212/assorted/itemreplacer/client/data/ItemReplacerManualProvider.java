package com.grim3212.assorted.itemreplacer.client.data;

import com.grim3212.assorted.itemreplacer.Family;
import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.itemreplacer.Constants;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/** This mod's chapter of the Assorted Util section, which every Util mod shares. */
public class ItemReplacerManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, as in the other Assorted manuals. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public ItemReplacerManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        this.chapter("auto_item_replacer", 20).image("info", picture("auto_item_replacer"), PICTURE_WIDTH, PICTURE_HEIGHT);
    }

    /** In-game screenshots under {@code textures/gui/manual}, taken with AssortedUtil-photos beside this repository. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
