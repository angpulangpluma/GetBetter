package com.dlsu.getbetter.getbetter.cryptoGB;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.dlsu.getbetter.getbetter.R;

/**
 * Created by YING LOPEZ on 10/2/2017.
 */

public class BackProcessResponseReciever extends BroadcastReceiver {

    public static final String ACTION_RESP = "com.dlsu.getbetter.getbetter.intent.action.MESSAGE_PROCESSED";

    @Override
    public void onReceive(Context context, Intent intent) {
//        TextView result = (TextView) findViewById(R.id.txt_result);
//        TextView result = new TextView();
//        String text = intent.getStringExtra(CryptoFileService.CRYPTO_OUT_MSG);
//        result.setText(text);
//        Log.d("result txt:", text);
    }
}
