package android.view;


import android.content.Context;

import androidx.annotation.NonNull;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(SurfaceControlViewHost.class)
public class SurfaceControlViewHostHidden {
    public SurfaceControlViewHostHidden(@NonNull Context c, @NonNull Display d,
                                        @NonNull WindowlessWindowManager wwm) {
        throw new RuntimeException("Stub!");
    }
    public void setView(@NonNull View view, @NonNull WindowManager.LayoutParams attrs) {
        throw new RuntimeException("Stub!");
    }
}
