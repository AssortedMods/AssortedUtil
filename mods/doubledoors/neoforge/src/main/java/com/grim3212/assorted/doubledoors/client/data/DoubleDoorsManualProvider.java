package com.grim3212.assorted.doubledoors.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.doubledoors.Constants;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/** This mod's chapter of the Assorted Util section, which every Util mod shares. */
public class DoubleDoorsManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, as in the other Assorted manuals. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public DoubleDoorsManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        this.chapter("double_doors", 10).image("info", picture("double_doors"), PICTURE_WIDTH, PICTURE_HEIGHT);
    }

    /** In-game screenshots under {@code textures/gui/manual}. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
