package com.grim3212.assorted.util.common.door;

import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.util.UtilCommonMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Clicks a door's partners as the player clicks it, so an iron partner still wants redstone. Runs on
 * both sides, as the client predicts a door opening.
 */
public final class DoubleDoors {

    private DoubleDoors() {
    }

    public static void init() {
        Services.EVENTS.registerEvent(UseBlockEvent.class, (final UseBlockEvent event) -> {
            if (event.getHand() == InteractionHand.MAIN_HAND && !event.isCanceled()) {
                openPartners(event.getLevel(), event.getPlayer(), event.getHitResult());
            }
        });
    }

    /** Sneaking opens just the one, the same as sneaking skips a block's use in vanilla. */
    public static void openPartners(Level level, Player player, BlockHitResult hit) {
        if (!UtilCommonMod.COMMON_CONFIG.doubleDoorsEnabled.get() || player.isSpectator() || player.isSecondaryUseActive()) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        for (BlockPos partner : DoorPartners.find(level, pos, level.getBlockState(pos))) {
            if (level.mayInteract(player, partner)) {
                level.getBlockState(partner).useWithoutItem(level, player, hit.withPosition(partner));
            }
        }
    }
}
