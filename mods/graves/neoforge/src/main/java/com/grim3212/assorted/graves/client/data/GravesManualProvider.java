package com.grim3212.assorted.graves.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.graves.Constants;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/** This mod's chapter of the Assorted Util section, which every Util mod shares. */
public class GravesManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, as in the other Assorted manuals. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public GravesManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        ChapterBuilder graves = this.chapter("graves", 0);
        graves.image("info", picture("grave"), PICTURE_WIDTH, PICTURE_HEIGHT).opensBlocks(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "grave"));
        graves.image("restore", picture("grave_restore"), PICTURE_WIDTH, PICTURE_HEIGHT);
    }

    /** In-game screenshots under {@code textures/gui/manual}, taken with AssortedUtil-photos beside this repository. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
