package com.grim3212.assorted.graves.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.graves.Constants;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/** This mod's section of the instruction manual. */
public class GravesManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, as in the other Assorted manuals. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public GravesManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        // A grave has no item to stand for it, so a skull does.
        this.section(160, Items.SKELETON_SKULL);

        ChapterBuilder graves = this.chapter("graves");
        graves.image("info", picture("grave"), PICTURE_WIDTH, PICTURE_HEIGHT).opensBlocks(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "grave"));
        graves.image("restore", picture("grave_restore"), PICTURE_WIDTH, PICTURE_HEIGHT);
    }

    /** In-game screenshots under {@code textures/gui/manual}, taken with AssortedUtil-photos beside this repository. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
