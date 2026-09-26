package com.grim3212.assorted.lightoverlay.client.light;

import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.jetbrains.annotations.Nullable;

/**
 * The chunk sections vanilla's cave culling says the camera could see, in any direction. A renderer mod
 * that replaces the section graph leaves it empty, and then every section counts as visible.
 */
public final class VisibleSections {

    private final @Nullable ViewArea area;
    private final @Nullable SectionOcclusionGraph graph;
    private final Long2BooleanOpenHashMap known = new Long2BooleanOpenHashMap();

    private VisibleSections(@Nullable ViewArea area, @Nullable SectionOcclusionGraph graph) {
        this.area = area;
        this.graph = graph;
    }

    public static VisibleSections capture(Minecraft minecraft) {
        ViewArea area = minecraft.levelRenderer.viewArea();
        SectionOcclusionGraph graph = minecraft.levelRenderer.sectionOcclusionGraph();
        if (area == null || minecraft.player == null) {
            return new VisibleSections(null, null);
        }

        // The camera's own section is always in a working graph, so without it the graph is not vanilla's.
        SectionRenderDispatcher.RenderSection own = area.getRenderSectionAt(minecraft.gameRenderer.mainCamera().blockPosition());
        return own != null && graph.getNode(own) != null ? new VisibleSections(area, graph) : new VisibleSections(null, null);
    }

    public boolean isVisible(BlockPos pos) {
        if (this.area == null || this.graph == null) {
            return true;
        }

        long section = SectionPos.asLong(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getY()), SectionPos.blockToSectionCoord(pos.getZ()));
        if (this.known.containsKey(section)) {
            return this.known.get(section);
        }

        SectionRenderDispatcher.RenderSection render = this.area.getRenderSectionAt(pos);
        boolean visible = render != null && this.graph.getNode(render) != null;
        this.known.put(section, visible);
        return visible;
    }
}
