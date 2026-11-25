package com.grim3212.assorted.lightoverlay.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.culling.Frustum;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** What the light overlay reads of the level renderer, which 1.21.1 keeps private. */
@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {

    @Accessor("viewArea")
    @Nullable ViewArea assortedlightoverlay$viewArea();

    @Accessor("sectionOcclusionGraph")
    SectionOcclusionGraph assortedlightoverlay$sectionOcclusionGraph();

    @Accessor("cullingFrustum")
    @Nullable Frustum assortedlightoverlay$cullingFrustum();
}
