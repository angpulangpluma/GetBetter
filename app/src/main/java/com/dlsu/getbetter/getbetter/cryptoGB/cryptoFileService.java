package com.dlsu.getbetter.getbetter.cryptoGB;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by YING LOPEZ on 9/28/2017.
 */



public class cryptoFileService extends IntentService {

    private file_aes cryptfile = null;
    private final String keyloc;
    private String fileloc;

    public static String CRYPTO_FILE_INPUT = null;
    public static String CRYPTO_FILE_CHOICE = null;
    public static String CRYPTO_FILE_NAME = null;
    //public Context CRYPTO_CONTEXT = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public cryptoFileService(String name, String fileloc) {
        super(name);
        aes src = new aes();
        try {
            src.retrieveKey(fileloc);
        } catch (IOException e) {
            e.printStackTrace();
            src.setKey();
        } finally {
            this.cryptfile = new file_aes(src);
            this.keyloc = fileloc;
        }
    }

    public cryptoFileService(String name, aes src, String fileloc){
        super(name);
        this.cryptfile = new file_aes(src);
        this.keyloc = fileloc;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        byte[] in = intent.getByteArrayExtra("CRYPTO_FILE_INPUT");
        if (in.length>0)
            System.out.println("Dekitaaaaaa");
        else System.out.println("Dekinaiiiii");
//        String ch = intent.getStringExtra("CRYPTO_FILE_CHOICE");
//        String fname = intent.getStringExtra("CRYPTO_FILE_NAME");
//        String floc = "";
//        FileOutputStream fos = null;
//        File file = null;
//        aes src = new aes();
//        src.setKey();
//        cryptfile = new file_aes(src);
//
//        if (ch=="enc"){
//            floc = "Android/data/com.dlsu.getbetter.getbetter/enc/";
//        } else if (ch=="dec"){
//            floc = "Android/data/com.dlsu.getbetter.getbetter/dec/";
//        }
//        try {
//            file = new File(floc + fname);
//            fos = new FileOutputStream(file);
//            fos.write(in);
//            fos.close();
//        } catch(Exception e){
//            e.printStackTrace();
//            System.exit(1);
//        }


    }
}
