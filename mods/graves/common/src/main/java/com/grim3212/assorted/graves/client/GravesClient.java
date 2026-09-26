package com.grim3212.assorted.graves.client;

import com.grim3212.assorted.graves.client.render.GraveRenderer;
import com.grim3212.assorted.graves.common.block.entity.GravesBlockEntityTypes;
import com.grim3212.assorted.lib.platform.ClientServices;

/** Client-only startup, called by both loaders' client entry points. */
public class GravesClient {

    public static void init() {
        ClientServices.CLIENT.registerBlockEntityRenderer(() -> GravesBlockEntityTypes.GRAVE.get(), GraveRenderer::new);
    }
}
