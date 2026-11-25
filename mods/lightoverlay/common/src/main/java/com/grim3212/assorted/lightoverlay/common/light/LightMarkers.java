package com.grim3212.assorted.lightoverlay.common.light;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Where the light overlay puts its numbers, which number and in which colour. No client class is touched,
 * so a dedicated server's tests can scan a level the way the overlay does.
 */
public final class LightMarkers {

    public static final int ALWAYS_COLOR = 0xFFFF4040;
    public static final int WHEN_DARK_COLOR = 0xFFFFD700;
    public static final int NEVER_COLOR = 0xFF40E040;
    /** How far above the floor a number lies, so it does not fight the block's top face. */
    public static final double LIFT = 0.02D;

    private LightMarkers() {
    }

    /** One number on the overlay: the block light at a spot a monster could stand on, in the colour of its danger. */
    public record LightMarker(double x, double y, double z, int light, int color) {
    }

    public static int color(SpawnLight light) {
        return switch (light) {
            case NEVER -> NEVER_COLOR;
            case WHEN_DARK -> WHEN_DARK_COLOR;
            default -> ALWAYS_COLOR;
        };
    }

    /** How many blocks above and below the player a scan of {@code radius} reaches. */
    public static int height(int radius) {
        return Math.max(radius / 2, 4);
    }

    /** Every marker in the circle around {@code centre}, as one scan. */
    public static List<LightMarker> scan(Level level, Predicate<BlockPos> visible, BlockPos centre, int radius, boolean showSafe) {
        List<LightMarker> found = new ArrayList<>();
        scanColumns(level, visible, centre, radius, -radius, radius, showSafe, found);
        return found;
    }

    /** The markers in rows {@code fromDx} to {@code toDx} of the circle around {@code centre}. */
    public static void scanColumns(Level level, Predicate<BlockPos> visible, BlockPos centre, int radius, int fromDx, int toDx, boolean showSafe, List<LightMarker> found) {
        int height = height(radius);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = fromDx; dx <= toDx; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }
                for (int dy = -height; dy <= height; dy++) {
                    pos.set(centre.getX() + dx, centre.getY() + dy, centre.getZ() + dz);
                    // Caves sealed off from the camera are left to the renderer's own culling to skip.
                    if (!level.isLoaded(pos) || !visible.test(pos)) {
                        continue;
                    }

                    SpawnLight light = SpawnLight.at(level, pos);
                    if (light == SpawnLight.NONE || (light == SpawnLight.NEVER && !showSafe)) {
                        continue;
                    }

                    found.add(new LightMarker(pos.getX() + 0.5D, pos.getY() + SpawnLight.floor(level, pos) + LIFT, pos.getZ() + 0.5D,
                            level.getBrightness(LightLayer.BLOCK, pos), color(light)));
                }
            }
        }
    }
}
