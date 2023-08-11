package android.view;

import android.graphics.Region;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(SurfaceControl.Transaction.class)
public class SurfaceControlTransactionHidden {
    public SurfaceControl.Transaction show(SurfaceControl sc) {
        throw new RuntimeException("Stub!");
    }

    public SurfaceControl.Transaction setTrustedOverlay(SurfaceControl sc, boolean isTrustedOverlay) {
        throw new RuntimeException("Stub!");
    }

    public SurfaceControl.Transaction setTransparentRegionHint(SurfaceControl sc, Region transparentRegion) {
        throw new RuntimeException("Stub!");
    }
}
