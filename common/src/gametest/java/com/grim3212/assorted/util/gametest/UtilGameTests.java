package com.grim3212.assorted.util.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Util. The tests live in small {@code <Feature>Tests}
 * classes; this only lists them. Each name here needs a matching
 * {@code data/assortedutil/test_instance/<name>.json}.
 */
public final class UtilGameTests {

    private UtilGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        SmokeTests.register(out);
        GraveTests.register(out);
        DoubleDoorTests.register(out);
        ReplacementTests.register(out);
        OverlayTests.register(out);
    }
}
