package com.grim3212.assorted.damagenumbers.gametest;

import com.grim3212.assorted.damagenumbers.client.damage.DamageKind;
import com.grim3212.assorted.damagenumbers.client.damage.DamagePopup;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.zombie.Zombie;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** How a damage number names and writes what hit, which a dedicated server can still check. */
final class DamageNumbersTests {

    private DamageNumbersTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("damage_numbers_name_their_source", DamageNumbersTests::damageNumbersNameTheirSource);
    }

    private static void damageNumbersNameTheirSource(GameTestHelper helper) {
        DamageSources sources = helper.getLevel().damageSources();
        Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(4, 1, 4));

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
}
