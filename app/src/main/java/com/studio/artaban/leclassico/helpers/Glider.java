package com.studio.artaban.leclassico.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.studio.artaban.leclassico.data.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by pascal on 14/08/16.
 * Glide class helper
 * Allow to work offline + Store image displayed when online (if not already done)
 */
public class Glider {

    private Context mContext; // Activity context
    private static Glider getInstance(Context context) {
        return new Glider(context);
    }
    private Glider(Context context) {
        mContext = context;
    }

    //////
    public static Glider with(Context context) {
        return getInstance(context);
    }

    // Image locations
    private String mImageFile;
    private String mImageUrl;

    public Glider load(String filePath, String url) {
        Logs.add(Logs.Type.V, "filePath: " + filePath + ";url: " + url);

        mImageFile = filePath;
        mImageUrl = url;
        return this;
    }

    // Placeholder drawable ID
    @DrawableRes private int mPlaceholderId;

    public Glider placeholder(@DrawableRes int id) {
        Logs.add(Logs.Type.V, "id: " + id);

        mPlaceholderId = id;
        return this;
    }

    //
    public interface OnLoadListener {
        boolean setResource(Bitmap resource, ImageView imageView);
    };

    public void into(final ImageView imageView, final OnLoadListener listener) {
        Logs.add(Logs.Type.V, "view: " + imageView + ";listener: " + listener);

        // Define image URI according if already downloaded
        File imageFile = new File(Storage.get() + mImageFile);
        final boolean imageExists = imageFile.exists();
        Uri imageUri = (imageExists)? Uri.fromFile(imageFile) : Uri.parse(mImageUrl);

        Glide.with(mContext)
            .load(imageUri)
            .asBitmap()
            .centerCrop()
            .placeholder(mPlaceholderId)
            .into(new BitmapImageViewTarget(imageView) {

                @Override
                protected void setResource(Bitmap resource) {

                    //Logs.add(Logs.Type.V, "resource: " + resource);
                    if (!imageExists) {
                        try {

                            FileOutputStream fos = new FileOutputStream(Storage.get() + mImageFile);
                            if (mImageFile.contains(Constants.IMAGE_JPEG_EXTENSION))
                                resource.compress(Bitmap.CompressFormat.JPEG,
                                        Constants.IMAGE_JPEG_QUALITY, fos);
                            else
                                resource.compress(Bitmap.CompressFormat.PNG,
                                        Constants.IMAGE_PNG_QUALITY, fos);
                            fos.flush();
                            fos.close();

                        } catch (IOException e) {
                            Logs.add(Logs.Type.E, "Failed to save image: " + e.getMessage());
                        }
                    }

                    // Check if needed to display image (using listener)
                    if ((listener == null) || (!listener.setResource(resource, imageView)))
                        imageView.setImageBitmap(resource);
                }
            });
    }
}
