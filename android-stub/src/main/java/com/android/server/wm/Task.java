package com.android.server.wm;

import android.graphics.Rect;

import androidx.annotation.Nullable;

public class Task {
    public void setWindowingMode(int windowingMode) { }

    @Nullable
    public Task getRootTask() { return null; }

    public void getBounds(Rect outBounds) { }

    public int setBounds(Rect bounds) { return 0; }

    public void setAlwaysOnTop(boolean alwaysOnTop) { }
}
