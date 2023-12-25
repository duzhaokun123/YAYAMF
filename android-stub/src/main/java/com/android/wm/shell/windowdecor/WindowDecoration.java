package com.android.wm.shell.windowdecor;

import android.app.ActivityManager;
import android.view.SurfaceControl;
import android.view.View;
import android.window.WindowContainerTransaction;

public abstract class WindowDecoration<T extends View & TaskFocusStateConsumer> implements AutoCloseable {
     abstract public void relayout(ActivityManager.RunningTaskInfo taskInfo);

      void relayout(RelayoutParams params, SurfaceControl.Transaction startT,
                    SurfaceControl.Transaction finishT, WindowContainerTransaction wct, T rootView,
                    RelayoutResult<T> outResult) {}

     static public class RelayoutResult<T extends View & TaskFocusStateConsumer> {}

     static public class RelayoutParams {}
}
