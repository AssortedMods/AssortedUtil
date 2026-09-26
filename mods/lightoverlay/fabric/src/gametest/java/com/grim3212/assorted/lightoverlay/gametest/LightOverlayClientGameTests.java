package com.grim3212.assorted.lightoverlay.gametest;

import com.grim3212.assorted.lightoverlay.client.light.LightOverlay;
import com.grim3212.assorted.lightoverlay.common.light.SpawnLight;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;

/** What a server cannot see: the overlay itself, by day and by night. {@code ./gradlew :lightoverlay:fabric:runClientGameTest} screenshots both. */
public class LightOverlayClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();
            world.getServer().runOnServer(server -> {
                ServerLevel level = server.overworld();
                level.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set noon");
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), 180.0F, 15.0F);
                level.setBlockAndUpdate(player.blockPosition().north(3).west(3), Blocks.TORCH.defaultBlockState());
            });

            context.runOnClient(client -> {
                LightOverlay.toggle(client);
                if (LightOverlay.scan(client.level, client.player.blockPosition(), 8, true).isEmpty()) {
                    throw new AssertionError("the light overlay found nowhere to mark on a grass plain");
                }
            });
            context.waitTicks(15);
            // Out of the torch's reach, under open sky.
            context.runOnClient(client -> expectLight(client.level, client.player.blockPosition().south(20), SpawnLight.WHEN_DARK, "at noon"));
            context.takeScreenshot("assortedlightoverlay_day");

            world.getServer().runOnServer(server -> server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set midnight"));
            context.waitTicks(15);
            context.runOnClient(client -> expectLight(client.level, client.player.blockPosition().south(20), SpawnLight.WHEN_DARK, "at midnight"));
            context.takeScreenshot("assortedlightoverlay_night");
        }
    }

    private static void expectLight(Level level, BlockPos pos, SpawnLight expected, String when) {
        SpawnLight found = SpawnLight.at(level, pos);
        if (found != expected) {
            throw new AssertionError("open grass " + when + " reads " + found + ", not " + expected);
        }
    }
}
