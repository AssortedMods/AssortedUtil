package com.grim3212.assorted.lightoverlay.client.light;

import com.grim3212.assorted.lib.conditions.PartToggles;
import com.grim3212.assorted.lightoverlay.Constants;
import com.grim3212.assorted.lightoverlay.client.LightOverlayClient;
import com.grim3212.assorted.lightoverlay.common.light.LightMarkers;
import com.grim3212.assorted.lightoverlay.common.light.LightMarkers.LightMarker;
import com.grim3212.assorted.lightoverlay.common.light.SpawnLight;
import com.grim3212.assorted.lightoverlay.mixin.client.LevelRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
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
    // Read once a scan: on 1.21.1 it means a walk over every section in the occlusion graph.
    private static VisibleSections scanVisible = VisibleSections.EVERYWHERE;
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
            minecraft.player.displayClientMessage(Component.translatable(shown ? "hud.assortedlightoverlay.light_overlay.on" : "hud.assortedlightoverlay.light_overlay.off"), true);
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
            scanVisible = VisibleSections.EVERYWHERE;
            return;
        }

        if (building == null) {
            if (--ticksUntilScan > 0) {
                return;
            }
            building = new ArrayList<>();
            scanCentre = player.blockPosition();
            scanVisible = VisibleSections.capture(minecraft);
            scanRadius = LightOverlayClient.CONFIG.lightOverlayRadius.get();
            nextColumn = -scanRadius;
        }

        int columnsPerTick = Math.max(1, (scanRadius * 2 + spreadTicks) / spreadTicks);
        int last = Math.min(scanRadius, nextColumn + columnsPerTick - 1);
        LightMarkers.scanColumns(minecraft.level, scanVisible::isVisible, scanCentre, scanRadius, nextColumn, last, LightOverlayClient.CONFIG.lightOverlayShowSafe.get(), building);
        nextColumn = last + 1;
        if (nextColumn > scanRadius) {
            markers = building;
            building = null;
            scanVisible = VisibleSections.EVERYWHERE;
            ticksUntilScan = RESCAN_TICKS;
            spreadTicks = SCAN_SPREAD_TICKS;
        }
    }

    public static List<LightMarker> scan(ClientLevel level, BlockPos centre, int radius, boolean showSafe) {
        return LightMarkers.scan(level, VisibleSections.capture(Minecraft.getInstance())::isVisible, centre, radius, showSafe);
    }

    public static void render(PoseStack poseStack, MultiBufferSource bufferSource, Camera camera, float partialTick) {
        if (markers.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        if (NUMBERS[0] == null) {
            for (int light = 0; light < NUMBERS.length; light++) {
                NUMBERS[light] = FormattedCharSequence.forward(Integer.toString(light), Style.EMPTY);
                OFFSETS[light] = -font.width(Integer.toString(light)) / 2.0F;
            }
        }
        // The frustum the level renderer culled this frame's sections with, already moved to the camera.
        @Nullable Frustum frustum = ((LevelRendererAccessor) minecraft.levelRenderer).assortedlightoverlay$cullingFrustum();
        Vec3 cam = camera.getPosition();
        // Squared to the nearest side, so the numbers read along the blocks rather than swivel with the view.
        float facing = Math.round(camera.getYRot() / 90.0F) * 90.0F;
        for (LightMarker marker : markers) {
            // Only what is on screen: most of the circle is behind the player or off to the side.
            if (frustum != null && !frustum.isVisible(new AABB(marker.x() - 0.5D, marker.y() - 0.1D, marker.z() - 0.5D, marker.x() + 0.5D, marker.y() + 0.1D, marker.z() + 0.5D))) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(marker.x() - cam.x, marker.y() - cam.y, marker.z() - cam.z);
            // Lying flat, turned so its top points the way the player looks.
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
            font.drawInBatch(NUMBERS[marker.light()], OFFSETS[marker.light()], -4.0F, marker.color(), false, poseStack.last().pose(), bufferSource,
                    Font.DisplayMode.POLYGON_OFFSET, PLATE, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        }
    }
}
