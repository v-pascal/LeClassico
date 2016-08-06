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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            Logs.add(Logs.Type.V, "name: " + name + ";binder: " + binder);
            mDataService = ((DataService.DataBinder)binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Logs.add(Logs.Type.W, "name: " + name);
            if (mDataService != null) {
                mDataService.stop();
                mDataService = null;
            }
        }
    };

    //////
    public void bind(Activity activity) {

        Logs.add(Logs.Type.V, "activity: " + activity);
        if (mBound) {
            Logs.add(Logs.Type.E, "Data service already bound");
            return;
        }
        mBound = true;

        Intent bindIntent = new Intent(activity, DataService.class);
        activity.bindService(bindIntent, mConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
    }
    public void unbind(Activity activity) {

        Logs.add(Logs.Type.V, "activity: " + activity);
        if (mBound) {
            activity.unbindService(mConnection);
            mBound = false;
        }
    }

    //
    public DataService get() {
        return mDataService;
    }
}
