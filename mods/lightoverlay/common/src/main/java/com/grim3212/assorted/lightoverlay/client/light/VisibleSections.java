package com.grim3212.assorted.lightoverlay.client.light;

import com.grim3212.assorted.lightoverlay.mixin.client.LevelRendererAccessor;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * The chunk sections vanilla's cave culling says the camera could see, in any direction. A renderer mod
 * that replaces the section graph leaves it empty, and then every section counts as visible.
 */
public final class VisibleSections {

    /** Every section visible, for when there is no vanilla graph to ask. */
    public static final VisibleSections EVERYWHERE = new VisibleSections(null);

    // 1.21.1 keeps the graph's nodes to itself; asking for the ones inside a frustum that takes in
    // everything hands back every section the graph reached.
    private static final Frustum ALL_AROUND = new Frustum(new Matrix4f(), new Matrix4f()) {
        @Override
        public boolean isVisible(AABB box) {
            return true;
        }
    };

    private final @Nullable LongOpenHashSet sections;

    private VisibleSections(@Nullable LongOpenHashSet sections) {
        this.sections = sections;
    }

    public static VisibleSections capture(Minecraft minecraft) {
        LevelRendererAccessor renderer = (LevelRendererAccessor) minecraft.levelRenderer;
        // The graph has nothing in it until the level renderer has a view area.
        if (renderer.assortedlightoverlay$viewArea() == null || minecraft.player == null) {
            return EVERYWHERE;
        }

        SectionOcclusionGraph graph = renderer.assortedlightoverlay$sectionOcclusionGraph();
        List<SectionRenderDispatcher.RenderSection> reached = new ArrayList<>();
        graph.addSectionsInFrustum(ALL_AROUND, reached);
        LongOpenHashSet sections = new LongOpenHashSet(reached.size());
        for (SectionRenderDispatcher.RenderSection section : reached) {
            sections.add(SectionPos.asLong(section.getOrigin()));
        }

        // The camera's own section is always in a working graph, so without it the graph is not vanilla's.
        BlockPos camera = minecraft.gameRenderer.getMainCamera().getBlockPosition();
        return sections.contains(SectionPos.asLong(camera)) ? new VisibleSections(sections) : EVERYWHERE;
    }

    public boolean isVisible(BlockPos pos) {
        return this.sections == null || this.sections.contains(SectionPos.asLong(pos));
    }
}
