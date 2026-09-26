package com.grim3212.assorted.doubledoors.gametest;

import com.grim3212.assorted.doubledoors.common.door.DoorPartners;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.doubledoors.gametest.DoubleDoorsTestSupport.*;

/** Double doors, trapdoor hatches and runs of fence gates. */
final class DoubleDoorTests {

    private DoubleDoorTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("placed_door_pair_finds_each_other", DoubleDoorTests::placedDoorPairFindsEachOther);
        out.accept("double_door_opens_and_closes_together", DoubleDoorTests::doubleDoorOpensAndClosesTogether);
        out.accept("sneaking_opens_one_door", DoubleDoorTests::sneakingOpensOneDoor);
        out.accept("iron_partner_stays_shut", DoubleDoorTests::ironPartnerStaysShut);
        out.accept("trapdoor_hatch_opens_together", DoubleDoorTests::trapdoorHatchOpensTogether);
        out.accept("fence_gates_open_together", DoubleDoorTests::fenceGatesOpenTogether);
    }

    /** Two doors placed side by side the way a player would, so vanilla picks the hinges, are each other's partner. */
    private static void placedDoorPairFindsEachOther(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        player.setYRot(180.0F);
        BlockPos left = new BlockPos(3, 1, 4);
        BlockPos right = new BlockPos(4, 1, 4);
        placeDoor(helper, player, left);
        placeDoor(helper, player, right);

        helper.assertTrue(helper.getBlockState(left).getValue(DoorBlock.HINGE) != helper.getBlockState(right).getValue(DoorBlock.HINGE), "vanilla gave both doors the same hinge");
        helper.assertValueEqual(partners(helper, left), List.of(helper.absolutePos(right)), "partners of the first door");
        helper.assertValueEqual(partners(helper, right.above()), List.of(helper.absolutePos(left)), "partners of the second door's top half");
        helper.succeed();
    }

    private static void doubleDoorOpensAndClosesTogether(GameTestHelper helper) {
        ServerPlayer player = mortalPlayer(helper, new BlockPos(4, 1, 6));
        doorPair(helper, Blocks.OAK_DOOR.defaultBlockState(), Blocks.SPRUCE_DOOR.defaultBlockState());

        use(helper, player, new BlockPos(3, 1, 4));
        helper.assertBlockProperty(new BlockPos(3, 1, 4), DoorBlock.OPEN, true);
        helper.assertBlockProperty(new BlockPos(4, 2, 4), DoorBlock.OPEN, true);

        use(helper, player, new BlockPos(4, 2, 4));
        helper.assertBlockProperty(new BlockPos(3, 1, 4), DoorBlock.OPEN, false);
        helper.assertBlockProperty(new BlockPos(4, 1, 4), DoorBlock.OPEN, false);
        helper.succeed();
    }

    private static void sneakingOpensOneDoor(GameTestHelper helper) {
        ServerPlayer player = mortalPlayer(helper, new BlockPos(4, 1, 6));
        player.setShiftKeyDown(true);
        doorPair(helper, Blocks.OAK_DOOR.defaultBlockState(), Blocks.OAK_DOOR.defaultBlockState());

        use(helper, player, new BlockPos(3, 1, 4));

        helper.assertBlockProperty(new BlockPos(3, 1, 4), DoorBlock.OPEN, true);
        helper.assertBlockProperty(new BlockPos(4, 1, 4), DoorBlock.OPEN, false);
        helper.succeed();
    }

    /** The partner is used the way a click on it would be, so an iron door still wants redstone. */
    private static void ironPartnerStaysShut(GameTestHelper helper) {
        ServerPlayer player = mortalPlayer(helper, new BlockPos(4, 1, 6));
        doorPair(helper, Blocks.OAK_DOOR.defaultBlockState(), Blocks.IRON_DOOR.defaultBlockState());

        use(helper, player, new BlockPos(3, 1, 4));

        helper.assertBlockProperty(new BlockPos(3, 1, 4), DoorBlock.OPEN, true);
        helper.assertBlockProperty(new BlockPos(4, 1, 4), DoorBlock.OPEN, false);
        helper.succeed();
    }

    /**
     * Two rows of two, hinged on the outer edges, open as one hatch. A third row behind, facing the same
     * way as the first, is a floor rather than part of the hatch and stays shut; 1.12 opened it.
     */
    private static void trapdoorHatchOpensTogether(GameTestHelper helper) {
        ServerPlayer player = mortalPlayer(helper, new BlockPos(6, 1, 6));
        BlockState north = Blocks.OAK_TRAPDOOR.defaultBlockState().setValue(TrapDoorBlock.FACING, Direction.NORTH);
        BlockState south = Blocks.OAK_TRAPDOOR.defaultBlockState().setValue(TrapDoorBlock.FACING, Direction.SOUTH);
        List<BlockPos> hatch = List.of(new BlockPos(3, 2, 5), new BlockPos(4, 2, 5), new BlockPos(3, 2, 4), new BlockPos(4, 2, 4));
        helper.setBlock(hatch.get(0), north);
        helper.setBlock(hatch.get(1), north);
        helper.setBlock(hatch.get(2), south);
        helper.setBlock(hatch.get(3), south);
        BlockPos floor = new BlockPos(4, 2, 6);
        helper.setBlock(floor, north);

        use(helper, player, hatch.get(1));

        for (BlockPos pos : hatch) {
            helper.assertBlockProperty(pos, TrapDoorBlock.OPEN, true);
        }
        helper.assertBlockProperty(floor, TrapDoorBlock.OPEN, false);
        helper.succeed();
    }

    /** Stacked, as in 1.12, and side by side in the same fence line. One in front, across the line, stays shut. */
    private static void fenceGatesOpenTogether(GameTestHelper helper) {
        ServerPlayer player = mortalPlayer(helper, new BlockPos(4, 1, 7));
        player.setYRot(180.0F);
        BlockState gate = Blocks.OAK_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.NORTH);
        List<BlockPos> run = List.of(new BlockPos(4, 1, 4), new BlockPos(4, 2, 4), new BlockPos(5, 1, 4));
        run.forEach(pos -> helper.setBlock(pos, gate));
        BlockPos across = new BlockPos(4, 1, 5);
        helper.setBlock(across, gate);

        use(helper, player, run.getFirst());

        for (BlockPos pos : run) {
            helper.assertBlockProperty(pos, FenceGateBlock.OPEN, true);
        }
        helper.assertBlockProperty(across, FenceGateBlock.OPEN, false);
        helper.succeed();
    }

    /** A closed pair facing north, the west door hinged on the left, as {@link #placedDoorPairFindsEachOther} shows vanilla places them. */
    private static void doorPair(GameTestHelper helper, BlockState west, BlockState east) {
        setDoor(helper, new BlockPos(3, 1, 4), west.setValue(DoorBlock.FACING, Direction.NORTH).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT));
        setDoor(helper, new BlockPos(4, 1, 4), east.setValue(DoorBlock.FACING, Direction.NORTH).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT));
    }

    private static void placeDoor(GameTestHelper helper, ServerPlayer player, BlockPos lower) {
        ItemStack door = new ItemStack(Items.OAK_DOOR);
        player.setItemInHand(InteractionHand.MAIN_HAND, door);
        door.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(lower.below()))));
        helper.assertBlockPresent(Blocks.OAK_DOOR, lower);
    }

    private static List<BlockPos> partners(GameTestHelper helper, BlockPos rel) {
        BlockPos pos = helper.absolutePos(rel);
        return DoorPartners.find(helper.getLevel(), pos, helper.getLevel().getBlockState(pos));
    }
}
