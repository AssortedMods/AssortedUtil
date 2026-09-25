package com.grim3212.assorted.util.client.render;

import com.grim3212.assorted.util.common.block.GraveBlock;
import com.grim3212.assorted.util.common.block.entity.GraveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * The inscription on a headstone: who, and when in the viewer's own time zone. The model's headstone
 * face looks south at rotation 0, as a wall sign's does.
 */
public class GraveRenderer implements BlockEntityRenderer<GraveBlockEntity, GraveRenderer.GraveRenderState> {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private static final int ENGRAVED = 0xFF303030;
    // The face is 12 of 16 pixels wide; the scale is 1.12's, shrunk for a long name.
    private static final float MAX_WIDTH = 0.7F;
    private static final float MAX_SCALE = 0.01F;
    private static final float FACE_Z = -0.5F + 2.0F / 16.0F + 0.005F;

    private final Font font;

    public GraveRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public GraveRenderState createRenderState() {
        return new GraveRenderState();
    }

    @Override
    public void extractRenderState(GraveBlockEntity grave, GraveRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(grave, state, partialTicks, cameraPosition, breakProgress);
        state.yRot = -grave.getBlockState().getValue(GraveBlock.FACING).toYRot();

        state.lines.clear();
        state.widths.clear();
        this.addLine(state, Component.literal(grave.getOwnerName()));
        if (grave.getDeathTime() > 0) {
            Instant died = Instant.ofEpochMilli(grave.getDeathTime());
            this.addLine(state, Component.literal(DATE.format(died.atZone(ZoneId.systemDefault()))));
            this.addLine(state, Component.literal(TIME.format(died.atZone(ZoneId.systemDefault()))));
        }
    }

    private void addLine(GraveRenderState state, Component line) {
        state.lines.add(line.getVisualOrderText());
        state.widths.add(this.font.width(line));
    }

    @Override
    public void submit(GraveRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));

        float top = 0.8F;
        for (int i = 0; i < state.lines.size(); i++) {
            int width = state.widths.get(i);
            float scale = width > 0 ? Math.min(MAX_SCALE, MAX_WIDTH / width) : MAX_SCALE;

            poseStack.pushPose();
            poseStack.translate(0.0F, top, FACE_Z);
            poseStack.scale(scale, -scale, scale);
            collector.submitText(poseStack, -width / 2.0F, 0.0F, state.lines.get(i), false, Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, ENGRAVED, 0, 0);
            poseStack.popPose();

            top -= 10.0F * scale + 0.02F;
        }

        poseStack.popPose();
    }

    public static class GraveRenderState extends BlockEntityRenderState {
        public float yRot;
        public final List<FormattedCharSequence> lines = new ArrayList<>();
        public final List<Integer> widths = new ArrayList<>();
    }
}
