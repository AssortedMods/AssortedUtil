package com.grim3212.assorted.damagenumbers.gametest;

import com.grim3212.assorted.damagenumbers.client.damage.DamageKind;
import com.grim3212.assorted.damagenumbers.client.damage.DamagePopup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** How a damage number names and writes what hit, which a dedicated server can still check. */
final class DamageNumbersTests {

    private DamageNumbersTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("damage_numbers_name_their_source", DamageNumbersTests::damageNumbersNameTheirSource);
        out.accept("modded_damage_types_are_sorted", DamageNumbersTests::moddedDamageTypesAreSorted);
        out.accept("damage_numbers_say_what_hit", DamageNumbersTests::damageNumbersSayWhatHit);
        out.accept("damage_numbers_bounce_and_fade", DamageNumbersTests::damageNumbersBounceAndFade);
    }

    private static void damageNumbersNameTheirSource(GameTestHelper helper) {
        DamageSources sources = helper.getLevel().damageSources();
        Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(4, 1, 4));

        helper.assertValueEqual(DamageKind.of(sources.inFire()), DamageKind.FIRE, "in fire");
        helper.assertValueEqual(DamageKind.of(sources.lava()), DamageKind.FIRE, "lava");
        helper.assertValueEqual(DamageKind.of(sources.fall()), DamageKind.FALL, "fall");
        helper.assertValueEqual(DamageKind.of(sources.drown()), DamageKind.DROWNING, "drowning");
        helper.assertValueEqual(DamageKind.of(sources.starve()), DamageKind.STARVATION, "starving");
        helper.assertValueEqual(DamageKind.of(sources.cactus()), DamageKind.CACTUS, "cactus");
        helper.assertValueEqual(DamageKind.of(sources.magic()), DamageKind.MAGIC, "magic");
        helper.assertValueEqual(DamageKind.of(sources.mobAttack(zombie)), DamageKind.MELEE, "a zombie's hit");
        helper.assertValueEqual(DamageKind.of(sources.explosion(null, null)), DamageKind.EXPLOSION, "an explosion");
        helper.assertValueEqual(DamageKind.of(sources.generic()), DamageKind.GENERIC, "generic");
        helper.assertValueEqual(DamageKind.of(null), DamageKind.GENERIC, "no known source");

        helper.assertValueEqual(DamagePopup.amount(3.0F), "3", "three");
        helper.assertValueEqual(DamagePopup.amount(2.5F), "2.5", "two and a half");
        helper.succeed();
    }

    // test_spell is in the gametest data's neoforge:is_magic tag, test_curse in nothing.
    private static void moddedDamageTypesAreSorted(GameTestHelper helper) {
        DamageSource spell = testDamage(helper, "test_spell");
        DamageSource curse = testDamage(helper, "test_curse");
        Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(4, 1, 4));

        helper.assertValueEqual(DamageKind.of(spell), DamageKind.MAGIC, "a modded spell");
        helper.assertValueEqual(DamageKind.of(helper.getLevel().damageSources().thorns(zombie)), DamageKind.MELEE, "thorns, which NeoForge calls magic");

        helper.assertValueEqual(DamageKind.of(curse), DamageKind.GENERIC, "an untagged modded type");
        // Pinned, so a change to the hashing that would recolour every modded type on update fails here.
        helper.assertValueEqual(DamageKind.GENERIC.color(curse), 0xDC8353, "an untagged modded type's own colour");
        helper.assertValueEqual(DamageKind.GENERIC.color(helper.getLevel().damageSources().generic()), DamageKind.GENERIC.color(), "vanilla generic stays grey");
        helper.assertValueEqual(DamageKind.GENERIC.label(curse).getString(), "Test Curse", "an untagged modded type's name, from its id");
        helper.assertValueEqual(DamageKind.GENERIC.label(helper.getLevel().damageSources().generic()), DamageKind.GENERIC.label(), "vanilla generic keeps its label");
        helper.assertValueEqual(DamageKind.nameOf(ResourceLocation.fromNamespaceAndPath("mymod", "hazards/acid__splash")), "Acid Splash", "a nested, double-underscored id");
        helper.succeed();
    }

    /** What the client gametest's sword hit checked, without a client: the hit's kind, colour and label. */
    private static void damageNumbersSayWhatHit(GameTestHelper helper) {
        DamageSources sources = helper.getLevel().damageSources();
        Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(4, 1, 4));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        DamageSource bareHanded = sources.playerAttack(player);
        helper.assertValueEqual(DamageKind.of(bareHanded), DamageKind.MELEE, "a player's punch");
        helper.assertValueEqual(DamageKind.describe(DamageKind.MELEE, bareHanded), DamageKind.MELEE.label(), "a punch's label");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
        DamageSource sword = sources.playerAttack(player);
        helper.assertValueEqual(DamageKind.of(sword), DamageKind.MELEE, "a sword hit");
        helper.assertValueEqual(DamageKind.MELEE.color(sword), DamageKind.MELEE.color(), "a sword hit's colour");
        helper.assertValueEqual(DamageKind.describe(DamageKind.MELEE, sword), new ItemStack(Items.DIAMOND_SWORD).getHoverName(), "a sword hit's label");

        helper.assertValueEqual(DamageKind.describe(DamageKind.MELEE, sources.mobAttack(zombie)), zombie.getName(), "a zombie's hit's label");

        Arrow arrow = helper.spawn(EntityType.ARROW, new BlockPos(2, 1, 2));
        DamageSource shot = sources.arrow(arrow, zombie);
        helper.assertValueEqual(DamageKind.of(shot), DamageKind.PROJECTILE, "an arrow");
        helper.assertValueEqual(DamageKind.describe(DamageKind.PROJECTILE, shot), arrow.getName(), "an arrow's label");
        helper.assertValueEqual(DamageKind.describe(DamageKind.FIRE, sources.inFire()), DamageKind.FIRE.label(), "fire's label");

        Component fire = Component.literal("Fire");
        helper.assertValueEqual(DamagePopup.text(-3.0F, fire, true).getString(), "-3 (Fire)", "three damage by fire");
        helper.assertValueEqual(DamagePopup.text(-3.0F, fire, false).getString(), "-3", "three damage, with the source hidden");
        helper.assertValueEqual(DamagePopup.text(2.5F, Component.literal("Heal"), true).getString(), "+2.5 (Heal)", "healing two and a half");
        helper.succeed();
    }

    /** A number is thrown up, never falls through the floor it bounces on, holds solid for half its life and is gone at the end. */
    private static void damageNumbersBounceAndFade(GameTestHelper helper) {
        int lifetime = 40;
        double floor = 64.25D;
        DamagePopup popup = new DamagePopup(FormattedCharSequence.EMPTY, 0.0F, DamageKind.FIRE.color(), lifetime, 0.5D, floor + 1.5D, 0.5D, floor, RandomSource.create(3212L));
        helper.assertValueEqual(popup.alpha(0.0F), 255, "a new number's alpha");

        double highest = popup.y();
        for (int age = 1; age <= lifetime; age++) {
            boolean alive = popup.tick();
            helper.assertTrue(popup.y() >= floor, "the number fell through its floor at tick " + age);
            highest = Math.max(highest, popup.y());
            if (age == lifetime / 2) {
                helper.assertValueEqual(popup.alpha(0.0F), 255, "the alpha halfway through its life");
            }
            if (age == lifetime * 3 / 4) {
                int alpha = popup.alpha(0.0F);
                helper.assertTrue(alpha > 0 && alpha < 255, "three quarters through its life it should be fading, not at alpha " + alpha);
            }
            helper.assertValueEqual(alive, age < lifetime, "whether the number is still showing at tick " + age);
        }
        helper.assertTrue(highest > floor + 1.5D, "the number was never thrown up");
        helper.assertValueEqual(popup.alpha(0.0F), 0, "a spent number's alpha");
        helper.succeed();
    }

    private static DamageSource testDamage(GameTestHelper helper, String name) {
        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("assorteddamagenumbers", name));
        return new DamageSource(helper.getLevel().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
    }
}
