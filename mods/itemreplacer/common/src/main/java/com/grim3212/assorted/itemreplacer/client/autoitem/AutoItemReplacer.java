package com.grim3212.assorted.itemreplacer.client.autoitem;

import com.grim3212.assorted.itemreplacer.Constants;
import com.grim3212.assorted.itemreplacer.common.autoitem.Replacements;
import com.grim3212.assorted.lib.conditions.PartToggles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

/**
 * Refills the selected hotbar slot when it runs out or its tool breaks, with one inventory click a
 * player could make themselves, so it works on any server.
 */
public final class AutoItemReplacer {

    private static ItemStack lastStack = ItemStack.EMPTY;
    private static int lastSlot = -1;

    private AutoItemReplacer() {
    }

    public static void tick(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || player.isSpectator() || !PartToggles.isEnabled(Constants.MOD_ID)) {
            lastStack = ItemStack.EMPTY;
            lastSlot = -1;
            return;
        }

        Inventory inventory = player.getInventory();
        int slot = inventory.getSelectedSlot();
        ItemStack current = inventory.getItem(slot);

        if (slot == lastSlot && !lastStack.isEmpty() && (current.isEmpty() || Replacements.isLeftoverOf(lastStack, current)) && !emptiedOnPurpose(minecraft, player)) {
            int from = Replacements.find(inventory, lastStack, slot);
            if (from >= 0) {
                minecraft.gameMode.handleContainerInput(player.inventoryMenu.containerId, menuSlot(from), slot, ContainerInput.SWAP, player);
                current = inventory.getItem(slot);
            }
        }

        lastSlot = slot;
        if (!ItemStack.matches(lastStack, current)) {
            lastStack = current.copy();
        }
    }

    /** Dropped with Q, moved to the offhand, or moved around in a screen: the player wanted the slot empty. */
    private static boolean emptiedOnPurpose(Minecraft minecraft, LocalPlayer player) {
        return minecraft.gui.screen() != null || player.containerMenu != player.inventoryMenu
                || minecraft.options.keyDrop.isDown() || minecraft.options.keySwapOffhand.isDown();
    }

    /** Inventory slots 0 to 8 are the hotbar, which the inventory menu lists last. */
    private static int menuSlot(int inventorySlot) {
        return inventorySlot < Inventory.SELECTION_SIZE ? InventoryMenu.USE_ROW_SLOT_START + inventorySlot : inventorySlot;
    }
}
