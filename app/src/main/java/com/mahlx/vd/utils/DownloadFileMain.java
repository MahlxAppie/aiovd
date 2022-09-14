package com.infusiblecoder.allinonevideodownloader.utils;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.infusiblecoder.allinonevideodownloader.utils.Constants.MY_ANDROID_10_IDENTIFIER_OF_FILE;
import static com.infusiblecoder.allinonevideodownloader.utils.Constants.directoryInstaShoryDirectorydownload_audio;
import static com.infusiblecoder.allinonevideodownloader.utils.Constants.directoryInstaShoryDirectorydownload_images;
import static com.infusiblecoder.allinonevideodownloader.utils.Constants.directoryInstaShoryDirectorydownload_videos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.infusiblecoder.allinonevideodownloader.R;

import java.io.File;
import java.lang.reflect.Method;

public class DownloadFileMain {
    public static DownloadManager downloadManager;
    public static long downloadID;


    public static void startDownloading(final Context context, String url, String title, String ext) {

        try {

            String cutTitle = "";

            DownloadBroadcastReceiver.isFirstTimeDownload = false;
            cutTitle = MY_ANDROID_10_IDENTIFIER_OF_FILE + title;


            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            cutTitle = cutTitle.replaceAll(characterFilter, "");
            cutTitle = cutTitle.replaceAll("['+.^:,#\"]", "");
            cutTitle = cutTitle.replace(" ", "-").replace("!", "").replace(":", "") + ext;
            if (cutTitle.length() > 100) {
                cutTitle = cutTitle.substring(0, 100) + ext;
            }

            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            url = url.replace("\"", "");

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(title);
            request.setDescription(context.getString(R.string.downloading_des));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            try {
                File file_v = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + directoryInstaShoryDirectorydownload_videos);
                if (!file_v.exists()) {
                    file_v.mkdir();
                }
                File file_i = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + directoryInstaShoryDirectorydownload_images);
                if (!file_i.exists()) {
                    file_i.mkdir();
                }

                File file_a = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + directoryInstaShoryDirectorydownload_audio);
                if (!file_a.exists()) {
                    file_a.mkdir();
                }
            }catch (IllegalStateException e){
                e.printStackTrace();
            }

            switch (ext) {
                case ".png":
                case ".jpg":
                case ".gif":
                case ".jpeg":
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, directoryInstaShoryDirectorydownload_images + cutTitle);
                    break;
                case ".mp4":
                case ".webm":
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, directoryInstaShoryDirectorydownload_videos + cutTitle);
                    break;
                case ".mp3":
                case ".m4a":
                case ".wav":
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, directoryInstaShoryDirectorydownload_audio + cutTitle);
                    break;
            }


            request.allowScanningByMediaScanner();
            downloadID = downloadManager.enqueue(request);
            Log.e("downloadFileName", cutTitle);


            expandNotificationBar(context);

            //iUtils.ShowToast(context, context.getResources().getString(R.string.don_start));

            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, context.getResources().getString(R.string.don_start), Toast.LENGTH_LONG).show());


        } catch (Exception e) {
            e.printStackTrace();
            try {
                ((Activity) context).runOnUiThread(() -> Toast.makeText(context, context.getResources().getString(R.string.error_occ), Toast.LENGTH_LONG).show());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    static void expandNotificationBar(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED)
            return;

        try {
            @SuppressLint("WrongConstant") Object service = context.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusbarManager.getMethod("expandNotificationsPanel"); //<-
            expand.invoke(service);
        } catch (Exception e) {
            Log.e("StatusBar", e.toString());
            Toast.makeText(context.getApplicationContext(), "Expansion Not Working", Toast.LENGTH_SHORT).show();
        }
    }


}