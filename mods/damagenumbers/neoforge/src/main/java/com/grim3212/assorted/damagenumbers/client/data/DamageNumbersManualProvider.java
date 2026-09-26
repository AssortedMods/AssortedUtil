package com.grim3212.assorted.damagenumbers.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.damagenumbers.Constants;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/** This mod's section of the instruction manual. */
public class DamageNumbersManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, as in the other Assorted manuals. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public DamageNumbersManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        // Damage numbers add no items, so a sword stands for them.
        this.section(165, Items.IRON_SWORD);

        this.chapter("damage_numbers").image("info", picture("damage_numbers"), PICTURE_WIDTH, PICTURE_HEIGHT);
    }

    /** In-game screenshots under {@code textures/gui/manual}, taken with AssortedUtil-photos beside this repository. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
