package com.grim3212.assorted.util.common.light;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;

/** Whether a zombie-sized monster could spawn standing at a spot, going by light alone: any time, only in the dark, or never. */
public enum SpawnLight {
    /** Not somewhere a monster can stand at all. */
    NONE,
    NEVER,
    WHEN_DARK,
    ALWAYS;

    private static final EntityType<?> MONSTER = EntityTypes.ZOMBIE;

    /** Where NaturalSpawner would put one, then Monster#isDarkEnoughToSpawn without the dice or the hour. */
    public static SpawnLight at(Level level, BlockPos pos) {
        // The hitbox test is NaturalSpawner's own: without it a fence gate or a wall reads as room to spawn.
        if (!SpawnPlacementTypes.ON_GROUND.isSpawnPositionOk(level, pos, MONSTER)
                || !level.noCollision(MONSTER.getSpawnAABB(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D))) {
            return NONE;
        }

        DimensionType dimension = level.dimensionType();
        int blockLimit = dimension.monsterSpawnBlockLightLimit();
        int darkest = dimension.monsterSpawnLightTest().maxInclusive();
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        if ((blockLimit < 15 && blockLight > blockLimit) || blockLight > darkest) {
            return NEVER;
        }

        // Bright sky only keeps them off by day; night or a storm lets them spawn.
        return level.getBrightness(LightLayer.SKY, pos) > darkest ? WHEN_DARK : ALWAYS;
    }

    /**
     * How high the floor a monster stands on is within the block: a carpet's or snow's top, not a door along
     * one edge. The support shape, as one layer of snow has no collision.
     */
    public static double floor(BlockGetter level, BlockPos pos) {
        double floor = 0.0D;
        for (AABB box : level.getBlockState(pos).getBlockSupportShape(level, pos).toAabbs()) {
            if (box.minX <= 0.5D && box.maxX >= 0.5D && box.minZ <= 0.5D && box.maxZ >= 0.5D) {
                floor = Math.max(floor, box.maxY);
            }
        }
        return floor;
    }
}
