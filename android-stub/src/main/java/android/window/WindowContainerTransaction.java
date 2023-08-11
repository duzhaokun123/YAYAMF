package android.window;

import android.graphics.Rect;

import androidx.annotation.NonNull;

public class WindowContainerTransaction {
    @NonNull
    public WindowContainerTransaction setBounds(
            @NonNull WindowContainerToken container, @NonNull Rect bounds) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public WindowContainerTransaction addRectInsetsProvider(
            @NonNull WindowContainerToken receiverWindowContainer,
            @NonNull Rect insetsProviderFrame,
            int[] insetsTypes) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public WindowContainerTransaction setAlwaysOnTop(
            @NonNull WindowContainerToken windowContainer,
            boolean alwaysOnTop) {
        throw new RuntimeException("Stub!");
    }
}
