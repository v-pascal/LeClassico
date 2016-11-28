package com.studio.artaban.leclassico.helpers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

/**
 * Created by pascal on 09/05/16.
 * Storage helper
 */
public final class Storage {

    public static final String FOLDER_PROFILES = File.separator + "Profiles";
    public static final String FOLDER_PHOTOS = File.separator + "Photos";
    public static final String FOLDER_LINKS = File.separator + "Links";

    //
    private static String FOLDER; // Application working folder path
    public static String get() {
        return FOLDER;
    }

    //////
    public static boolean moveFile(String src, String dst) {

        Logs.add(Logs.Type.V, "src: " + src + ", dst: " + dst);
        File sourceFile = new File(src);
        File destinationFile = new File(dst);

        // Check if file exists
        if (!sourceFile.exists()) {

            Logs.add(Logs.Type.W, "Source file '" + src + "' does not exist");
            return false;
        }

        // Check if the source is a file
        if (!sourceFile.isFile()) {

            Logs.add(Logs.Type.W, "The source '" + src + "' is not a file");
            return false;
        }

        if (!sourceFile.renameTo(destinationFile)) {
            try { // Try to copy it then delete

                copyFile(sourceFile, destinationFile);
                sourceFile.delete();
            }
            catch (IOException e) {

                Logs.add(Logs.Type.E, "Failed to move file from '" + src + "' to '" + dst + '\'');
                return false;
            }
        }
        return true;
    }
    public static void copyFile(File src, File dst) throws IOException {
        Logs.add(Logs.Type.V, "src: " + src + ", dst: " + dst);

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try { inChannel.transferTo(0, inChannel.size(), outChannel); }
        finally {

            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }
    public static String readFile(File file) throws IOException {
        Logs.add(Logs.Type.V, "file: " + file);

        FileInputStream fin = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        StringBuilder content = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
            content.append(line).append("\n");

        reader.close();
        fin.close();
        return content.toString();
    }
    public static void removeFiles(final String regex) {
        Logs.add(Logs.Type.V, "regex: " + regex);

        File files = new File(FOLDER);
        File[] filesToDelete = files.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.matches(regex);
            }
        });
        if (filesToDelete == null)
            return;

        for (File file : filesToDelete) {
            if (file.isFile())
                file.delete();
        }
    }
    public static boolean createFolder(String folder) {
        Logs.add(Logs.Type.V, "folder: " + folder);

        File directory = new File(folder);
        if ((!directory.exists()) && (!directory.mkdir())) {

            Logs.add(Logs.Type.E, "Failed to create folder: " + folder);
            return false;
        }
        return true;
    }

    //////
    public static boolean createWorkingFolders(Context context) {
        Logs.add(Logs.Type.V, "context: " + context);

        File workingFolder = context.getExternalFilesDir(null);
        if (workingFolder == null) {
            Logs.add(Logs.Type.F, "Shared storage is not currently available");
            return false;
        }
        Storage.FOLDER = workingFolder.getAbsolutePath();

        // Create sub folders
        createFolder(FOLDER + Storage.FOLDER_PROFILES);
        createFolder(FOLDER + Storage.FOLDER_PHOTOS);
        createFolder(FOLDER + Storage.FOLDER_LINKS);
        return true;
    }
}