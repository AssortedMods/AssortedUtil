package com.grim3212.assorted.itemreplacer.client;

import com.grim3212.assorted.itemreplacer.Constants;
import com.grim3212.assorted.itemreplacer.client.autoitem.AutoItemReplacer;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.resources.Identifier;

/** Client-only startup, called by both loaders' client entry points. */
public class ItemReplacerClient {

    public static void init() {
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.withDefaultNamespace("clock"), 10)
                .manualOrder(160);

        ClientServices.CLIENT.registerClientTickEnd(AutoItemReplacer::tick);
    }
}
