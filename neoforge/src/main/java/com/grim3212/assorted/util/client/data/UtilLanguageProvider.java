package com.grim3212.assorted.util.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.util.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. The grave's name is its id in title case, so it needs no line
 * here (see {@link LibLanguageProvider}).
 */
public class UtilLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public UtilLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("key.category.assortedutil.general", "Assorted Util");
        this.add("key.assortedutil.toggle_time", "Cycle the Time Panel");
        this.add("key.assortedutil.toggle_light_overlay", "Light Level Overlay");

        this.add("tag.block.assortedutil.ignored_by_double_doors", "Ignored by Double Doors");

        this.add("message.assortedutil.grave.dug", "Your belongings are in a grave at %s, %s, %s.");
        this.add("message.assortedutil.grave.no_room", "There was no room for a grave, so your belongings were dropped.");
        this.add("message.assortedutil.grave.not_yours", "This is %s's grave.");

        this.add("hud.assortedutil.time.game", "Day %s - %s");
        this.add("hud.assortedutil.light_overlay.on", "Light level overlay on");
        this.add("hud.assortedutil.light_overlay.off", "Light level overlay off");

        this.add("hud.assortedutil.damage.melee", "Melee");
        this.add("hud.assortedutil.damage.projectile", "Projectile");
        this.add("hud.assortedutil.damage.explosion", "Explosion");
        this.add("hud.assortedutil.damage.fire", "Fire");
        this.add("hud.assortedutil.damage.magic", "Magic");
        this.add("hud.assortedutil.damage.freezing", "Freezing");
        this.add("hud.assortedutil.damage.lightning", "Lightning");
        this.add("hud.assortedutil.damage.drowning", "Drowning");
        this.add("hud.assortedutil.damage.fall", "Fall");
        this.add("hud.assortedutil.damage.starvation", "Starvation");
        this.add("hud.assortedutil.damage.cactus", "Cactus");
        this.add("hud.assortedutil.damage.generic", "Generic");
        this.add("hud.assortedutil.damage.healing", "Heal");

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

        this.add("manual.assortedutil.chapter.double_doors", "Double Doors");
        this.add("manual.assortedutil.chapter.double_doors.info.title", "Double Doors");
        this.add("manual.assortedutil.chapter.double_doors.info",
                "You can have two doors side by side and opening one door will open the other. The same goes for a large group of trapdoors, and for fence gates stacked on or beside each other." + BREAK
                        + "Sneak to open just one. Doors, trapdoors and gates from other mods should work too.");

        this.add("manual.assortedutil.chapter.auto_item_replacer", "Item Replacer");
        this.add("manual.assortedutil.chapter.auto_item_replacer.info.title", "Item Replacer");
        this.add("manual.assortedutil.chapter.auto_item_replacer.info",
                "When the stack in your hand runs out or your tool breaks, another from your inventory takes its place.");

        this.add("manual.assortedutil.chapter.time", "Time");
        this.add("manual.assortedutil.chapter.time.info.title", "Time");
        this.add("manual.assortedutil.chapter.time.info",
                "Press the key default \"H\" to slide down a clock with the day and time in the world. Press it again for the date and time where you are, press it again for both, and once more to hide it." + BREAK
                        + "In the Nether the world's time cannot be told.");

        this.add("manual.assortedutil.chapter.light_overlay", "Light Overlay");
        this.add("manual.assortedutil.chapter.light_overlay.info.title", "Light Overlay");
        this.add("manual.assortedutil.chapter.light_overlay.info",
                "Press the key default \"F7\" to show the block light on the ground around you." + BREAK
                        + "Red is where a monster can spawn at any time, yellow where one can only at night or a storm, and green where one never can.");

        this.add("manual.assortedutil.chapter.damage_numbers", "Damage Numbers");
        this.add("manual.assortedutil.chapter.damage_numbers.info.title", "Damage Numbers");
        this.add("manual.assortedutil.chapter.damage_numbers.info",
                "Damage and healing pop off creatures as numbers, with what caused it beside them." + BREAK
                        + "A hit by a player will say the weapon, and a hit by a creature names the creature.");
    }
}
