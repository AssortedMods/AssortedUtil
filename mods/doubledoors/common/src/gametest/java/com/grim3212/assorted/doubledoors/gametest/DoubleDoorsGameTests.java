package com.grim3212.assorted.doubledoors.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Double Doors. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assorteddoubledoors/test_instance/<name>.json}.
 */
public final class DoubleDoorsGameTests {

    private DoubleDoorsGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        SmokeTests.register(out);
        DoubleDoorTests.register(out);
        FamilyTests.register(out);
    }
}
