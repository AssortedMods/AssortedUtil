package com.grim3212.assorted.graves.gametest;

import com.grim3212.assorted.graves.common.block.GravesBlocks;
import com.grim3212.assorted.graves.common.block.entity.GraveBlockEntity;
import com.grim3212.assorted.graves.common.grave.Graves;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.graves.gametest.GravesTestSupport.*;

/** Graves: what a death puts in one, what a totem keeps out of one, and getting it all back. */
final class GraveTests {

    private static final int HEAD = 39;
    private static final int FEET = 36;

    private GraveTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("grave_takes_what_a_death_drops", GraveTests::graveTakesWhatADeathDrops);
        out.accept("totem_keeps_the_grave_away", GraveTests::totemKeepsTheGraveAway);
        out.accept("grave_puts_things_back_in_their_slots", GraveTests::gravePutsThingsBackInTheirSlots);
        out.accept("stranger_gets_armour_in_their_pack", GraveTests::strangerGetsArmourInTheirPack);
        out.accept("broken_grave_spills_everything", GraveTests::brokenGraveSpillsEverything);
        out.accept("grave_outlasts_an_explosion", GraveTests::graveOutlastsAnExplosion);
        out.accept("grave_finds_room_beside_a_wall", GraveTests::graveFindsRoomBesideAWall);
        out.accept("grave_experience_matches_vanilla_levels", GraveTests::graveExperienceMatchesVanillaLevels);
    }

    /**
     * Everything but the Curse of Vanishing boots goes in the grave, experience with it, and nothing is
     * left on the ground. Dying runs through each loader's own death path, so this covers Lib's hook.
     */
    private static void graveTakesWhatADeathDrops(GameTestHelper helper) {
        ServerPlayer player = mortalPlayer(helper, CENTRE);
        Inventory inventory = player.getInventory();
        inventory.setItem(0, new ItemStack(Items.STONE, 10));
        inventory.setItem(20, new ItemStack(Items.BREAD, 3));
        inventory.setItem(HEAD, new ItemStack(Items.DIAMOND_HELMET));
        inventory.setItem(Inventory.SLOT_OFFHAND, new ItemStack(Items.SHIELD));
        ItemStack cursed = new ItemStack(Items.IRON_BOOTS);
        cursed.enchant(enchantment(helper, Enchantments.VANISHING_CURSE), 1);
        inventory.setItem(FEET, cursed);
        player.experienceLevel = 10;
        player.experienceProgress = 0.5F;
        int experience = Graves.totalExperience(player);

        player.kill(helper.getLevel());

        helper.assertBlockPresent(GravesBlocks.GRAVE.get(), CENTRE);
        GraveBlockEntity grave = helper.getBlockEntity(CENTRE, GraveBlockEntity.class);
        List<ItemStackWithSlot> items = grave.getItems();
        helper.assertValueEqual(items.size(), 4, "stacks in the grave");
        helper.assertTrue(items.stream().noneMatch(item -> item.stack().is(Items.IRON_BOOTS)), "Curse of Vanishing boots went into the grave instead of vanishing");
        helper.assertTrue(items.stream().anyMatch(item -> item.slot() == HEAD && item.stack().is(Items.DIAMOND_HELMET)), "the helmet was not kept against its head slot");
        helper.assertValueEqual(grave.getExperience(), experience, "experience in the grave");
        helper.assertTrue(inventory.isEmpty(), "the dead player still holds " + inventory.getItem(0));
        helper.assertEntityNotPresent(EntityTypes.ITEM);
        helper.assertEntityNotPresent(EntityTypes.EXPERIENCE_ORB);
        // Or clearing the box at the end spills it into the next test.
        grave.clearContents();
        helper.succeed();
    }

    /** A totem saves the player after a grave hook would already have run on Fabric's ALLOW_DEATH. */
    private static void totemKeepsTheGraveAway(GameTestHelper helper) {
        ServerPlayer player = mortalPlayer(helper, CENTRE);
        player.getInventory().setItem(0, new ItemStack(Items.STONE, 10));
        player.getInventory().setItem(Inventory.SLOT_OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));

        player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 1000.0F);

        helper.assertTrue(player.isAlive(), "the totem did not save the player");
        helper.assertBlockNotPresent(GravesBlocks.GRAVE.get(), CENTRE);
        helper.assertValueEqual(countInInventory(player, Items.STONE), 10, "stone still carried");
        helper.succeed();
    }

    /** The owner gets each stack back in the slot it came from, the helmet on their head, and their experience. */
    private static void gravePutsThingsBackInTheirSlots(GameTestHelper helper) {
        ServerPlayer owner = mortalPlayer(helper, CENTRE);
        GraveBlockEntity grave = placeGrave(helper, owner, 50);

        use(helper, owner, CENTRE.north());

        helper.assertBlockNotPresent(GravesBlocks.GRAVE.get(), CENTRE.north());
        helper.assertTrue(owner.getInventory().getItem(3).is(Items.STONE), "the stone did not go back to hotbar slot 3");
        helper.assertTrue(owner.getInventory().getItem(HEAD).is(Items.DIAMOND_HELMET), "the helmet was not put back on");
        helper.assertValueEqual(Graves.totalExperience(owner), 50, "experience given back");
        helper.assertTrue(grave.getItems().isEmpty(), "the grave still holds items after being emptied");
        helper.assertEntityNotPresent(EntityTypes.ITEM);
        helper.succeed();
    }

    /** Someone else's armour goes in the pack: it might carry Curse of Binding. */
    private static void strangerGetsArmourInTheirPack(GameTestHelper helper) {
        ServerPlayer owner = survivalPlayer(helper);
        ServerPlayer stranger = mortalPlayer(helper, CENTRE);
        placeGrave(helper, owner, 0);

        use(helper, stranger, CENTRE.north());

        helper.assertTrue(stranger.getInventory().getItem(HEAD).isEmpty(), "the stranger was dressed in the dead player's helmet");
        helper.assertValueEqual(countInInventory(stranger, Items.DIAMOND_HELMET), 1, "helmets in the stranger's inventory");
        helper.assertValueEqual(countInInventory(stranger, Items.STONE), 10, "stone in the stranger's inventory");
        helper.succeed();
    }

    /** Broken by hand, a grave spills its items and experience rather than losing them. */
    private static void brokenGraveSpillsEverything(GameTestHelper helper) {
        placeGrave(helper, survivalPlayer(helper), 30);

        helper.getLevel().destroyBlock(helper.absolutePos(CENTRE.north()), false);

        helper.assertItemEntityCountIs(Items.STONE, CENTRE.north(), 2.0D, 10);
        helper.assertItemEntityCountIs(Items.DIAMOND_HELMET, CENTRE.north(), 2.0D, 1);
        helper.assertEntityPresent(EntityTypes.EXPERIENCE_ORB);
        helper.succeed();
    }

    private static void graveOutlastsAnExplosion(GameTestHelper helper) {
        GraveBlockEntity grave = placeGrave(helper, survivalPlayer(helper), 0);
        BlockPos at = helper.absolutePos(CENTRE);

        helper.getLevel().explode(null, at.getX() + 0.5D, at.getY() + 0.5D, at.getZ() + 0.5D, 4.0F, Level.ExplosionInteraction.TNT);

        helper.assertBlockPresent(GravesBlocks.GRAVE.get(), CENTRE.north());
        grave.clearContents();
        helper.succeed();
    }

    /** Dying inside a block, the grave takes the nearest free spot; walled in completely, there is none. */
    private static void graveFindsRoomBesideAWall(GameTestHelper helper) {
        BlockPos death = CENTRE.above();
        helper.setBlock(death, Blocks.STONE);
        BlockPos spot = Graves.findSpot(helper.getLevel(), helper.absolutePos(death), 2);
        helper.assertTrue(spot != null && spot.distManhattan(helper.absolutePos(death)) == 1, "the grave went to " + spot + ", not a block next to " + helper.absolutePos(death));
        BlockPos walledIn = Graves.findSpot(helper.getLevel(), helper.absolutePos(death), 0);
        helper.assertTrue(walledIn == null, "with nowhere to search, stone was replaced by a grave at " + walledIn);
        helper.succeed();
    }

    /** The closed form matches adding up Player#getXpNeededForNextLevel, across all three of its bands. */
    private static void graveExperienceMatchesVanillaLevels(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        long sum = 0;
        for (int level = 0; level <= 60; level++) {
            helper.assertValueEqual((long) Graves.pointsToReach(level), sum, "points to reach level " + level);
            player.experienceLevel = level;
            sum += player.getXpNeededForNextLevel();
        }
        helper.succeed();
    }

    /** A grave just north of the centre, as a death would leave it: stone in hotbar slot 3 and a helmet on. */
    private static GraveBlockEntity placeGrave(GameTestHelper helper, ServerPlayer owner, int experience) {
        helper.setBlock(CENTRE.north(), GravesBlocks.GRAVE.get());
        GraveBlockEntity grave = helper.getBlockEntity(CENTRE.north(), GraveBlockEntity.class);
        grave.fill(owner, List.of(new ItemStackWithSlot(3, new ItemStack(Items.STONE, 10)), new ItemStackWithSlot(HEAD, new ItemStack(Items.DIAMOND_HELMET))),
                experience, System.currentTimeMillis());
        return grave;
    }
}
