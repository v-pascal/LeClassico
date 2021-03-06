package com.studio.artaban.leclassico.helpers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.studio.artaban.leclassico.data.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pascal on 26/05/16.
 * Internet connection helper
 */
public final class Internet {

    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";

    //
    private static final int DEFAULT_ONLINE_TIMEOUT = 2000; // Default Internet connection check timeout (in millisecond)
    private static final String DEFAULT_ONLINE_URL = "http://www.google.com"; // Default Internet connection check URL

    private static boolean isConnected; // Connected flag (see 'isOnline' method below)

    public static boolean isOnline(Context context) { return isOnline(context, DEFAULT_ONLINE_TIMEOUT); }
    public static boolean isOnline(Context context, final int timeOut) {

        // Check Internet connection from any thread even UI thread (check INTERNET permission first)
        Logs.add(Logs.Type.V, "context: " + context + ", timeOut: " + timeOut);
        isConnected = false;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if ((netInfo == null) || (!netInfo.isConnectedOrConnecting()))
            return false;

        Runnable checkInternet = new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(DEFAULT_ONLINE_URL); // URL to check
                    HttpURLConnection connURL = (HttpURLConnection) url.openConnection();
                    connURL.setConnectTimeout(timeOut);
                    connURL.connect();

                    if (connURL.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        Logs.add(Logs.Type.I, "Connected");
                        isConnected = true;
                    }
                }
                catch (MalformedURLException e) { Logs.add(Logs.Type.F, e.getMessage()); }
                catch (SocketTimeoutException e) { Logs.add(Logs.Type.F, e.getMessage()); }
                catch (IOException e) { Logs.add(Logs.Type.F, e.getMessage()); }
                finally {
                    synchronized (this) {
                        notify();
                    }
                }
            }
        };
        synchronized (checkInternet) {

            new Thread(checkInternet).start();

            // Wait Internet connection check
            try { checkInternet.wait(); }
            catch (InterruptedException e) {
                Logs.add(Logs.Type.E, "Wait Internet interrupted: " + e.getMessage());
            }
        }
        return isConnected;
    }

    //////
    private static final int BUFFER_SIZE = 4096;
    private static final String POST_CONTENT_ENCODING = ENCODING_UTF_8;
    private static final String DEFAULT_REPLY_ENCODING = ENCODING_ISO_8859_1;

    public enum DownloadResult {

        CANCELLED, // The download has been cancelled by the user
        WRONG_URL, // Wrong URL format
        CONNECTION_FAILED, // Failed to connect to URL
        REPLY_ERROR, // Reply error
        SUCCEEDED // Download succeeded
    }
    public interface OnDownloadListener { // Download listener

        boolean onCheckCancelled();
        void onPublishProgress(int read);
    }
    public interface OnRequestListener {
        boolean onReceiveReply(String response);
    }

    //
    public static DownloadResult downloadHttpFile(String url, String file, OnDownloadListener listener) {

        Logs.add(Logs.Type.V, "url: " + url + ";file: " + file + ";listener: " + listener);

        InputStream is = null;
        OutputStream os = null;
        HttpURLConnection httpConnection = null;
        try {

            URL urlFile = new URL(url);
            httpConnection = (HttpURLConnection)urlFile.openConnection();
            httpConnection.setConnectTimeout(DEFAULT_ONLINE_TIMEOUT);
            httpConnection.connect();

            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException();

            // Save reply into expected file
            Logs.add(Logs.Type.I, "Save reply into expected file");
            is = httpConnection.getInputStream();
            os = new FileOutputStream(file);

            byte buffer[] = new byte[BUFFER_SIZE];
            int bufferRead;
            while ((bufferRead = is.read(buffer)) != Constants.NO_DATA) {

                // Check if download has been cancelled
                if ((listener != null) && (listener.onCheckCancelled()))
                    return DownloadResult.CANCELLED;

                // Check if needed to update progress bar
                if (listener != null)
                    listener.onPublishProgress(bufferRead);
                os.write(buffer, 0, bufferRead);
            }
            return DownloadResult.SUCCEEDED;
        }
        catch (MalformedURLException e) {

            Logs.add(Logs.Type.F, "Wrong web service URL: " + e.getMessage());
            return DownloadResult.WRONG_URL;
        }
        catch (IOException e) {

            Logs.add(Logs.Type.E, "Failed to connect to web service: " + e.getMessage());
            return DownloadResult.CONNECTION_FAILED;
        }
        finally {

            if (httpConnection != null)
                httpConnection.disconnect();

            try {
                if (is != null) is.close();
                if (os != null) os.close();
            }
            catch (IOException e) {
                Logs.add(Logs.Type.E, "Failed to close IO streams");
            }
        }
    }

    private static String getPostContent(ContentValues data) {
    // Format content values into HTTP content post data

        //Logs.add(Logs.Type.V, "data: " + data);
        StringBuilder postContent = new StringBuilder();
        boolean firstData = true;

        for (Map.Entry<String, Object> entry : data.valueSet()) {
            if (firstData)
                firstData = false;
            else
                postContent.append("&");

            try {
                postContent.append(URLEncoder.encode(entry.getKey(), POST_CONTENT_ENCODING));
                postContent.append('=');
                postContent.append(URLEncoder.encode(entry.getValue().toString(), POST_CONTENT_ENCODING));

            } catch (UnsupportedEncodingException e) {
                Logs.add(Logs.Type.E, "Failed to format POST data: " + e.getMessage());
            }
        }
        return postContent.toString();
    }
    public static DownloadResult downloadHttpRequest(String url, ContentValues postData, String encoding,
                                                     OnRequestListener listener) {

        Logs.add(Logs.Type.V, "url: " + url + ";postData: " + postData + ";encoding: " + encoding +
                ";listener: " + listener);
        //Logs.add(Logs.Type.I, "postData: " + postData);

        InputStream is = null;
        OutputStream os = null;
        BufferedWriter bw = null;
        HttpURLConnection httpConnection = null;
        try {

            URL urlRequest = new URL(url);
            httpConnection = (HttpURLConnection)urlRequest.openConnection();
            httpConnection.setConnectTimeout(DEFAULT_ONLINE_TIMEOUT);
            if (postData != null) {

                httpConnection.setRequestMethod("POST");
                httpConnection.setDoOutput(true);
                os = httpConnection.getOutputStream();
                bw = new BufferedWriter(new OutputStreamWriter(os, POST_CONTENT_ENCODING));
                bw.write(getPostContent(postData));
                bw.flush();
            }
            httpConnection.connect();
            int code = httpConnection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK)
                throw new IOException("Wrong HTTP code " + code + " response");

            // Store reply into string (if needed)
            if (listener != null) {

                is = httpConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,
                        (encoding == null)? DEFAULT_REPLY_ENCODING:encoding));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    response.append(line).append('\n');

                br.close();
                if (!listener.onReceiveReply(response.toString()))
                    return DownloadResult.REPLY_ERROR;

            } else
                Logs.add(Logs.Type.W, "No request listener");

            return DownloadResult.SUCCEEDED;
        }
        catch (MalformedURLException e) {

            Logs.add(Logs.Type.F, "Wrong web service URL: " + e.getMessage());
            return DownloadResult.WRONG_URL;
        }
        catch (IOException e) {

            Logs.add(Logs.Type.E, "Failed to connect to web service: " + e.getMessage());
            return DownloadResult.CONNECTION_FAILED;
        }
        finally {

            if (httpConnection != null)
                httpConnection.disconnect();

            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
                if (os != null) os.close();
                if (is != null) is.close();

            } catch (IOException e) {
                Logs.add(Logs.Type.E, "Failed to close input stream");
            }
        }
    }

    //////
    public interface OnConnectivityListener {

        void onConnection();
        void onDisconnection();
    };

    private static final ArrayList<OnConnectivityListener> connectivityListeners = new ArrayList<>();
    public static boolean addConnectivityListener(OnConnectivityListener listener) {
        Logs.add(Logs.Type.V, "listener: " + listener);

        if (!connectivityListeners.contains(listener)) {
            connectivityListeners.add(listener);
            return true;
        }
        return false;
    }
    public static boolean removeConnectivityListener(OnConnectivityListener listener) {
        Logs.add(Logs.Type.V, "listener: " + listener);
        return connectivityListeners.remove(listener);
    }
    // Connectivity listener

    public static boolean isConnected() { return isConnected; }
    public static class ConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);

            boolean previousConnection = isConnected;
            isOnline(context);
            if (previousConnection != isConnected) {
                try {
                    if (isConnected) {
                        for (OnConnectivityListener listener : connectivityListeners)
                            listener.onConnection();

                    } else {
                        for (OnConnectivityListener listener : connectivityListeners)
                            listener.onDisconnection();
                    }

                } catch (NullPointerException e) {
                    Logs.add(Logs.Type.I, "No connectivity listener");
                }
            }
        }
    }
}
