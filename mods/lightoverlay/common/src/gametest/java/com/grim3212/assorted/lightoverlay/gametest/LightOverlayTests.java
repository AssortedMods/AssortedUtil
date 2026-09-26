package com.grim3212.assorted.lightoverlay.gametest;

import com.grim3212.assorted.lightoverlay.common.light.SpawnLight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** The rules under the light overlay, which a dedicated server can still check. */
final class LightOverlayTests {

    private LightOverlayTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("light_overlay_marks_where_monsters_spawn", LightOverlayTests::lightOverlayMarksWhereMonstersSpawn);
        out.accept("light_overlay_needs_room_for_a_zombie", LightOverlayTests::lightOverlayNeedsRoomForAZombie);
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
        LightOverlayTestSupport.setDoor(helper, door, Blocks.OAK_DOOR.defaultBlockState());
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
}
