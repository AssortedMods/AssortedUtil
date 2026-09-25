package com.grim3212.assorted.util.gametest;

import com.grim3212.assorted.util.client.damage.DamageKind;
import com.grim3212.assorted.util.client.damage.DamageNumbers;
import com.grim3212.assorted.util.client.light.LightOverlay;
import com.grim3212.assorted.util.client.time.TimeDisplay;
import com.grim3212.assorted.util.client.time.TimeHud;
import com.grim3212.assorted.util.common.block.GraveBlock;
import com.grim3212.assorted.util.common.block.UtilBlocks;
import com.grim3212.assorted.util.common.block.entity.GraveBlockEntity;
import com.grim3212.assorted.util.common.light.SpawnLight;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;

/**
 * What a server cannot see: the headstone, the clock, a damage number off a real hit and the light
 * overlay. {@code ./gradlew :fabric:runClientGameTest} screenshots each.
 */
public class UtilClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(UtilClientGameTests::manualTextFitsUnderPictures);
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();

            record Scene(BlockPos grave, int husk) {
            }

            Scene scene = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                level.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set noon");
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), 180.0F, 15.0F);
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));

                BlockPos grave = player.blockPosition().north(3);
                level.setBlockAndUpdate(grave, UtilBlocks.GRAVE.get().defaultBlockState().setValue(GraveBlock.FACING, Direction.SOUTH));
                ((GraveBlockEntity) level.getBlockEntity(grave)).fill(player, List.of(new ItemStackWithSlot(0, new ItemStack(Items.STONE))), 0, System.currentTimeMillis());
                level.setBlockAndUpdate(grave.west(3), Blocks.TORCH.defaultBlockState());
                BlockPos door = player.blockPosition().north(2).east(2);
                level.setBlockAndUpdate(door, Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
                level.setBlockAndUpdate(door.above(), Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
                level.setBlockAndUpdate(door.east(), Blocks.OAK_FENCE_GATE.defaultBlockState());

                // A husk, not a zombie: at noon a zombie would burn, and its fire numbers would muddle the sword's.
                Husk husk = EntityTypes.HUSK.create(level, EntitySpawnReason.COMMAND);
                husk.snapTo(player.getX() + 2.5D, player.getY(), player.getZ() - 6.0D, 0.0F, 0.0F);
                husk.setNoAi(true);
                level.addFreshEntity(husk);
                return new Scene(grave, husk.getId());
            });

            context.waitFor(client -> client.level.getEntity(scene.husk()) != null
                    && client.level.getBlockEntity(scene.grave()) instanceof GraveBlockEntity grave && !grave.getOwnerName().isEmpty());
            context.runOnClient(client -> {
                TimeHud.cycle();
                TimeHud.cycle();
                TimeHud.cycle();
                if (TimeHud.display() != TimeDisplay.BOTH) {
                    throw new AssertionError("three presses of the time key show " + TimeHud.display() + ", not both times");
                }
            });
            context.waitTicks(10);
            context.takeScreenshot("assortedutil_grave_and_time");

            world.getServer().runOnServer(server -> {
                ServerLevel level = server.overworld();
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                ((LivingEntity) level.getEntity(scene.husk())).hurtServer(level, level.damageSources().playerAttack(player), 5.0F);
            });
            context.waitFor(client -> !DamageNumbers.popups().isEmpty());
            context.runOnClient(client -> {
                int color = DamageNumbers.popups().getFirst().color();
                if (color != DamageKind.MELEE.color()) {
                    throw new AssertionError("a sword hit came up in colour " + Integer.toHexString(color) + ", not melee's");
                }
            });
            context.waitTicks(3);
            context.takeScreenshot("assortedutil_damage_number");

            context.runOnClient(client -> {
                TimeHud.cycle();
                LightOverlay.toggle(client);
                if (LightOverlay.scan(client.level, client.player.blockPosition(), 8, true).isEmpty()) {
                    throw new AssertionError("the light overlay found nowhere to mark on a grass plain");
                }
            });
            context.waitTicks(15);
            // Out of the torch's reach, under open sky.
            context.runOnClient(client -> expectLight(client.level, client.player.blockPosition().south(20), SpawnLight.WHEN_DARK, "at noon"));
            context.takeScreenshot("assortedutil_light_overlay_day");

            world.getServer().runOnServer(server -> server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set midnight"));
            context.waitTicks(15);
            context.runOnClient(client -> expectLight(client.level, client.player.blockPosition().south(20), SpawnLight.WHEN_DARK, "at midnight"));
            context.takeScreenshot("assortedutil_light_overlay_night");
        }
    }

    /** An image page leaves 84 of its 176 pixels for text, 9 lines at the manual's 152 wide; past that the reader has to scroll. */
    private static void manualTextFitsUnderPictures(Minecraft client) {
        List<String> tooLong = new ArrayList<>();
        for (String page : List.of("graves.info", "graves.restore", "double_doors.info", "auto_item_replacer.info", "time.info", "light_overlay.info", "damage_numbers.info")) {
            int lines = client.font.split(Component.translatable("manual.assortedutil.chapter." + page), 152).size();
            if (lines > 9) {
                tooLong.add(page + " (" + lines + " lines)");
            }
        }
        if (!tooLong.isEmpty()) {
            throw new AssertionError("manual pages run past the 9 lines under their picture: " + tooLong);
        }
    }

    private static void expectLight(Level level, BlockPos pos, SpawnLight expected, String when) {
        SpawnLight found = SpawnLight.at(level, pos);
        if (found != expected) {
            throw new AssertionError("open grass " + when + " reads " + found + ", not " + expected);
        }
    }
}
