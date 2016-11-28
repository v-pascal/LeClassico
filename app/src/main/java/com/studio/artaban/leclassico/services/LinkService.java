package com.studio.artaban.leclassico.services;

import android.app.IntentService;
import android.content.Intent;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 28/11/16.
 * Publication link service (needed to download link image, title, description & info)
 */
public class LinkService extends IntentService {

    public LinkService() {
        super(LinkService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Logs.add(Logs.Type.V, "intent: " + intent);




        //intent.getData()




    }
}
