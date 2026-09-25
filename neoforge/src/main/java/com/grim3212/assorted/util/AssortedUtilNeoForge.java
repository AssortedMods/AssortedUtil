package com.grim3212.assorted.util;

import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.util.client.data.UtilBlockstateProvider;
import com.grim3212.assorted.util.client.data.UtilLanguageProvider;
import com.grim3212.assorted.util.client.data.UtilManualProvider;
import com.grim3212.assorted.util.data.UtilBlockTagProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(Constants.MOD_ID)
public class AssortedUtilNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedUtilNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        UtilCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new ForgeBlockTagProvider(packOutput, event.getLookupProvider(), Constants.MOD_ID, new UtilBlockTagProvider(packOutput, event.getLookupProvider())));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new UtilBlockstateProvider(packOutput));
        event.addProvider(new UtilLanguageProvider(packOutput));
        event.addProvider(new UtilManualProvider(packOutput));
    }
}
