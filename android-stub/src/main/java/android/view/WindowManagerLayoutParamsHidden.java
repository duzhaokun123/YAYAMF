package android.view;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(WindowManager.LayoutParams.class)
public class WindowManagerLayoutParamsHidden {
    public void setTrustedOverlay() {
        throw new RuntimeException("Stub!");
    }
}
