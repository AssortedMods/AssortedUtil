package com.grim3212.assorted.graves.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

    private final List<GraveItem> items = new ArrayList<>();
    private int experience;
    private @Nullable UUID owner;
    private String ownerName = "";
    private long deathTime;

    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(GravesBlockEntityTypes.GRAVE.get(), pos, state);
    }

    public void fill(Player player, List<GraveItem> items, int experience, long deathTime) {
        this.owner = player.getUUID();
        this.ownerName = player.getGameProfile().getName();
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

    public List<GraveItem> getItems() {
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

    /** Spills what is left on the ground, called by the block as it is removed, while this still exists. */
    public void onBlockRemoved(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            for (GraveItem item : this.items) {
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (GraveItem item : this.items) {
            list.add(item.save(registries));
        }
        tag.put("Items", list);
        tag.putInt("Experience", this.experience);
        this.saveOwner(tag);
    }

    private void saveOwner(CompoundTag tag) {
        if (this.owner != null) {
            tag.putUUID("Owner", this.owner);
        }
        tag.putString("OwnerName", this.ownerName);
        tag.putLong("DeathTime", this.deathTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            GraveItem.load(list.getCompound(i), registries).ifPresent(this.items::add);
        }
        this.experience = tag.getInt("Experience");
        this.owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        this.ownerName = tag.getString("OwnerName");
        this.deathTime = tag.getLong("DeathTime");
    }

    /** The headstone only needs who and when; the items stay on the server. */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveOwner(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
