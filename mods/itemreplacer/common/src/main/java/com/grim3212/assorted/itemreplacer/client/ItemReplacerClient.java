package com.grim3212.assorted.itemreplacer.client;

import com.grim3212.assorted.itemreplacer.client.autoitem.AutoItemReplacer;
import com.grim3212.assorted.lib.platform.ClientServices;

/** Client-only startup, called by both loaders' client entry points. */
public class ItemReplacerClient {

    public static void init() {
        ClientServices.CLIENT.registerClientTickEnd(AutoItemReplacer::tick);
    }
}
