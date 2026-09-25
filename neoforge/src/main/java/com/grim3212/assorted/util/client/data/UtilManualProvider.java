package com.grim3212.assorted.util.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.util.Constants;
import com.grim3212.assorted.util.common.UtilParts;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/** This mod's section of the instruction manual. Each chapter hangs off the part it documents. */
public class UtilManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, as in the other Assorted manuals. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public UtilManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        // Util adds no items to stand for it, so a clock does.
        this.section(160, Items.CLOCK);

        ChapterBuilder graves = this.chapter("graves").whenPartEnabled(UtilParts.GRAVES);
        graves.image("info", picture("grave"), PICTURE_WIDTH, PICTURE_HEIGHT).opensBlocks(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "grave"));
        graves.image("restore", picture("grave_restore"), PICTURE_WIDTH, PICTURE_HEIGHT);

        this.chapter("double_doors").whenPartEnabled(UtilParts.DOUBLE_DOORS).image("info", picture("double_doors"), PICTURE_WIDTH, PICTURE_HEIGHT);
        this.chapter("auto_item_replacer").whenPartEnabled(UtilParts.AUTO_ITEM_REPLACER).image("info", picture("auto_item_replacer"), PICTURE_WIDTH, PICTURE_HEIGHT);
        this.chapter("time").whenPartEnabled(UtilParts.TIME).image("info", picture("time"), PICTURE_WIDTH, PICTURE_HEIGHT);
        this.chapter("light_overlay").whenPartEnabled(UtilParts.LIGHT_OVERLAY).image("info", picture("light_overlay"), PICTURE_WIDTH, PICTURE_HEIGHT);
        this.chapter("damage_numbers").whenPartEnabled(UtilParts.DAMAGE_NUMBERS).image("info", picture("damage_numbers"), PICTURE_WIDTH, PICTURE_HEIGHT);
    }

    /** In-game screenshots under {@code textures/gui/manual}, taken with AssortedUtil-photos beside this repository. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
