package com.grim3212.assorted.graves.common.grave;

import com.grim3212.assorted.lib.events.PlayerDeathDropsEvent;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.graves.Constants;
import com.grim3212.assorted.graves.GravesCommonMod;
import com.grim3212.assorted.graves.common.block.GraveBlock;
import com.grim3212.assorted.graves.common.block.GravesBlocks;
import com.grim3212.assorted.graves.common.block.entity.GraveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class Graves {

    private Graves() {
    }

    public static void init() {
        Services.EVENTS.registerEvent(PlayerDeathDropsEvent.class, (final PlayerDeathDropsEvent event) -> dig(event.getPlayer()));
    }

    /** Moves what the player would drop into a new grave near them, and returns where, or null if nothing was moved. */
    public static @Nullable BlockPos dig(ServerPlayer player) {
        ServerLevel level = player.level();
        if (player.isSpectator() || level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            return null;
        }

        Inventory inventory = player.getInventory();
        List<ItemStackWithSlot> items = new ArrayList<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            // Curse of Vanishing: vanilla destroys these right after, and a grave must not save them.
            if (!stack.isEmpty() && !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                items.add(new ItemStackWithSlot(slot, stack));
            }
        }

        int experience = GravesCommonMod.COMMON_CONFIG.gravesKeepAllExperience.get() ? totalExperience(player) : 0;
        if (items.isEmpty() && experience <= 0) {
            return null;
        }

        BlockPos pos = findSpot(level, player.blockPosition(), GravesCommonMod.COMMON_CONFIG.gravesSearchRadius.get());
        if (pos == null) {
            Constants.LOG.info("No room for {}'s grave near {}, their items drop instead", player.getPlainTextName(), player.blockPosition());
            player.sendSystemMessage(Component.translatable("message.assortedgraves.grave.no_room"));
            return null;
        }

        BlockState grave = GravesBlocks.GRAVE.get().defaultBlockState()
                .setValue(GraveBlock.FACING, player.getDirection().getOpposite())
                .setValue(GraveBlock.WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        if (!level.setBlock(pos, grave, Block.UPDATE_ALL) || !(level.getBlockEntity(pos) instanceof GraveBlockEntity graveEntity)) {
            return null;
        }

        graveEntity.fill(player, items, experience, System.currentTimeMillis());
        for (ItemStackWithSlot item : items) {
            inventory.setItem(item.slot(), ItemStack.EMPTY);
        }
        if (experience > 0) {
            // Nothing left for vanilla to drop as orbs.
            player.experienceLevel = 0;
            player.experienceProgress = 0.0F;
            player.totalExperience = 0;
        }

        player.sendSystemMessage(Component.translatable("message.assortedgraves.grave.dug", pos.getX(), pos.getY(), pos.getZ()));
        return pos;
    }

    /** The nearest block the grave can replace, within the world; the death spot itself first. */
    public static @Nullable BlockPos findSpot(ServerLevel level, BlockPos origin, int radius) {
        // Died in the void or above the build limit: the nearest height a block can go.
        BlockPos start = new BlockPos(origin.getX(), Mth.clamp(origin.getY(), level.getMinY(), level.getMaxY()), origin.getZ());
        for (BlockPos pos : BlockPos.withinManhattan(start, radius, radius, radius)) {
            if (canHold(level, pos)) {
                return pos.immutable();
            }
        }

        return null;
    }

    private static boolean canHold(ServerLevel level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos) || !level.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        return (state.isAir() || state.canBeReplaced()) && !state.hasBlockEntity();
    }

    /** Gives back what the grave holds and removes it. The owner's things go back into the slots they came from. */
    public static void restore(ServerPlayer player, GraveBlockEntity grave) {
        List<ItemStackWithSlot> items = grave.getItems();
        int experience = grave.getExperience();
        boolean owner = grave.isOwner(player);
        grave.clearContents();

        Inventory inventory = player.getInventory();
        List<ItemStack> leftovers = new ArrayList<>();
        for (ItemStackWithSlot item : items) {
            // Someone else's armour goes into the pack, not onto them: it might carry Curse of Binding.
            boolean sameSlot = (owner || item.slot() < Inventory.INVENTORY_SIZE) && item.isValidInContainer(inventory.getContainerSize());
            if (sameSlot && inventory.getItem(item.slot()).isEmpty()) {
                inventory.setItem(item.slot(), item.stack());
            } else {
                leftovers.add(item.stack());
            }
        }
        for (ItemStack stack : leftovers) {
            if (!inventory.add(stack) && !stack.isEmpty()) {
                player.drop(stack, false);
            }
        }

        if (experience > 0) {
            player.giveExperiencePoints(experience);
        }

        ServerLevel level = player.level();
        BlockPos pos = grave.getBlockPos();
        level.removeBlock(pos, false);
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.4F, 1.0F);
    }

    /** Every point the player has, where vanilla's own drop on death is at most 100 of them. */
    public static int totalExperience(Player player) {
        double total = pointsToReach(player.experienceLevel) + (double) player.experienceProgress * player.getXpNeededForNextLevel();
        return (int) Math.min(Math.round(total), Integer.MAX_VALUE);
    }

    /** The sum of Player#getXpNeededForNextLevel below {@code level}, closed form so a level set by command cannot stall a death. */
    public static double pointsToReach(int level) {
        double l = Math.max(level, 0);
        if (l <= 16) {
            return l * l + 6 * l;
        }
        return l <= 31 ? 2.5 * l * l - 40.5 * l + 360 : 4.5 * l * l - 162.5 * l + 2220;
    }
}
