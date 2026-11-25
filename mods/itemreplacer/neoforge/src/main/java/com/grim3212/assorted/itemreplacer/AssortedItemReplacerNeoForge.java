package com.grim3212.assorted.itemreplacer;

import com.grim3212.assorted.itemreplacer.client.data.ItemReplacerLanguageProvider;
import com.grim3212.assorted.itemreplacer.client.data.ItemReplacerManualProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(Constants.MOD_ID)
public class AssortedItemReplacerNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedItemReplacerNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);
    }

    /** Client datagen only: the language file and the manual, written into common for both loaders. */
    private void gatherData(final GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.getGenerator().addProvider(event.includeClient(), new ItemReplacerLanguageProvider(packOutput));
        event.getGenerator().addProvider(event.includeClient(), new ItemReplacerManualProvider(packOutput));
    }
}
