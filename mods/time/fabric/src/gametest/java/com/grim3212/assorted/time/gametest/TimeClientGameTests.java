package com.grim3212.assorted.time.gametest;

import com.grim3212.assorted.time.client.time.TimeDisplay;
import com.grim3212.assorted.time.client.time.TimeHud;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;

/** What a server cannot see: the clock. {@code ./gradlew :time:fabric:runClientGameTest} screenshots it. */
public class TimeClientGameTests implements FabricClientGameTest {

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
            });

            context.runOnClient(client -> {
                TimeHud.cycle();
                TimeHud.cycle();
                TimeHud.cycle();
                if (TimeHud.display() != TimeDisplay.BOTH) {
                    throw new AssertionError("three presses of the time key show " + TimeHud.display() + ", not both times");
                }
            });
            context.waitTicks(10);
            context.takeScreenshot("assortedtime_time");
        }
    }
}
