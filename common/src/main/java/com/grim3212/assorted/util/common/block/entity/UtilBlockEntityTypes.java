package com.grim3212.assorted.util.common.block.entity;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.util.Constants;
import com.grim3212.assorted.util.common.block.UtilBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class UtilBlockEntityTypes {

    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<BlockEntityType<GraveBlockEntity>> GRAVE = BLOCK_ENTITIES.register("grave", () -> Services.PLATFORM.createBlockEntityType(GraveBlockEntity::new, UtilBlocks.GRAVE.get()));

    public static void init() {
    }
}
