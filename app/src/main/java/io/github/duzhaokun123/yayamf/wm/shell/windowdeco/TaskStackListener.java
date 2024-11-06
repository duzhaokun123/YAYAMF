package io.github.duzhaokun123.yayamf.wm.shell.windowdeco;

import android.app.ActivityManager;
import android.app.ITaskStackListener;
import android.content.ComponentName;
import android.os.Handler;
import android.os.RemoteException;
import android.window.TaskSnapshot;

import io.github.duzhaokun123.yaxh.utils.logger.ALog;

public class TaskStackListener extends ITaskStackListener.Stub {
    private YAYAMFWindowDecorViewModel viewModel;
    public TaskStackListener(YAYAMFWindowDecorViewModel viewModel) {
        this.viewModel = viewModel;
    }
    @Override
    public void onTaskStackChanged() throws RemoteException {

    }

    @Override
    public void onActivityPinned(String packageName, int userId, int taskId, int stackId) throws RemoteException {

    }

    @Override
    public void onActivityUnpinned() throws RemoteException {

    }

    @Override
    public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo task, boolean homeTaskVisible, boolean clearedTask, boolean wasVisible) throws RemoteException {

    }

    @Override
    public void onActivityForcedResizable(String packageName, int taskId, int reason) throws RemoteException {

    }

    @Override
    public void onActivityDismissingDockedTask() throws RemoteException {

    }

    @Override
    public void onActivityLaunchOnSecondaryDisplayFailed(ActivityManager.RunningTaskInfo taskInfo, int requestedDisplayId) throws RemoteException {

    }

    @Override
    public void onActivityLaunchOnSecondaryDisplayRerouted(ActivityManager.RunningTaskInfo taskInfo, int requestedDisplayId) throws RemoteException {

    }

    @Override
    public void onTaskCreated(int taskId, ComponentName componentName) throws RemoteException {

    }

    @Override
    public void onTaskRemoved(int taskId) throws RemoteException {

    }

    @Override
    public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {

    }

    @Override
    public void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        ALog.INSTANCE.d("onTaskDescriptionChanged" + taskInfo.taskDescription.toString(), null);
        YAYAMFWindowDecoration windowDecoration = viewModel.getWindowDecorationById().get(taskInfo.taskId);
        if (windowDecoration != null) {
            windowDecoration.postRelayout(taskInfo);
        }
    }

    @Override
    public void onActivityRequestedOrientationChanged(int taskId, int requestedOrientation) throws RemoteException {

    }

    @Override
    public void onTaskRemovalStarted(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {

    }

    @Override
    public void onTaskProfileLocked(ActivityManager.RunningTaskInfo taskInfo, int userId) throws RemoteException {

    }

    @Override
    public void onTaskSnapshotChanged(int taskId, TaskSnapshot snapshot) throws RemoteException {

    }

    @Override
    public void onTaskSnapshotInvalidated(int taskId) throws RemoteException {

    }

    @Override
    public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {

    }

    @Override
    public void onTaskDisplayChanged(int taskId, int newDisplayId) throws RemoteException {

    }

    @Override
    public void onRecentTaskListUpdated() throws RemoteException {

    }

    @Override
    public void onRecentTaskListFrozenChanged(boolean frozen) throws RemoteException {

    }

    @Override
    public void onTaskFocusChanged(int taskId, boolean focused) throws RemoteException {

    }

    @Override
    public void onTaskRequestedOrientationChanged(int taskId, int requestedOrientation) throws RemoteException {

    }

    @Override
    public void onActivityRotation(int displayId) throws RemoteException {

    }

    @Override
    public void onTaskMovedToBack(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
        ALog.INSTANCE.d("onTaskMovedToBack" + taskInfo.taskDescription.toString(), null);
    }

    @Override
    public void onLockTaskModeChanged(int mode) throws RemoteException {

    }
}
