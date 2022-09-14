package com.infusiblecoder.allinonevideodownloader.extraFeatures.videolivewallpaper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.databinding.ActivityMainLivevideoBinding;
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager;
import com.infusiblecoder.allinonevideodownloader.utils.Constants;
import com.infusiblecoder.allinonevideodownloader.utils.GlideApp;
import com.infusiblecoder.allinonevideodownloader.utils.LocaleHelper;

import java.io.File;
import java.io.IOException;

import es.dmoral.toasty.Toasty;


public class MainActivityLivewallpaper extends AppCompatActivity {
    public CinimaWallService cinimaService;

    private String url = null;

    private ActivityMainLivevideoBinding binding;
    private String nn = "nnn";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityMainLivevideoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        try {
            binding.spinKit.setVisibility(View.GONE);
            cinimaService = new CinimaWallService();
            binding.checkboxSound.setOnClickListener(view13 -> cinimaService.setEnableVideoAudio(MainActivityLivewallpaper.this, binding.checkboxSound.isChecked()));
            binding.checkboxPlayBegin.setOnClickListener(view12 -> cinimaService.setPlayB(MainActivityLivewallpaper.this, binding.checkboxPlayBegin.isChecked()));
            binding.checkboxBattery.setOnClickListener(view1 -> cinimaService.setPlayBatterySaver(MainActivityLivewallpaper.this, binding.checkboxBattery.isChecked()));


            SharedPreferences prefs = getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE);
            nn = prefs.getString("inappads", "nnn");

            if (Constants.show_Ads) {
                if (nn.equals("nnn")) {
                    AdsManager.loadInterstitialAd(MainActivityLivewallpaper.this);

                    AdsManager.loadBannerAd(this, binding.bannerContainer);
                } else {

                    binding.bannerContainer.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        try {
            if (i == 53213 && i2 == -1) {

                this.url = getPath(intent.getData());

                binding.spinKit.setVisibility(View.VISIBLE);
                binding.videoSelectButton.setVisibility(View.GONE);
                GlideApp.with(this).asBitmap().addListener(new RequestListener<>() {
                    public boolean onLoadFailed(GlideException glideException, Object obj, Target<Bitmap> target, boolean z) {
                        binding.spinKit.setVisibility(View.GONE);
                        binding.videoSelectButton.setVisibility(View.VISIBLE);
                        return false;
                    }

                    public boolean onResourceReady(Bitmap bitmap, Object obj, Target<Bitmap> target, DataSource dataSource, boolean z) {
                        binding.spinKit.setVisibility(View.GONE);
                        binding.videoSelectButton.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).load(Uri.fromFile(new File(this.url))).into(binding.imgThumb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath(Uri uri) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else return null;
        } catch (Exception e) {
            return null;
        }
    }


    private void showAdmobAds() {
        if (Constants.show_Ads) {
            if (nn.equals("nnn")) {

                AdsManager.showAdmobInterstitialAd(MainActivityLivewallpaper.this, new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        AdsManager.loadInterstitialAd(MainActivityLivewallpaper.this);

                    }
                });

            }
        }
    }

    public void set_up_video_clicked(View view) {
        try {
            showAdmobAds();
            if (this.url == null) {

                try {
                    Toasty.error(this, getString(R.string.please_select_video)).show();
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.please_select_video), Toast.LENGTH_SHORT).show();
                }

                return;
            }
            cinimaService.setEnableVideoAudio(this, binding.checkboxSound.isChecked());
            cinimaService.setPlayB(this, binding.checkboxPlayBegin.isChecked());
            cinimaService.setPlayBatterySaver(this, binding.checkboxBattery.isChecked());
            cinimaService.setVidSource(this, this.url);
            if (cinimaService.getVideoSource(this) == null) {

                try {
                    Toasty.info(this, getString(R.string.error_emty_video)).show();
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.error_emty_video), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            try {
                clearWallpaper();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
            intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(this, CinimaWallService.class));
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void video_on_clicked(View view) {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 53213);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }


}
