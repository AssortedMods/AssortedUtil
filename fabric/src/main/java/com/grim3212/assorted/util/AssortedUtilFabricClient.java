package com.grim3212.assorted.util;

import com.grim3212.assorted.util.client.UtilClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedUtilFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UtilClient.init();
    }
}
