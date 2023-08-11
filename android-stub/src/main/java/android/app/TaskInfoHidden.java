package android.app;

import android.content.res.Configuration;
import android.graphics.Point;
import android.window.WindowContainerToken;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(TaskInfo.class)
public class TaskInfoHidden {
    public int getWindowingMode() {
        throw new RuntimeException("Stub!");
    }

    public int getActivityType() {
        throw new RuntimeException("Stub!");
    }

    public Configuration configuration;

    public int displayId;

    public Point positionInParent;

    public WindowContainerToken token;
}
