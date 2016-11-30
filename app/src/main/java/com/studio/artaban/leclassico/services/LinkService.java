package com.studio.artaban.leclassico.services;

import android.app.IntentService;
import android.content.Intent;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.tables.persistent.LinksTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pascal on 28/11/16.
 * Publication link service (needed to download link image, title, description & info data
 * from URL link)
 */
public class LinkService extends IntentService {

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
        Internet.DownloadResult result = Internet.downloadHttpRequest(link.url, null,
                new Internet.OnRequestListener() {

            @Override
            public boolean onReceiveReply(String response) {
                //Logs.add(Logs.Type.V, "response: " + response);

                ////// Image
                String content = ' ' + ATTRIBUTE_CONTENT + "=\"(.*?)\"";
                String property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_OG_IMAGE +"\"";
                Matcher matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]" + property + '|' +
                        property + "[^>]" + content + ')' + MARKER_B).matcher(response);

                if (matcher.find())
                    link.image = (matcher.group(1) != null)? matcher.group(1):matcher.group(2);
                else {

                    property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_TWITTER_IMAGE +"\"";
                    matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]" + property + '|' +
                            property + "[^>]" + content + ')' + MARKER_B).matcher(response);
                    if (matcher.find())
                        link.image = (matcher.group(1) != null)? matcher.group(1):matcher.group(2);
                }

                ////// Title
                property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_OG_TITLE +"\"";
                matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]" + property + '|' +
                        property + "[^>]" + content + ')' + MARKER_B).matcher(response);

                if (matcher.find())
                    link.title = (matcher.group(1) != null)? matcher.group(1):matcher.group(2);
                else {

                    matcher = Pattern.compile(MARKER_TITLE_A + "(.*?)" + MARKER_TITLE_B).matcher(response);
                    if (matcher.find())
                        link.title = matcher.group(1);
                }

                ////// Description
                property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_OG_DESCRIPTION +"\"";
                matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]" + property + '|' +
                        property + "[^>]" + content + ')' + MARKER_B).matcher(response);

                if (matcher.find())
                    link.description = (matcher.group(1) != null)? matcher.group(1):matcher.group(2);
                else {

                    property = ' ' + ATTRIBUTE_NAME + "=\"" + VALUE_DESCRIPTION +"\"";
                    matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]" + property + '|' +
                            property + "[^>]" + content + ')' + MARKER_B).matcher(response);
                    if (matcher.find())
                        link.description = (matcher.group(1) != null)? matcher.group(1):matcher.group(2);
                }

                ////// Info (source)
                property = ' ' + ATTRIBUTE_PROPERTY + "=\"" + VALUE_OG_SITE_NAME +"\"";
                matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]" + property + '|' +
                        property + "[^>]" + content + ')' + MARKER_B).matcher(response);

                if (matcher.find())
                    link.info = (matcher.group(1) != null)? matcher.group(1):matcher.group(2);
                else {

                    property = ' ' + ATTRIBUTE_NAME + "=\"" + VALUE_AUTHOR +"\"";
                    matcher = Pattern.compile(MARKER_META_A + '(' + content + "[^>]" + property + '|' +
                            property + "[^>]" + content + ')' + MARKER_B).matcher(response);
                    if (matcher.find())
                        link.info = (matcher.group(1) != null)? matcher.group(1):matcher.group(2);
                }
                return true;
            }
        });
        if (result != Internet.DownloadResult.SUCCEEDED)
            link.status = LinksTable.STATUS_FAILED;

        // Download image (if any)
        if (link.url != null) {










        }

        // Set link info (URL + by + source)
        if ((link.title != null) || (link.description != null)) {

            Matcher matcher = Pattern.compile("^http[s]://(.*?)/").matcher(link.url);
            link.info = matcher.group(1).toUpperCase() + ((link.info != null)?
                    ' ' + getResources().getString(R.string.by) + ' ' + link.info:"");

        } else
            link.info = null;

        // Insert|update link entry








    }
}
