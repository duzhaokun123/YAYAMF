package com.android.wm.shell.common;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.view.Display;

import java.util.Set;

public class DisplayController {
    public Display getDisplay(int displayId) {
        return null;
    }

    public void removeDisplayWindowListener(OnDisplaysChangedListener listener) {
        return;
    }

    public interface OnDisplaysChangedListener {
        /**
         * Called when a display has been added to the WM hierarchy.
         */
        default void onDisplayAdded(int displayId) {}

        /**
         * Called when a display's window-container configuration changes.
         */
        default void onDisplayConfigurationChanged(int displayId, Configuration newConfig) {}

        /**
         * Called when a display is removed.
         */
        default void onDisplayRemoved(int displayId) {}

        /**
         * Called when fixed rotation on a display is started.
         */
        default void onFixedRotationStarted(int displayId, int newRotation) {}

        /**
         * Called when fixed rotation on a display is finished.
         */
        default void onFixedRotationFinished(int displayId) {}

        /**
         * Called when keep-clear areas on a display have changed.
         */
        default void onKeepClearAreasChanged(int displayId, Set<Rect> restricted,
                Set<Rect> unrestricted) {}
    }
}
