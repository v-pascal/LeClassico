package com.studio.artaban.leclassico.tools;

import android.app.Activity;
import android.os.Bundle;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 06/08/16.
 * Tool to be able to run & wait a task finished on the UI thread
 */
public class WaitUiThread {

    public static abstract class TaskToRun {
        public abstract void proceed(Bundle result);
    };

    //////
    public static Bundle run(final Activity activity, final TaskToRun task) throws NullPointerException {

        Logs.add(Logs.Type.V, "activity: " + activity + ";task: " + task);
        final Bundle result = new Bundle();

        Runnable taskRunnable = new Runnable() {
            @Override
            public void run() {

                Logs.add(Logs.Type.I, "Proceed task");
                task.proceed(result);

                // Notify initialization finished
                synchronized (this) { notify(); }
            }
        };
        synchronized (taskRunnable) {

            activity.runOnUiThread(taskRunnable);

            // Wait task finished on UI thread
            try { taskRunnable.wait(); }
            catch (InterruptedException e) {
                Logs.add(Logs.Type.E, e.getMessage());
            }
        }
        return result;
    }
}
