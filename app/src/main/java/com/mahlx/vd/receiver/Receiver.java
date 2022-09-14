package com.infusiblecoder.allinonevideodownloader.receiver;

import static com.infusiblecoder.allinonevideodownloader.utils.Constants.PREF_CLIP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.infusiblecoder.allinonevideodownloader.services.ClipboardMonitor;

public class Receiver extends BroadcastReceiver {
    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {
try {
    String whichAction = intent.getAction();
    prefs = context.getSharedPreferences(PREF_CLIP, Context.MODE_PRIVATE);
    editor = prefs.edit();
    if ("quit_action".equals(whichAction)) {
        Log.e("loged", "quite");
        editor.putBoolean("csRunning", false);
        editor.commit();
        context.stopService(new Intent(context,
                ClipboardMonitor.class));

        return;
    }
}catch (Exception e){
    e.printStackTrace();
}


    }
}