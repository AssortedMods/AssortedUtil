package com.grim3212.assorted.graves;

import com.grim3212.assorted.graves.client.data.GravesBlockstateProvider;
import com.grim3212.assorted.graves.client.data.GravesLanguageProvider;
import com.grim3212.assorted.graves.client.data.GravesManualProvider;
import com.grim3212.assorted.graves.data.GravesBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedGravesNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedGravesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        GravesCommonMod.init();
    }

    /** One event for both halves; the data run's --server and --client flags pick which providers run. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new GravesBlockTagProvider(packOutput, lookupProvider)));

        // Client datagen: models, the language file and the manual, written into common for both loaders.
        generator.addProvider(event.includeClient(), new GravesBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new GravesLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new GravesManualProvider(packOutput));
    }
}
