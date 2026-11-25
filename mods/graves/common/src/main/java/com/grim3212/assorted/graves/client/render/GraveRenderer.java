package com.grim3212.assorted.graves.client.render;

import com.grim3212.assorted.graves.common.block.GraveBlock;
import com.grim3212.assorted.graves.common.block.entity.GraveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

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
public class GraveRenderer implements BlockEntityRenderer<GraveBlockEntity> {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private static final int ENGRAVED = 0xFF303030;
    // The face is 12 of 16 pixels wide; the scale is 1.12's, shrunk for a long name.
    private static final float MAX_WIDTH = 0.7F;
    private static final float MAX_SCALE = 0.01F;
    private static final float FACE_Z = -0.5F + 2.0F / 16.0F + 0.005F;

    private final Font font;

    public GraveRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(GraveBlockEntity grave, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        List<FormattedCharSequence> lines = new ArrayList<>();
        List<Integer> widths = new ArrayList<>();
        this.addLine(lines, widths, Component.literal(grave.getOwnerName()));
        if (grave.getDeathTime() > 0) {
            Instant died = Instant.ofEpochMilli(grave.getDeathTime());
            this.addLine(lines, widths, Component.literal(DATE.format(died.atZone(ZoneId.systemDefault()))));
            this.addLine(lines, widths, Component.literal(TIME.format(died.atZone(ZoneId.systemDefault()))));
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-grave.getBlockState().getValue(GraveBlock.FACING).toYRot()));

        float top = 0.8F;
        for (int i = 0; i < lines.size(); i++) {
            int width = widths.get(i);
            float scale = width > 0 ? Math.min(MAX_SCALE, MAX_WIDTH / width) : MAX_SCALE;

            poseStack.pushPose();
            poseStack.translate(0.0F, top, FACE_Z);
            poseStack.scale(scale, -scale, scale);
            this.font.drawInBatch(lines.get(i), -width / 2.0F, 0.0F, ENGRAVED, false, poseStack.last().pose(), buffers, Font.DisplayMode.POLYGON_OFFSET, 0, packedLight);
            poseStack.popPose();

            top -= 10.0F * scale + 0.02F;
        }

        poseStack.popPose();
    }

    private void addLine(List<FormattedCharSequence> lines, List<Integer> widths, Component line) {
        lines.add(line.getVisualOrderText());
        widths.add(this.font.width(line));
    }
}
