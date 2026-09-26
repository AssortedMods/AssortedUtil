package com.grim3212.assorted.lightoverlay.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * Helpers and constants shared by Assorted Light Overlay's gametest classes, which import them statically,
 * alongside AssortedLib's {@code TestSupport}.
 */
final class LightOverlayTestSupport {

    private LightOverlayTestSupport() {
    }

    /** Middle of the 9x9x9 box, one block above its floor - room on every side for a drop. */
    static final BlockPos CENTRE = new BlockPos(4, 1, 4);

    /** Sets both halves of a door, as a player placing it would. */
    static void setDoor(GameTestHelper helper, BlockPos lower, BlockState state) {
        helper.setBlock(lower, state.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(lower.above(), state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }
}
