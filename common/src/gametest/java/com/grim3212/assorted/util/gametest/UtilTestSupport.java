package com.grim3212.assorted.util.gametest;

import com.grim3212.assorted.lib.test.TestSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * Helpers and constants shared by Assorted Util's gametest classes, which import them statically,
 * alongside AssortedLib's {@code TestSupport}.
 */
final class UtilTestSupport {

    private UtilTestSupport() {
    }

    /** Middle of the 9x9x9 box, one block above its floor - room on every side for a drop. */
    static final BlockPos CENTRE = new BlockPos(4, 1, 4);

    /**
     * A survival player standing on the floor at {@code rel} who can be hurt. Until its client says it has
     * loaded, a new player is immune to everything, /kill included.
     */
    static ServerPlayer mortalPlayer(GameTestHelper helper, BlockPos rel) {
        ServerPlayer player = TestSupport.survivalPlayer(helper);
        player.connection.handleAcceptPlayerLoad(new ServerboundPlayerLoadedPacket());
        TestSupport.stand(helper, player, rel.below());
        return player;
    }

    /** An empty-handed right click on the side of a relative {@code rel}, through the game mode so the loader's use-block event fires. */
    static InteractionResult use(GameTestHelper helper, ServerPlayer player, BlockPos rel) {
        return player.gameMode.useItemOn(player, helper.getLevel(), ItemStack.EMPTY, InteractionHand.MAIN_HAND,
                TestSupport.hitSide(helper.absolutePos(rel), Direction.SOUTH));
    }

    static Holder<Enchantment> enchantment(GameTestHelper helper, ResourceKey<Enchantment> key) {
        return helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    /** Sets both halves of a door, as a player placing it would. */
    static void setDoor(GameTestHelper helper, BlockPos lower, BlockState state) {
        helper.setBlock(lower, state.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(lower.above(), state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }
}
