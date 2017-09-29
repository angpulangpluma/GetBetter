package com.dlsu.getbetter.getbetter.cryptoGB;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by YING LOPEZ on 9/28/2017.
 */



public class cryptoTextService extends IntentService {

    private text_aes crypttext = null;
    private final String keyloc;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public cryptoTextService(String name, String fileloc) {
        super(name);
        aes src = new aes();
        try {
            src.retrieveKey(fileloc);
        } catch (IOException e) {
            e.printStackTrace();
            src.setKey();
        } finally {
            this.crypttext = new text_aes(src);
            this.keyloc = fileloc;
        }
    }

    public cryptoTextService(String name, aes src, String fileloc){
        super(name);
        this.crypttext = new text_aes(src);
        this.keyloc = fileloc;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //do encryption here
    }
}
