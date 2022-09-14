package com.infusiblecoder.allinonevideodownloader;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.multidex.MultiDexApplication;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager;
import com.infusiblecoder.allinonevideodownloader.utils.AppOpenManager;
import com.infusiblecoder.allinonevideodownloader.utils.DownloadBroadcastReceiver;
import com.infusiblecoder.allinonevideodownloader.utils.LocaleHelper;
import com.onesignal.OneSignal;

import java.util.Locale;
import java.util.concurrent.Executor;

public class Appcontroller extends MultiDexApplication {

    AppOpenManager appOpenManager;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {

            MobileAds.initialize(
                    getApplicationContext(),
                    initializationStatus -> {
                    });


            appOpenManager = new AppOpenManager(this);

            //firebase
            FirebaseMessaging.getInstance().subscribeToTopic("all");
            // OneSignal Initialization
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

            OneSignal.initWithContext(this);
            OneSignal.setAppId(getString(R.string.onsignalappid));


            AudienceNetworkAds.initialize(this);

            SharedPreferences prefs = getSharedPreferences("lang_pref", MODE_PRIVATE);
            // System.out.println("default lang = "+Locale.getDefault().getLanguage());

            String lang = prefs.getString("lang", Locale.getDefault().getLanguage());


            LocaleHelper.setLocale(getApplicationContext(), lang);
//
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectCustomSlowCalls()
//                    .detectDiskWrites()
//                    .detectNetwork()
//                    .penaltyLog()
//                    .build());


            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);


            FirebaseRemoteConfigSettings.Builder configBuilder = new FirebaseRemoteConfigSettings.Builder();

            if (BuildConfig.DEBUG) {
                long cacheInterval = 0;
                configBuilder.setMinimumFetchIntervalInSeconds(cacheInterval);
            }
            firebaseRemoteConfig.setConfigSettingsAsync(configBuilder.build());


            if (!BuildConfig.DEBUG) {
                fetchRemoteTitle();


            } else {
                //TODO dont change these ids
                AdsManager.videoapp_AdmobNativeID = "ca-app-pub-3940256099942544/2247696110";
                AdsManager.videoapp_AdmobRewardID = "ca-app-pub-3940256099942544/5224354917";
                AdsManager.videoapp_AdmobInterstitial = "ca-app-pub-3940256099942544/1033173712";
                AdsManager.videoapp_AdmobBanner = "ca-app-pub-3940256099942544/6300978111";
                AdsManager.videoapp_AdmobAppId = "ca-app-pub-3940256099942544~3347511713";

            }


            registerReceiver(new DownloadBroadcastReceiver(), new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void fetchRemoteTitle() {

        AdsManager.videoapp_AdmobNativeID = firebaseRemoteConfig.getString("videoapp_AdmobNativeID");
        AdsManager.videoapp_AdmobRewardID = firebaseRemoteConfig.getString("videoapp_AdmobRewardID");
        AdsManager.videoapp_AdmobInterstitial = firebaseRemoteConfig.getString("videoapp_AdmobInterstitial");
        AdsManager.videoapp_AdmobBanner = firebaseRemoteConfig.getString("videoapp_AdmobBanner");
        AdsManager.videoapp_AdmobAppId = firebaseRemoteConfig.getString("videoapp_AdmobAppId");


        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener((Executor) this, task -> {


                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        //System.err.println("failedloadidss done = "+AdsManager.videoapp_AdmobInterstitial);

                    } else {

                        // System.err.println("failedloadidss = "+AdsManager.videoapp_AdmobInterstitial);

//                            Toast.makeText(SplashScreen.this, "Fetch failed",
//                                    Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    System.err.println("failedloadidss falier = " + AdsManager.videoapp_AdmobInterstitial);

                });

    }

}
