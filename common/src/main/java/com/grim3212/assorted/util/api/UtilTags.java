package com.grim3212.assorted.util.api;

import com.grim3212.assorted.util.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class UtilTags {

    public static class Blocks {
        /** Doors, trapdoors and fence gates that always open on their own, for modded ones with rules of their own. */
        public static final TagKey<Block> IGNORED_BY_DOUBLE_DOORS = create("ignored_by_double_doors");

        private static TagKey<Block> create(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
