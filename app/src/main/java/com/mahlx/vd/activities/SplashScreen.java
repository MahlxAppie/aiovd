package com.infusiblecoder.allinonevideodownloader.activities;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.infusiblecoder.allinonevideodownloader.BuildConfig;
import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.databinding.ActivitySplashScreenBinding;
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager;
import com.infusiblecoder.allinonevideodownloader.utils.LocaleHelper;
import com.infusiblecoder.allinonevideodownloader.utils.iUtils;


public class SplashScreen extends AppCompatActivity {

    AppUpdateManager appUpdateManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try {

            appUpdateManager = AppUpdateManagerFactory.create(SplashScreen.this);


            binding.version.setText(String.format("%s %s", getString(R.string.version), BuildConfig.VERSION_NAME));
            AdsManager.setdefaluts(SplashScreen.this);


            checkAppUpdate();

            new Thread() {
                @Override
                public void run() {
                    try {
                        Looper.prepare();
//                    iUtils.myDLphpTempCookies = iUtils.showCookiesdlphp("http://tikdd.infusiblecoder.com/");
//                    System.out.println("mysjdjhdjkh dlphp= " + iUtils.myDLphpTempCookies);
                        iUtils.myInstagramTempCookies = iUtils.showCookies("https://www.instagram.com/");
                        System.out.println("mysjdjhdjkh " + iUtils.myInstagramTempCookies);


                    } catch (Throwable e) {
                        e.printStackTrace();
                        goToMainAndFinish();
                    }
                }
            }.start();

        } catch (Throwable e) {
            e.printStackTrace();

            goToMainAndFinish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, IMMEDIATE, SplashScreen.this, 1009);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void goToMainAndFinish() {


        new CountDownTimer(1000, 1500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        }.start();


    }

    public void checkAppUpdate() {
        try {
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, IMMEDIATE, SplashScreen.this, 1009);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    goToMainAndFinish();
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                goToMainAndFinish();
            });

        } catch (Exception e) {
            e.printStackTrace();
            goToMainAndFinish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1009) {
            if (resultCode != RESULT_OK) {
                goToMainAndFinish();
            } else {
                goToMainAndFinish();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }


}



