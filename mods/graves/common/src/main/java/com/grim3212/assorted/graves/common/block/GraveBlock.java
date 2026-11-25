package com.grim3212.assorted.graves.common.block;

import com.grim3212.assorted.graves.GravesCommonMod;
import com.grim3212.assorted.graves.common.block.entity.GraveBlockEntity;
import com.grim3212.assorted.graves.common.grave.Graves;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

/** A headstone over a slab of cover. {@code FACING} is the way the inscription faces. */
public class GraveBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final MapCodec<GraveBlock> CODEC = simpleCodec(GraveBlock::new);

    private static final Map<Direction, VoxelShape> SHAPES = rotations(
            Shapes.or(Block.box(1, 0, 14, 15, 15, 16), Block.box(2, 0, 1, 14, 1, 14)));

    public GraveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    /** The shape for each facing, from the one facing north, turned clockwise a quarter at a time. */
    private static Map<Direction, VoxelShape> rotations(VoxelShape north) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        VoxelShape shape = north;
        Direction facing = Direction.NORTH;
        for (int turn = 0; turn < 4; turn++) {
            shapes.put(facing, shape);
            VoxelShape[] turned = {Shapes.empty()};
            shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    turned[0] = Shapes.or(turned[0], Shapes.box(1.0D - maxZ, minY, minX, 1.0D - minZ, maxY, maxX)));
            shape = turned[0].optimize();
            facing = facing.getClockWise();
        }
        return shapes;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction directionToNeighbour, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, directionToNeighbour, neighbourState, level, pos, neighbourPos);
    }

    /** Spills what the grave still holds while its block entity exists; a state change such as waterlogging keeps it. */
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof GraveBlockEntity grave) {
            grave.onBlockRemoved(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraveBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof GraveBlockEntity grave)) {
            return InteractionResult.PASS;
        }

        if (!mayOpen(grave, player)) {
            if (player instanceof ServerPlayer) {
                player.displayClientMessage(Component.translatable("message.assortedgraves.grave.not_yours", grave.getOwnerName()), true);
            }
            return InteractionResult.FAIL;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            Graves.restore(serverPlayer, grave);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof GraveBlockEntity grave && !mayOpen(grave, player)) {
            return 0.0F;
        }

        return super.getDestroyProgress(state, player, level, pos);
    }

    private static boolean mayOpen(GraveBlockEntity grave, Player player) {
        return !GravesCommonMod.COMMON_CONFIG.gravesOwnerOnly.get() || grave.isOwner(player) || player.isCreative();
    }
}
