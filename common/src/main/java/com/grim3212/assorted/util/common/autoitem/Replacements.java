package com.grim3212.assorted.util.common.autoitem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Picks the stack that takes over a hotbar slot that ran out. Kept apart from the client tick so the
 * choice can be tested on a server.
 */
public final class Replacements {

    private Replacements() {
    }

    /**
     * The inventory slot holding the best replacement for {@code used}, or -1. A stack that is not
     * damageable must match exactly, so a spent Potion of Healing is never refilled with Poison.
     */
    public static int find(Inventory inventory, ItemStack used, int selected) {
        int best = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
            ItemStack candidate = inventory.getItem(slot);
            if (slot == selected || candidate.isEmpty() || !ItemStack.isSameItem(candidate, used)) {
                continue;
            }

            int score;
            if (used.isDamageableItem()) {
                // Same enchantments and name first, then the most worn, to use tools up in order as 1.12 meant to.
                score = (sameIgnoringDamage(candidate, used) ? 1 << 20 : 0) + candidate.getDamageValue();
            } else if (ItemStack.isSameItemSameComponents(candidate, used)) {
                score = candidate.getCount();
            } else {
                continue;
            }

            // Leave the rest of the hotbar as the player laid it out.
            if (slot >= Inventory.SELECTION_SIZE) {
                score += 1 << 24;
            }

            if (score > bestScore) {
                best = slot;
                bestScore = score;
            }
        }

        return best;
    }

    private static boolean sameIgnoringDamage(ItemStack a, ItemStack b) {
        ItemStack first = a.copyWithCount(1);
        ItemStack second = b.copyWithCount(1);
        first.remove(DataComponents.DAMAGE);
        second.remove(DataComponents.DAMAGE);
        return ItemStack.isSameItemSameComponents(first, second);
    }

    /** Whether {@code now} is what using up the last of {@code used} leaves behind: the bowl, bottle or bucket. */
    public static boolean isLeftoverOf(ItemStack used, ItemStack now) {
        var remainder = used.get(DataComponents.USE_REMAINDER);
        return used.getCount() == 1 && remainder != null && !now.isEmpty() && ItemStack.isSameItem(remainder.convertInto().create(), now);
    }
}
