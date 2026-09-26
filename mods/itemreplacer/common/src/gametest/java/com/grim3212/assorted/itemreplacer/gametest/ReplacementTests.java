package com.grim3212.assorted.itemreplacer.gametest;

import com.grim3212.assorted.itemreplacer.common.autoitem.Replacements;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.itemreplacer.gametest.ItemReplacerTestSupport.*;

/** What the item replacer picks to refill a slot. The swap itself is a client click; this is the choice. */
final class ReplacementTests {

    private ReplacementTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("replacer_takes_from_the_pack_first", ReplacementTests::replacerTakesFromThePackFirst);
        out.accept("replacer_never_swaps_one_potion_for_another", ReplacementTests::replacerNeverSwapsOnePotionForAnother);
        out.accept("replacer_prefers_the_same_enchantments", ReplacementTests::replacerPrefersTheSameEnchantments);
        out.accept("replacer_knows_what_using_up_leaves", ReplacementTests::replacerKnowsWhatUsingUpLeaves);
    }

    /** The biggest stack in the main inventory, before anything laid out on the hotbar. 1.12 took the first it found. */
    private static void replacerTakesFromThePackFirst(GameTestHelper helper) {
        Inventory inventory = survivalPlayer(helper).getInventory();
        inventory.setItem(2, new ItemStack(Items.COBBLESTONE, 64));
        inventory.setItem(12, new ItemStack(Items.COBBLESTONE, 5));
        inventory.setItem(30, new ItemStack(Items.COBBLESTONE, 40));

        helper.assertValueEqual(Replacements.find(inventory, new ItemStack(Items.COBBLESTONE), 0), 30, "slot picked");
        helper.assertValueEqual(Replacements.find(inventory, new ItemStack(Items.DIRT), 0), -1, "slot picked for something not carried");
        helper.succeed();
    }

    private static void replacerNeverSwapsOnePotionForAnother(GameTestHelper helper) {
        Inventory inventory = survivalPlayer(helper).getInventory();
        inventory.setItem(10, PotionContents.createItemStack(Items.POTION, Potions.POISON));
        ItemStack healing = PotionContents.createItemStack(Items.POTION, Potions.HEALING);

        helper.assertValueEqual(Replacements.find(inventory, healing, 0), -1, "slot picked to replace a Potion of Healing");
        inventory.setItem(20, PotionContents.createItemStack(Items.POTION, Potions.HEALING));
        helper.assertValueEqual(Replacements.find(inventory, healing, 0), 20, "slot picked to replace a Potion of Healing");
        helper.succeed();
    }

    /** A broken Fortune pickaxe is replaced by a Fortune one, however worn, before a plain one. */
    private static void replacerPrefersTheSameEnchantments(GameTestHelper helper) {
        Inventory inventory = survivalPlayer(helper).getInventory();
        ItemStack broken = new ItemStack(Items.DIAMOND_PICKAXE);
        broken.enchant(enchantment(helper, Enchantments.FORTUNE), 3);
        inventory.setItem(10, new ItemStack(Items.DIAMOND_PICKAXE));
        ItemStack worn = broken.copy();
        worn.set(DataComponents.DAMAGE, 1000);
        inventory.setItem(11, worn);

        helper.assertValueEqual(Replacements.find(inventory, broken, 0), 11, "slot picked");
        helper.succeed();
    }

    private static void replacerKnowsWhatUsingUpLeaves(GameTestHelper helper) {
        helper.assertTrue(Replacements.isLeftoverOf(new ItemStack(Items.MUSHROOM_STEW), new ItemStack(Items.BOWL)), "a bowl is not what a stew leaves");
        helper.assertTrue(Replacements.isLeftoverOf(PotionContents.createItemStack(Items.POTION, Potions.HEALING), new ItemStack(Items.GLASS_BOTTLE)), "a bottle is not what a potion leaves");
        // Emptying a water bucket is placing water, not using it up; the empty bucket is wanted back.
        helper.assertFalse(Replacements.isLeftoverOf(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET)), "an emptied water bucket counted as used up");
        helper.assertFalse(Replacements.isLeftoverOf(new ItemStack(Items.HONEY_BOTTLE, 3), new ItemStack(Items.HONEY_BOTTLE, 2)), "a stack with more left counted as used up");
        helper.succeed();
    }
}
