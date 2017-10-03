package com.dlsu.getbetter.getbetter.cryptoGB;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.dlsu.getbetter.getbetter.DirectoryConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static com.dlsu.getbetter.getbetter.cryptoGB.BackProcessResponseReciever.ACTION_RESP;

/**
 * Created by YING LOPEZ on 9/28/2017.
 */



public class CryptoFileService extends IntentService {

    private file_aes cryptfile = null;
//    private final String keyloc;
    private String fileloc;

    public static String CRYPTO_FILE_INPUT = null;
    public static String CRYPTO_FILE_CHOICE = null;
    public static String CRYPTO_FILE_NAME = null;
    //public Context CRYPTO_CONTEXT = null;
    public static String CRYPTO_OUT_MSG = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
//    public CryptoFileService(String name, String fileloc) {
//        super(name);
//        aes src = new aes();
//        try {
//            src.retrieveKey(fileloc);
//        } catch (IOException e) {
//            e.printStackTrace();
//            src.setKey();
//        } finally {
//            this.cryptfile = new file_aes(src);
//            this.keyloc = fileloc;
//        }
//    }
//
//    public CryptoFileService(String name, aes src, String fileloc){
//        super(name);
//        this.cryptfile = new file_aes(src);
//        this.keyloc = fileloc;
//    }

    public CryptoFileService(String name){
        super(name);
        Log.d("new service", "yes");
    }
    public CryptoFileService(){
        super("CryptoFileService");
        Log.d("new service", "yes");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("service in", "yes");
//        Log.d("CRYPTO_FILE_NAME", CRYPTO_FILE_NAME);
        byte[] in = intent.getByteArrayExtra("CRYPTO_FILE_INPUT");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        if (in.length>0){
            Log.d("in.length", "yes!");
            broadcastIntent.putExtra(CRYPTO_OUT_MSG, "dekitaaaaa");
            FileOutputStream fos = null;
            File path = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS),
                    DirectoryConstants.CRYPTO_FOLDER);
            boolean success = true;
            success = path.mkdirs();
//            MediaScannerConnection.scanFile(this, new String[] {path.toString()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener(){
//                        public void onScanCompleted(String path, Uri uri){
//                            Log.d("ExternalStorage", "Scanned" + path + ":");
//                            Log.d("ExternalStorage", "-> uri=" + uri);
//                        }
//                    });
            if (success){
                File input = new File(path, intent.getCharArrayExtra("CRYPTO_FILE_NAME").toString());
                try {
//            file = new File(floc + fname);
                    fos = new FileOutputStream(input);
                    fos.write(in);
                    fos.flush();
                    fos.close();
                    if(input.exists()) Log.i("file created", "yes");
                    else Log.i("file created", "no");
//                    MediaScannerConnection.scanFile(this, new String[] {input.toString()}, null,
//                            new MediaScannerConnection.OnScanCompletedListener(){
//                                public void onScanCompleted(String path, Uri uri){
//                                    Log.d("ExternalStorage", "Scanned" + path + ":");
//                                    Log.d("ExternalStorage", "-> uri=" + uri);
//                                }
//                            });
                } catch(Exception e){
                    e.printStackTrace();
                    broadcastIntent.putExtra(CRYPTO_OUT_MSG, "noooooo");
                    sendBroadcast(broadcastIntent);
//                System.exit(1);
                }
            } else{
                Log.i("file created", "no!");
                sendBroadcast(broadcastIntent);
            }

        } else{
            Log.d("in.length", "no!");
            broadcastIntent.putExtra(CRYPTO_OUT_MSG, "dekinaiiii");
            sendBroadcast(broadcastIntent);
        }
//            System.out.println("Dekitaaaaaa");
//        else System.out.println("Dekinaiiiii");
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
