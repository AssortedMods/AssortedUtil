package com.grim3212.assorted.doubledoors.mixin;

import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * A trapdoor's block set, which says whether a hand opens it. A door has a public getter for it; a
 * trapdoor's is protected.
 */
@Mixin(TrapDoorBlock.class)
public interface TrapDoorBlockAccessor {

    @Accessor("type")
    BlockSetType assorteddoubledoors_getType();
}
