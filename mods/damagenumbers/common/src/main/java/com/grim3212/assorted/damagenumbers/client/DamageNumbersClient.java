package com.grim3212.assorted.damagenumbers.client;

import com.grim3212.assorted.damagenumbers.client.damage.DamageNumbers;
import com.grim3212.assorted.damagenumbers.config.DamageNumbersClientConfig;
import com.grim3212.assorted.lib.platform.ClientServices;

/** Client-only startup, called by both loaders' client entry points. */
public class DamageNumbersClient {

    public static final DamageNumbersClientConfig CONFIG = new DamageNumbersClientConfig();

    public static void init() {
        ClientServices.CLIENT.registerClientTickEnd(DamageNumbers::tick);
        ClientServices.CLIENT.registerLevelSubmit(DamageNumbers::submit);
    }
}
