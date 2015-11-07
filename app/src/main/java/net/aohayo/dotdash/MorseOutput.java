package net.aohayo.dotdash;

import android.content.Context;

public abstract class MorseOutput {
    public void init() {

    }

    public void finish() {

    }

    public abstract void start();
    public abstract void start(int duration);
    public abstract void stop();
}
