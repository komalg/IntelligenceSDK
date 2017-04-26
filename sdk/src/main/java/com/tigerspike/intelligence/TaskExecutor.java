package com.tigerspike.intelligence;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import java.util.ArrayList;
import java.util.LinkedList;

public class TaskExecutor {

    private Application mApplication;
    private OAuth mOAuth;

    private LinkedList<IntelligenceTask> mTaskQueue;
    private Boolean mIsBusy = false;
    private Boolean mNetworkConnected = false;

    ArrayList<OnNetworkChangeListener> mOnNetworkChangeListeners;

    TaskExecutor(Application application, OAuth oAuth) {

        mApplication = application;
        mOAuth = oAuth;

        mTaskQueue = new LinkedList<>();

        mOnNetworkChangeListeners = new ArrayList<>();

        // Connection Change Listener
        if (Utils.hasPermission(mApplication, Constants.ANDROID_PERMISSION_ACCESS_NETWORK_STATE)) {

            // Check initial network state
            checkConnection();

            // Register broadcast Listener
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mApplication.registerReceiver(mConnectionListener, intentFilter);

        } else {
            mConnectionListener = null;
        }

    }

    void shutdown() {
        mApplication.unregisterReceiver(mConnectionListener);
    }

    public Boolean hasConnection() {
        return mNetworkConnected;
    }

    private void checkConnection() {

        // Check initial network state
        ConnectivityManager connectivityManager = (ConnectivityManager) mApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean connected = networkInfo != null && networkInfo.isConnected();

        if (connected && !mNetworkConnected) {
            mNetworkConnected = true;
            onNetworkDetected();
        } else if (!connected && mNetworkConnected) {
            mNetworkConnected = false;
            onNetworkLost();
        }

    }

    private BroadcastReceiver mConnectionListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            checkConnection();
        }

    };

    private void onNetworkDetected() {
        for (OnNetworkChangeListener onNetworkChangeListener : mOnNetworkChangeListeners) {
            onNetworkChangeListener.onNetworkDetected();
        }
    }

    private void onNetworkLost() {
        for (OnNetworkChangeListener onNetworkChangeListener : mOnNetworkChangeListeners) {
            onNetworkChangeListener.onNetworkLost();
        }
    }

    void addTask(IntelligenceTask task) {
        mTaskQueue.addLast(task);
        processQueue(false);
    }

    private synchronized void processQueue(boolean inProgress) {

        if (!inProgress && mIsBusy) {
            return;
        }

        if (mTaskQueue.size() == 0) {
            mIsBusy = false;
            return;
        }

        mIsBusy = true;

        IntelligenceTask task = mTaskQueue.poll();

        task.onPreExecute();

        new TaskTask().execute(task);

    }

    private void onExecuted(IntelligenceTask intelligenceTask, Exception exception) {

        if (exception == null) {
            intelligenceTask.onPostExecute();
        } else {
            if (exception instanceof IntelligenceException) {

                Log.e(exception.getClass().getName(), "(" + String.valueOf(((IntelligenceException) exception).getErrorCode()) + ") " + String.valueOf(exception.getMessage()));

                intelligenceTask.onError((IntelligenceException) exception);
            } else {

                Log.e(exception.getClass().getName(), String.valueOf(exception.getMessage()));

                intelligenceTask.onError(new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.UnhandledError, "Unknown exception, see cause for more details").addCause(exception));
            }
        }

        processQueue(true);

    }

    // TODO : Implement custom AsyncTask directly on IntelligenceTask.

    class TaskTask extends AsyncTask<IntelligenceTask, Void, IntelligenceTask> {

        private Exception mException;

        @Override
        protected IntelligenceTask doInBackground(IntelligenceTask... tasks) {
            IntelligenceTask intelligenceTask = tasks[0];
            intelligenceTask.setOAuth(mOAuth);
            try {
                intelligenceTask.execute();
            } catch (Exception exception) {
                mException = exception;
            }
            return intelligenceTask;
        }

        protected void onPostExecute(IntelligenceTask intelligenceTask) {
            onExecuted(intelligenceTask, mException);
        }

    }

    public void addNetworkChangeListener(OnNetworkChangeListener onNetworkChangeListener) {
        if (!mOnNetworkChangeListeners.contains(onNetworkChangeListener)) {
            mOnNetworkChangeListeners.add(onNetworkChangeListener);
        }
    }

    public void removeNetworkChangeListener(OnNetworkChangeListener onNetworkChangeListener) {
        mOnNetworkChangeListeners.remove(onNetworkChangeListener);
    }

    interface OnNetworkChangeListener {
        void onNetworkLost();
        void onNetworkDetected();
    }

}

