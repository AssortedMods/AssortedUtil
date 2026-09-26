package com.grim3212.assorted.doubledoors.common.door;

import com.grim3212.assorted.doubledoors.DoubleDoorsCommonMod;
import com.grim3212.assorted.doubledoors.api.DoubleDoorsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Which doors, trapdoors or fence gates open along with the one clicked. Blocks are recognised by
 * their class or vanilla tag and read through the shared state properties, so modded ones join in.
 */
public final class DoorPartners {

    private DoorPartners() {
    }

    /** Where the partners of the block at {@code pos} are; empty when it has none or is not a door at all. */
    public static List<BlockPos> find(Level level, BlockPos pos, BlockState state) {
        if (state.is(DoubleDoorsTags.Blocks.IGNORED_BY_DOUBLE_DOORS)) {
            return List.of();
        }

        if (isDoor(state)) {
            return DoubleDoorsCommonMod.COMMON_CONFIG.doubleDoorsDoors.get() ? doorPartner(level, pos, state) : List.of();
        }
        if (isTrapdoor(state)) {
            return DoubleDoorsCommonMod.COMMON_CONFIG.doubleDoorsTrapdoors.get() ? trapdoorPartners(level, pos, state) : List.of();
        }
        if (isFenceGate(state)) {
            return DoubleDoorsCommonMod.COMMON_CONFIG.doubleDoorsFenceGates.get() ? fenceGatePartners(level, pos, state) : List.of();
        }

        return List.of();
    }

    public static boolean isDoor(BlockState state) {
        return (state.getBlock() instanceof DoorBlock || state.is(BlockTags.DOORS))
                && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && state.hasProperty(BlockStateProperties.DOOR_HINGE) && state.hasProperty(BlockStateProperties.OPEN)
                && !state.is(DoubleDoorsTags.Blocks.IGNORED_BY_DOUBLE_DOORS);
    }

    public static boolean isTrapdoor(BlockState state) {
        return (state.getBlock() instanceof TrapDoorBlock || state.is(BlockTags.TRAPDOORS))
                && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && state.hasProperty(BlockStateProperties.HALF)
                && state.hasProperty(BlockStateProperties.OPEN) && !state.is(DoubleDoorsTags.Blocks.IGNORED_BY_DOUBLE_DOORS);
    }

    public static boolean isFenceGate(BlockState state) {
        return (state.getBlock() instanceof FenceGateBlock || state.is(BlockTags.FENCE_GATES))
                && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && state.hasProperty(BlockStateProperties.OPEN)
                && !state.is(DoubleDoorsTags.Blocks.IGNORED_BY_DOUBLE_DOORS);
    }

    /** The door beside this one hinged on the far side, as vanilla places the second door of a pair. */
    private static List<BlockPos> doorPartner(Level level, BlockPos pos, BlockState state) {
        BlockPos lower = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockState lowerState = level.getBlockState(lower);
        if (!isDoor(lowerState)) {
            return List.of();
        }

        Direction facing = lowerState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        DoorHingeSide hinge = lowerState.getValue(BlockStateProperties.DOOR_HINGE);
        BlockPos other = lower.relative(hinge == DoorHingeSide.RIGHT ? facing.getCounterClockWise() : facing.getClockWise());
        if (!level.isLoaded(other)) {
            return List.of();
        }

        BlockState otherState = level.getBlockState(other);
        boolean pair = isDoor(otherState) && otherState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
                && otherState.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing && otherState.getValue(BlockStateProperties.DOOR_HINGE) != hinge
                && otherState.getValue(BlockStateProperties.OPEN) == lowerState.getValue(BlockStateProperties.OPEN);
        return pair ? List.of(other) : List.of();
    }

    /**
     * The row this trapdoor is in, side by side along its hinge, and the row facing it that closes the
     * other half of the hatch. A floor of trapdoors all facing one way is never more than those two rows.
     */
    private static List<BlockPos> trapdoorPartners(Level level, BlockPos pos, BlockState state) {
        int max = DoubleDoorsCommonMod.COMMON_CONFIG.doubleDoorsMaxConnected.get();
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        List<BlockPos> partners = new ArrayList<>();

        addTrapdoorRow(level, pos, state, facing, partners, max);

        BlockPos counterpart = pos.relative(facing);
        if (partners.size() < max && level.isLoaded(counterpart)) {
            BlockState counterpartState = level.getBlockState(counterpart);
            if (trapdoorMatches(state, counterpartState, facing.getOpposite())) {
                partners.add(counterpart);
                addTrapdoorRow(level, counterpart, counterpartState, facing.getOpposite(), partners, max);
            }
        }

        return partners;
    }

    private static void addTrapdoorRow(Level level, BlockPos start, BlockState startState, Direction facing, List<BlockPos> partners, int max) {
        for (Direction along : new Direction[]{facing.getClockWise(), facing.getCounterClockWise()}) {
            BlockPos next = start.relative(along);
            while (partners.size() < max && level.isLoaded(next) && trapdoorMatches(startState, level.getBlockState(next), facing)) {
                partners.add(next);
                next = next.relative(along);
            }
        }
    }

    private static boolean trapdoorMatches(BlockState clicked, BlockState other, Direction facing) {
        return isTrapdoor(other) && other.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing
                && other.getValue(BlockStateProperties.HALF) == clicked.getValue(BlockStateProperties.HALF)
                && other.getValue(BlockStateProperties.OPEN) == clicked.getValue(BlockStateProperties.OPEN);
    }

    /** Gates stacked on each other, as in 1.12, and now also the ones beside it in the same fence line. */
    private static List<BlockPos> fenceGatePartners(Level level, BlockPos pos, BlockState state) {
        int max = DoubleDoorsCommonMod.COMMON_CONFIG.doubleDoorsMaxConnected.get();
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis();
        Direction side = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
        Direction[] neighbours = {Direction.UP, Direction.DOWN, side, side.getOpposite()};

        List<BlockPos> partners = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        seen.add(pos);
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(pos);
        while (!queue.isEmpty() && partners.size() < max) {
            BlockPos current = queue.poll();
            for (Direction direction : neighbours) {
                BlockPos next = current.relative(direction);
                if (partners.size() >= max || !seen.add(next) || !level.isLoaded(next)) {
                    continue;
                }

                BlockState nextState = level.getBlockState(next);
                // Either facing on the axis: a gate turns to face whoever opened it last.
                if (isFenceGate(nextState) && nextState.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis() == axis
                        && nextState.getValue(BlockStateProperties.OPEN) == state.getValue(BlockStateProperties.OPEN)) {
                    partners.add(next);
                    queue.add(next);
                }
            }
        }

        return partners;
    }
}
