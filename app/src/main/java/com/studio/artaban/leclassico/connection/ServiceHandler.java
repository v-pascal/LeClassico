package com.studio.artaban.leclassico.connection;

import android.os.Handler;
import android.os.Message;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 18/08/16.
 * Handler used to manage exchange between the data service & any other thread
 */
public class ServiceHandler extends Handler {

    public interface OnPublishListener { ///////////////////////////////////////////////////////////
        void publishProgress(byte step, Object data);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private byte mLoginResult; // Login & synchronization result
    public byte getResult() {
        return mLoginResult;
    }

    private boolean mCancelled; // Cancel request flag
    public boolean isCancelled() { // Used by the data service to known if needed to cancel
        return mCancelled;
    }

    //////
    public void cancel() { // Cancel thread requested
        Logs.add(Logs.Type.V, null);
        mCancelled = true;
    }

    //
    private OnPublishListener mListener; // Publish listener
    public ServiceHandler(OnPublishListener listener) {
        super();
        mListener = listener;
    }

    //////
    @Override
    public void handleMessage(Message msg) {

        Logs.add(Logs.Type.V, "msg: " + msg);
        if (mCancelled) {

            mLoginResult = (byte)Constants.NO_DATA;
            getLooper().quit();
            return; // Prevents any message process if cancelled
        }
        switch (msg.what) {
            case (byte)Constants.NO_DATA: { // Unexpected error (service not bound)

                Logs.add(Logs.Type.E, "Service not bound");
                mLoginResult = (byte)Constants.NO_DATA;
                getLooper().quit();
                break;
            }
            case DataService.LOGIN_STEP_SUCCEEDED: {

                mListener.publishProgress((byte)msg.what, (String)msg.obj);
                break;
            }
            case DataService.LOGIN_STEP_ERROR:
            case DataService.LOGIN_STEP_FAILED:
            case DataService.SYNCHRONIZATION_STEP_ERROR:
            case DataService.SYNCHRONIZATION_STEP_SUCCEEDED: {

                mLoginResult = (byte)msg.what;
                getLooper().quit();
                break;
            }
            default: { // Tables DB synchronization

                mListener.publishProgress((byte)msg.what, null);
                break;
            }
        }
    }
}
