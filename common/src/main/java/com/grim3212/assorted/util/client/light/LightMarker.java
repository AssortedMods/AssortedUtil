package com.grim3212.assorted.util.client.light;

import net.minecraft.util.FormattedCharSequence;

/** One number on the overlay: the block light at a spot a monster could stand on, in the colour of its danger. */
public record LightMarker(double x, double y, double z, FormattedCharSequence text, float offset, int color) {
}
