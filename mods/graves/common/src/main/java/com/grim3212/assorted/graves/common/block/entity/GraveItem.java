package com.grim3212.assorted.graves.common.block.entity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * A stack in a grave and the inventory slot it came out of, saved the way a container saves its
 * items: the stack's own tag with a {@code Slot} byte beside it.
 */
public record GraveItem(int slot, ItemStack stack) {

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putByte("Slot", (byte) this.slot);
        return (CompoundTag) this.stack.save(registries, tag);
    }

    public static Optional<GraveItem> load(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains("Slot", Tag.TAG_ANY_NUMERIC)) {
            return Optional.empty();
        }

        return ItemStack.parse(registries, tag).map(stack -> new GraveItem(tag.getByte("Slot") & 255, stack));
    }
}
