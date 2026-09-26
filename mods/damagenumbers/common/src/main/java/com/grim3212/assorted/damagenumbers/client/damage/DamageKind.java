package com.grim3212.assorted.damagenumbers.client.damage;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/** RPG Damage's sources and colours, grouped by damage type tag so modded damage types fall into place. */
public enum DamageKind {
    MELEE(0xFE2712),
    PROJECTILE(0xFE2712),
    EXPLOSION(0xFE2712),
    FIRE(0xFF7F00),
    MAGIC(0xA020F0),
    FREEZING(0x7FD4FF),
    LIGHTNING(0xFFEE55),
    DROWNING(0x5599FF),
    FALL(0xBEBEBE),
    STARVATION(0xBEBEBE),
    CACTUS(0xBEBEBE),
    GENERIC(0xBEBEBE),
    HEALING(0x00A550);

    private final int color;

    DamageKind(int color) {
        this.color = color;
    }

    public int color() {
        return this.color;
    }

    public Component label() {
        return Component.translatable("hud.assorteddamagenumbers.damage." + this.name().toLowerCase(Locale.ROOT));
    }

    public static DamageKind of(@Nullable DamageSource source) {
        if (source == null) {
            return GENERIC;
        }
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return FIRE;
        }
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            return EXPLOSION;
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            return PROJECTILE;
        }
        if (source.is(DamageTypeTags.IS_DROWNING)) {
            return DROWNING;
        }
        if (source.is(DamageTypeTags.IS_FREEZING)) {
            return FREEZING;
        }
        if (source.is(DamageTypeTags.IS_LIGHTNING)) {
            return LIGHTNING;
        }
        if (source.is(DamageTypeTags.IS_FALL)) {
            return FALL;
        }
        if (source.is(DamageTypes.STARVE)) {
            return STARVATION;
        }
        if (source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH)) {
            return CACTUS;
        }
        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC) || source.is(DamageTypes.WITHER) || source.is(DamageTypes.DRAGON_BREATH)) {
            return MAGIC;
        }
        return source.getEntity() != null ? MELEE : GENERIC;
    }
}
