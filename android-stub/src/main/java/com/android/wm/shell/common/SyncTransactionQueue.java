package com.android.wm.shell.common;

import android.view.SurfaceControl;

public class SyncTransactionQueue {
    public void runInSync(TransactionRunnable runnable) {}

       /** Task to run with transaction. */
    public interface TransactionRunnable {
        /** Runs with transaction. */
        void runWithTransaction(SurfaceControl.Transaction t);
    }
}
