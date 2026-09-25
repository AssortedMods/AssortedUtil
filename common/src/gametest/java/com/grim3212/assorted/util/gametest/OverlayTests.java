package com.grim3212.assorted.util.gametest;

import com.grim3212.assorted.util.client.damage.DamageKind;
import com.grim3212.assorted.util.client.damage.DamagePopup;
import com.grim3212.assorted.util.common.light.SpawnLight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** The rules under the two client overlays, which a dedicated server can still check. */
final class OverlayTests {

    private OverlayTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("light_overlay_marks_where_monsters_spawn", OverlayTests::lightOverlayMarksWhereMonstersSpawn);
        out.accept("light_overlay_needs_room_for_a_zombie", OverlayTests::lightOverlayNeedsRoomForAZombie);
        out.accept("damage_numbers_name_their_source", OverlayTests::damageNumbersNameTheirSource);
    }

    /**
     * Walled in and roofed over, a monster can spawn; beside glowstone it cannot; on glass nothing stands.
     * Light is worked out a tick or more after a block changes, hence succeedWhen.
     */
    private static void lightOverlayMarksWhereMonstersSpawn(GameTestHelper helper) {
        BlockPos dark = new BlockPos(2, 1, 2);
        for (int y = 1; y <= 2; y++) {
            for (Direction side : Direction.Plane.HORIZONTAL) {
                helper.setBlock(dark.atY(y).relative(side), Blocks.STONE);
            }
        }
        helper.setBlock(dark.atY(3), Blocks.STONE);
        BlockPos lit = new BlockPos(6, 1, 6);
        helper.setBlock(lit.east(), Blocks.GLOWSTONE);
        BlockPos onGlass = new BlockPos(1, 1, 7);
        helper.setBlock(onGlass.below(), Blocks.GLASS);

        helper.succeedWhen(() -> {
            helper.assertValueEqual(SpawnLight.at(helper.getLevel(), helper.absolutePos(dark)), SpawnLight.ALWAYS, "walled in the dark");
            helper.assertValueEqual(SpawnLight.at(helper.getLevel(), helper.absolutePos(lit)), SpawnLight.NEVER, "beside glowstone");
            helper.assertValueEqual(SpawnLight.at(helper.getLevel(), helper.absolutePos(onGlass)), SpawnLight.NONE, "on glass");
        });
    }

    /**
     * NaturalSpawner's hitbox test: nothing fits in a fence gate, a pane or a wall. A door is 3 pixels at one
     * edge, so a zombie does fit in the doorway, and its number goes on the floor there, not on the door.
     */
    private static void lightOverlayNeedsRoomForAZombie(GameTestHelper helper) {
        BlockPos gate = new BlockPos(1, 1, 1);
        BlockPos pane = new BlockPos(3, 1, 1);
        BlockPos wall = new BlockPos(5, 1, 1);
        BlockPos door = new BlockPos(1, 1, 5);
        BlockPos carpet = new BlockPos(5, 1, 5);
        helper.setBlock(gate, Blocks.OAK_FENCE_GATE);
        helper.setBlock(pane, Blocks.GLASS_PANE);
        helper.setBlock(wall, Blocks.COBBLESTONE_WALL);
        UtilTestSupport.setDoor(helper, door, Blocks.OAK_DOOR.defaultBlockState());
        helper.setBlock(carpet, Blocks.MOSS_CARPET);

        for (BlockPos blocked : new BlockPos[]{gate, pane, wall, gate.above(), pane.above(), wall.above()}) {
            helper.assertValueEqual(SpawnLight.at(helper.getLevel(), helper.absolutePos(blocked)), SpawnLight.NONE, "light at " + blocked);
        }
        helper.assertFalse(SpawnLight.at(helper.getLevel(), helper.absolutePos(door)) == SpawnLight.NONE, "a zombie fits in a doorway, but the overlay says not");
        helper.assertValueEqual(SpawnLight.at(helper.getLevel(), helper.absolutePos(door.above())), SpawnLight.NONE, "light on top of a door's lower half");
        helper.assertValueEqual(SpawnLight.floor(helper.getLevel(), helper.absolutePos(door)), 0.0D, "floor height in a doorway");
        helper.assertValueEqual(SpawnLight.floor(helper.getLevel(), helper.absolutePos(carpet)), 1.0D / 16.0D, "floor height on a carpet");
        BlockPos snow = new BlockPos(3, 1, 5);
        helper.setBlock(snow, Blocks.SNOW);
        helper.assertFalse(SpawnLight.at(helper.getLevel(), helper.absolutePos(snow)) == SpawnLight.NONE, "a zombie can stand in one layer of snow, but the overlay says not");
        helper.assertValueEqual(SpawnLight.floor(helper.getLevel(), helper.absolutePos(snow)), 2.0D / 16.0D, "floor height on a snow layer");
        helper.succeed();
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
