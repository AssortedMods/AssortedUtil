package com.grim3212.assorted.graves.gametest;

import com.grim3212.assorted.graves.common.block.GraveBlock;
import com.grim3212.assorted.graves.common.block.GravesBlocks;
import com.grim3212.assorted.graves.common.block.entity.GraveBlockEntity;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.List;

/** What a server cannot see: the headstone. {@code ./gradlew :graves:fabric:runClientGameTest} screenshots it. */
public class GravesClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();

            BlockPos grave = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                level.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set noon");
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), 180.0F, 15.0F);

                BlockPos pos = player.blockPosition().north(3);
                level.setBlockAndUpdate(pos, GravesBlocks.GRAVE.get().defaultBlockState().setValue(GraveBlock.FACING, Direction.SOUTH));
                ((GraveBlockEntity) level.getBlockEntity(pos)).fill(player, List.of(new ItemStackWithSlot(0, new ItemStack(Items.STONE))), 0, System.currentTimeMillis());
                return pos;
            });

            context.waitFor(client -> client.level.getBlockEntity(grave) instanceof GraveBlockEntity entity && !entity.getOwnerName().isEmpty());
            context.waitTicks(10);
            context.takeScreenshot("assortedgraves_grave");
        }
    }
}
