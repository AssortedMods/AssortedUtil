package com.grim3212.assorted.graves.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * What a player had when they died, each stack with the inventory slot it came out of so it can go
 * back there, and their experience in points.
 */
public class GraveBlockEntity extends BlockEntity {

    private final List<ItemStackWithSlot> items = new ArrayList<>();
    private int experience;
    private @Nullable UUID owner;
    private String ownerName = "";
    private long deathTime;

    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(GravesBlockEntityTypes.GRAVE.get(), pos, state);
    }

    public void fill(Player player, List<ItemStackWithSlot> items, int experience, long deathTime) {
        this.owner = player.getUUID();
        this.ownerName = player.getGameProfile().name();
        this.items.clear();
        this.items.addAll(items);
        this.experience = experience;
        this.deathTime = deathTime;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    /** Empties the grave, so removing it afterwards drops nothing. */
    public void clearContents() {
        this.items.clear();
        this.experience = 0;
        this.setChanged();
    }

    public List<ItemStackWithSlot> getItems() {
        return List.copyOf(this.items);
    }

    public int getExperience() {
        return this.experience;
    }

    public boolean isOwner(Player player) {
        return this.owner == null || this.owner.equals(player.getUUID());
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public long getDeathTime() {
        return this.deathTime;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level instanceof ServerLevel serverLevel) {
            for (ItemStackWithSlot item : this.items) {
                Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item.stack());
            }
            if (this.experience > 0) {
                ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), this.experience);
            }
            this.items.clear();
            this.experience = 0;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ValueOutput.TypedOutputList<ItemStackWithSlot> list = output.list("Items", ItemStackWithSlot.CODEC);
        this.items.forEach(list::add);
        output.putInt("Experience", this.experience);
        this.saveOwner(output);
    }

    private void saveOwner(ValueOutput output) {
        output.storeNullable("Owner", UUIDUtil.CODEC, this.owner);
        output.putString("OwnerName", this.ownerName);
        output.putLong("DeathTime", this.deathTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items.clear();
        input.listOrEmpty("Items", ItemStackWithSlot.CODEC).forEach(this.items::add);
        this.experience = input.getIntOr("Experience", 0);
        this.owner = input.read("Owner", UUIDUtil.CODEC).orElse(null);
        this.ownerName = input.getStringOr("OwnerName", "");
        this.deathTime = input.getLongOr("DeathTime", 0L);
    }

    /** The headstone only needs who and when; the items stay on the server. */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.storeNullable("Owner", UUIDUtil.CODEC, this.owner);
        tag.putString("OwnerName", this.ownerName);
        tag.putLong("DeathTime", this.deathTime);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
