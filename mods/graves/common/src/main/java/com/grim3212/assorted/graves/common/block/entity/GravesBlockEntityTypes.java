package com.grim3212.assorted.graves.common.block.entity;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.graves.Constants;
import com.grim3212.assorted.graves.common.block.GravesBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GravesBlockEntityTypes {

    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<BlockEntityType<GraveBlockEntity>> GRAVE = BLOCK_ENTITIES.register("grave", () -> Services.PLATFORM.createBlockEntityType(GraveBlockEntity::new, GravesBlocks.GRAVE.get()));

    public static void init() {
    }
}
