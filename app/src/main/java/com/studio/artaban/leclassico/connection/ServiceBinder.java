package com.studio.artaban.leclassico.connection;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 05/08/16.
 * Data service binder class
 * To create connection between activities & the data service
 */
public class ServiceBinder {

    private DataService mDataService; // Data service reference
    private boolean mBound; // Service bound flag

    private ServiceConnection mConnection;

    //////
    public interface OnServiceListener {
        void onServiceConnected();
        void onServiceDisconnected(ServiceConnection connection);
    };
    public boolean bind(Activity activity, final OnServiceListener listener) {

        Logs.add(Logs.Type.V, "activity: " + activity + ";listener: " + listener);
        if (mBound) {

            Logs.add(Logs.Type.W, "Data service already bound");
            return true; // Already bound
        }
        mBound = true;

        mConnection = new ServiceConnection() { // Implement service connection
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {

                Logs.add(Logs.Type.V, "name: " + name + ";binder: " + binder);
                mDataService = ((DataService.DataBinder)binder).getService();

                if (listener != null)
                    listener.onServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

                Logs.add(Logs.Type.W, "name: " + name);
                if (mDataService != null) {
                    if (listener != null)
                        listener.onServiceDisconnected(this);

                    mDataService = null;
                }
            }
        };
        Intent bindIntent = new Intent(activity, DataService.class);
        return activity.bindService(bindIntent, mConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
    }
    public void unbind(Activity activity) {

        Logs.add(Logs.Type.V, "activity: " + activity);
        if (mBound) {
            activity.unbindService(mConnection);
            mBound = false;

        } else
            Logs.add(Logs.Type.W, "Service not bound");
    }

    //
    public DataService get() {
        return mDataService;
    }
}
