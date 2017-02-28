package com.studio.artaban.leclassico.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.tables.persistent.LinksTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Storage;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pascal on 28/11/16.
 * Publication link service (needed to download image, title, description & info data from URL link)
 */
public class LinkService extends IntentService {

    public static final String EXTRA_DATA_URI = "uri";
    // Extra data keys

    public LinkService() {
        super(LinkService.class.getName());
    }

    //////
    private static String MARKER_META_A = "<meta";
    private static String MARKER_TITLE_A = "<title>";
    private static String MARKER_TITLE_B = "</title>";
    private static String MARKER_B = "/>";

    private static String ATTRIBUTE_NAME = "name";
    private static String ATTRIBUTE_CONTENT = "content";
    private static String ATTRIBUTE_PROPERTY = "property";

    private static String VALUE_DESCRIPTION = "description";
    private static String VALUE_AUTHOR = "author";

    private static String VALUE_OG_SITE_NAME = "og:site_name";
    private static String VALUE_OG_TITLE = "og:title";
    private static String VALUE_OG_DESCRIPTION = "og:description";
    private static String VALUE_OG_IMAGE = "og:image";

    private static String VALUE_TWITTER_IMAGE = "twitter:image";

    ////// IntentService ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onHandleIntent(Intent intent) {

        Logs.add(Logs.Type.V, "intent: " + intent);
        if (intent.getData() == null)
            throw new IllegalArgumentException("Missing URL data");

        // Get URL request HTML page replied
        final LinksTable.Link link = new LinksTable.Link(Constants.NO_DATA);
        link.url = intent.getData().toString();
        link.status = LinksTable.STATUS_DONE;

        Internet.DownloadResult result = Internet.downloadHttpRequest(link.url, null, Internet.ENCODING_UTF_8,
                new Internet.OnRequestListener() {

            @Override
            public boolean onReceiveReply(String response) {
                //Logs.add(Logs.Type.V, "response: " + response);

                ////// Image
                String content = ' ' + ATTRIBUTE_CONTENT + "=\"(.*?)\"";
                String property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_OG_IMAGE +"\"";
                Matcher matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]*" + property + '|' +
                        property + "[^>]*" + content + ") *" + MARKER_B).matcher(response);

                if (matcher.find())
                    link.image = (matcher.group(2) != null)? matcher.group(2):matcher.group(3);
                else {
                    property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_TWITTER_IMAGE +"\"";
                    matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]*" + property + '|' +
                            property + "[^>]*" + content + ") *" + MARKER_B).matcher(response);
                    if (matcher.find())
                        link.image = (matcher.group(2) != null)? matcher.group(2):matcher.group(3);
                }

                ////// Title
                property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_OG_TITLE +"\"";
                matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]*" + property + '|' +
                        property + "[^>]*" + content + ") *" + MARKER_B).matcher(response);

                if (matcher.find())
                    link.title = (matcher.group(2) != null)? matcher.group(2):matcher.group(3);
                else {
                    matcher = Pattern.compile(MARKER_TITLE_A + "(.*?)" + MARKER_TITLE_B).matcher(response);
                    if (matcher.find())
                        link.title = matcher.group(1);
                }

                ////// Description
                property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_OG_DESCRIPTION +"\"";
                matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]*" + property + '|' +
                        property + "[^>]*" + content + ") *" + MARKER_B).matcher(response);

                if (matcher.find())
                    link.description = (matcher.group(2) != null)? matcher.group(2):matcher.group(3);
                else {
                    property = ' ' + ATTRIBUTE_NAME + "=\"" + VALUE_DESCRIPTION +"\"";
                    matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]*" + property + '|' +
                            property + "[^>]*" + content + ") *" + MARKER_B).matcher(response);
                    if (matcher.find())
                        link.description = (matcher.group(2) != null)? matcher.group(2):matcher.group(3);
                }

                ////// Info (source)
                property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_OG_SITE_NAME +"\"";
                matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]*" + property + '|' +
                        property + "[^>]*" + content + ") *" + MARKER_B).matcher(response);

                if (matcher.find())
                    link.info = (matcher.group(2) != null)? matcher.group(2):matcher.group(3);
                else {
                    property = ' ' + ATTRIBUTE_NAME + "=\"" + VALUE_AUTHOR +"\"";
                    matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]*" + property + '|' +
                            property + "[^>]*" + content + ") *" + MARKER_B).matcher(response);
                    if (matcher.find())
                        link.info = (matcher.group(2) != null)? matcher.group(2):matcher.group(3);
                }

                Logs.add(Logs.Type.I, "Title: " + link.title);
                Logs.add(Logs.Type.I, "Description: " + link.description);
                Logs.add(Logs.Type.I, "Info: " + link.info);
                return true;
            }
        });
        if (result != Internet.DownloadResult.SUCCEEDED) {
            Logs.add(Logs.Type.W, "Failed to download link date (" + link.url + ')');
            link.status = LinksTable.STATUS_FAILED;
        }

        // Set link info (URL + by + source)
        if ((link.title != null) || (link.description != null)) {
            Matcher matcher = Pattern.compile("https?://([^/]*?)(/|$)").matcher(link.url);
            if (matcher.find())
                link.info = matcher.group(1).toUpperCase() + ((link.info != null)?
                        ' ' + getResources().getString(R.string.by) + ' ' + link.info.toUpperCase():"");
        } else
            link.info = null;

        ////// Insert link entry (if not already exists)
        Uri uri = Uri.parse(DataProvider.CONTENT_URI + LinksTable.TABLE_NAME);
        long linkId = DataTable.getEntryId(getContentResolver(), LinksTable.TABLE_NAME,
                LinksTable.COLUMN_URL + "='" + link.url + '\'');
        if (linkId == Constants.NO_DATA) {

            Date now = new Date();
            DateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            ContentValues values = new ContentValues();

            values.put(LinksTable.COLUMN_URL, link.url);
            values.put(LinksTable.COLUMN_STATUS, link.status);
            values.put(LinksTable.COLUMN_TITLE, link.title);
            values.put(LinksTable.COLUMN_DESCRIPTION, link.description);
            values.put(LinksTable.COLUMN_INFO, link.info);
            values.put(Constants.DATA_COLUMN_STATUS_DATE, dateFormat.format(now));
            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, 0); // Not used
            Uri entryUri = getContentResolver().insert(uri, values);

            if (entryUri != null)
                linkId = Integer.valueOf(entryUri.getLastPathSegment());
        }

        // Download link image (if any)
        if ((link.image != null) && (linkId != Constants.NO_DATA)) {

            // Create link folder (e.i .../Links/link ID/image file name)
            String imageFile = Storage.get() + Storage.FOLDER_LINKS + File.separator + linkId;
            Storage.createFolder(imageFile);

            String imageName = null;
            int imagePos = link.image.lastIndexOf('/');
            if (imagePos != Constants.NO_DATA)
                imageName = link.image.substring(imagePos);
            if ((imageName == null) || (imageName.isEmpty())) {

                Logs.add(Logs.Type.E, "Wrong image URL");
                link.status = LinksTable.STATUS_IMAGE_FAILED;
                link.image = null;

            } else {
                imageFile += File.separator + imageName;

                // Download image
                result = Internet.downloadHttpFile(link.image, imageFile, null);
                if (result != Internet.DownloadResult.SUCCEEDED) {

                    Logs.add(Logs.Type.W, "Failed to download link image (" + link.image + ')');
                    link.status = LinksTable.STATUS_IMAGE_FAILED;
                    link.image = null;

                } else
                    link.image = imageName;
            }

            ////// Update link entry
            ContentValues values = new ContentValues();
            values.put(LinksTable.COLUMN_STATUS, link.status);
            values.put(LinksTable.COLUMN_IMAGE, link.image);

            getContentResolver().update(Uri.withAppendedPath(uri, String.valueOf(linkId)),
                    values, null, null);
        }

        // Force URI cursor to refresh (if any)
        if (intent.hasExtra(EXTRA_DATA_URI))
            getContentResolver().notifyChange((Uri)intent.getParcelableExtra(EXTRA_DATA_URI), null);
    }
}
