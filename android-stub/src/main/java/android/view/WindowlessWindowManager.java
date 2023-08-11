package android.view;

import android.content.res.Configuration;
import android.os.IBinder;

public class WindowlessWindowManager {
    public WindowlessWindowManager(Configuration c, SurfaceControl rootSurface,
                                   IBinder hostInputToken) {
        throw new RuntimeException("Stub!");
    }
}
