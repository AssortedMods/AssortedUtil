package com.grim3212.assorted.lightoverlay.client.light;

import com.grim3212.assorted.lib.conditions.PartToggles;
import com.grim3212.assorted.lightoverlay.Constants;
import com.grim3212.assorted.lightoverlay.client.LightOverlayClient;
import com.grim3212.assorted.lightoverlay.common.light.SpawnLight;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Numbers on the ground around the player with the block light there, wherever a monster could stand:
 * red where one can spawn at any time, yellow only in the dark of night or a storm, green never. See {@link SpawnLight}.
 */
public final class LightOverlay {

    private static final int ALWAYS_COLOR = 0xFFFF4040;
    private static final int WHEN_DARK_COLOR = 0xFFFFD700;
    private static final int NEVER_COLOR = 0xFF40E040;
    // A dark plate behind each number reads as well as an outline, for one quad instead of eight copies of the glyphs.
    private static final int PLATE = 0x90000000;
    private static final int RESCAN_TICKS = 20;
    // A full scan at radius 32 costs tens of milliseconds, so it is spread over a second: new numbers about every two.
    private static final int SCAN_SPREAD_TICKS = 20;
    // The first scan after the key is pressed goes faster, so the numbers come up at once.
    private static final int FIRST_SCAN_SPREAD_TICKS = 5;
    private static final float TEXT_SCALE = 0.04F;

    // Light only runs 0 to 15, so each number's text and width are worked out once.
    private static final FormattedCharSequence[] NUMBERS = new FormattedCharSequence[16];
    private static final float[] OFFSETS = new float[16];

    private static boolean shown;
    private static List<LightMarker> markers = List.of();
    private static @Nullable List<LightMarker> building;
    private static BlockPos scanCentre = BlockPos.ZERO;
    private static int scanRadius;
    private static int nextColumn;
    private static int ticksUntilScan;
    private static int spreadTicks = FIRST_SCAN_SPREAD_TICKS;

    private LightOverlay() {
    }

    public static void toggle(Minecraft minecraft) {
        shown = !shown;
        building = null;
        ticksUntilScan = 0;
        spreadTicks = FIRST_SCAN_SPREAD_TICKS;
        if (minecraft.player != null) {
            minecraft.player.sendOverlayMessage(Component.translatable(shown ? "hud.assortedlightoverlay.light_overlay.on" : "hud.assortedlightoverlay.light_overlay.off"));
        }
    }

    public static boolean isShown() {
        return shown;
    }

    /** Scans a slice of the circle each tick, and swaps the new markers in once the whole circle is done. */
    public static void tick(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (!shown || !PartToggles.isEnabled(Constants.MOD_ID) || player == null || minecraft.level == null) {
            markers = List.of();
            building = null;
            return;
        }

        if (building == null) {
            if (--ticksUntilScan > 0) {
                return;
            }
            building = new ArrayList<>();
            scanCentre = player.blockPosition();
            scanRadius = LightOverlayClient.CONFIG.lightOverlayRadius.get();
            nextColumn = -scanRadius;
        }

        int columnsPerTick = Math.max(1, (scanRadius * 2 + spreadTicks) / spreadTicks);
        int last = Math.min(scanRadius, nextColumn + columnsPerTick - 1);
        scanColumns(minecraft.level, VisibleSections.capture(minecraft), scanCentre, scanRadius, nextColumn, last, LightOverlayClient.CONFIG.lightOverlayShowSafe.get(), building);
        nextColumn = last + 1;
        if (nextColumn > scanRadius) {
            markers = building;
            building = null;
            ticksUntilScan = RESCAN_TICKS;
            spreadTicks = SCAN_SPREAD_TICKS;
        }
    }

    public static List<LightMarker> scan(ClientLevel level, BlockPos centre, int radius, boolean showSafe) {
        List<LightMarker> found = new ArrayList<>();
        scanColumns(level, VisibleSections.capture(Minecraft.getInstance()), centre, radius, -radius, radius, showSafe, found);
        return found;
    }

    /** The markers in rows {@code fromDx} to {@code toDx} of the circle around {@code centre}. */
    private static void scanColumns(ClientLevel level, VisibleSections visible, BlockPos centre, int radius, int fromDx, int toDx, boolean showSafe, List<LightMarker> found) {
        int height = Math.max(radius / 2, 4);
        if (NUMBERS[0] == null) {
            Font font = Minecraft.getInstance().font;
            for (int light = 0; light < NUMBERS.length; light++) {
                NUMBERS[light] = FormattedCharSequence.forward(Integer.toString(light), Style.EMPTY);
                OFFSETS[light] = -font.width(Integer.toString(light)) / 2.0F;
            }
        }

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = fromDx; dx <= toDx; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }
                for (int dy = -height; dy <= height; dy++) {
                    pos.set(centre.getX() + dx, centre.getY() + dy, centre.getZ() + dz);
                    // Caves sealed off from the camera are left to the renderer's own culling to skip.
                    if (!level.isLoaded(pos) || !visible.isVisible(pos)) {
                        continue;
                    }

                    SpawnLight light = SpawnLight.at(level, pos);
                    if (light == SpawnLight.NONE || (light == SpawnLight.NEVER && !showSafe)) {
                        continue;
                    }

                    int number = level.getBrightness(LightLayer.BLOCK, pos);
                    int color = switch (light) {
                        case NEVER -> NEVER_COLOR;
                        case WHEN_DARK -> WHEN_DARK_COLOR;
                        default -> ALWAYS_COLOR;
                    };
                    found.add(new LightMarker(pos.getX() + 0.5D, pos.getY() + SpawnLight.floor(level, pos) + 0.02D, pos.getZ() + 0.5D,
                            NUMBERS[number], OFFSETS[number], color));
                }
            }
        }
    }

    public static void submit(PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (markers.isEmpty()) {
            return;
        }

        Vec3 cam = camera.pos;
        // Squared to the nearest side, so the numbers read along the blocks rather than swivel with the view.
        float facing = Math.round(camera.yRot / 90.0F) * 90.0F;
        for (LightMarker marker : markers) {
            // Only what is on screen: most of the circle is behind the player or off to the side.
            if (!camera.cullFrustum.isVisible(new AABB(marker.x() - 0.5D, marker.y() - 0.1D, marker.z() - 0.5D, marker.x() + 0.5D, marker.y() + 0.1D, marker.z() + 0.5D))) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(marker.x() - cam.x, marker.y() - cam.y, marker.z() - cam.z);
            // Lying flat, turned so its top points the way the player looks.
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
            collector.submitText(poseStack, marker.offset(), -4.0F, marker.text(), false, Font.DisplayMode.POLYGON_OFFSET,
                    LightCoordsUtil.FULL_BRIGHT, marker.color(), PLATE, 0);
            poseStack.popPose();
        }
    }
}
