package com.grim3212.assorted.util.client.time;

/** What the time panel shows, in the order the time key steps through them. */
public enum TimeDisplay {
    HIDDEN(false, false),
    GAME(true, false),
    REAL(false, true),
    BOTH(true, true);

    private final boolean game;
    private final boolean real;

    TimeDisplay(boolean game, boolean real) {
        this.game = game;
        this.real = real;
    }

    public boolean showsGame() {
        return this.game;
    }

    public boolean showsReal() {
        return this.real;
    }

    public TimeDisplay next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
