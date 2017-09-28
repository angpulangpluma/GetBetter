package com.dlsu.getbetter.getbetter.cryptoGB;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by YING LOPEZ on 9/28/2017.
 */



public class cryptoService extends IntentService {

    private file_aes cryptfile = null;
    private text_aes crypttext = null;
    private final String keyloc;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public cryptoService(String name, String fileloc) {
        super(name);
        aes src = new aes();
        try {
            src.retrieveKey(fileloc);
        } catch (IOException e) {
            e.printStackTrace();
            src.setKey();
        } finally {
            this.cryptfile = new file_aes(src);
            this.crypttext = new text_aes(src);
            this.keyloc = fileloc;
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //do encryption here
    }
}
