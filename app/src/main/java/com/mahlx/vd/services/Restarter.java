package com.infusiblecoder.allinonevideodownloader.services;

import static com.infusiblecoder.allinonevideodownloader.utils.Constants.STARTFOREGROUND_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.i("Broadcast Listened", "Service tried to stop");
            //Toast.makeText(context, R.string.restarted_service, Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ClipboardMonitor.class).setAction(STARTFOREGROUND_ACTION));
            } else {
                context.startService(new Intent(context, ClipboardMonitor.class));
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
    }
}