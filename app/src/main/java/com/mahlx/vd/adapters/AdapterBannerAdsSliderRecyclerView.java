package com.infusiblecoder.allinonevideodownloader.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager;
import com.smarteist.autoimageslider.SliderViewAdapter;


public class AdapterBannerAdsSliderRecyclerView extends SliderViewAdapter<AdapterBannerAdsSliderRecyclerView.SliderAdapterVH> {

    private final Context context;

    public AdapterBannerAdsSliderRecyclerView(Context context) {
        this.context = context;
    }


    @SuppressLint("MissingPermission")
    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        AdView adview;
        try {

            adview = new AdView(context);
            adview.setAdSize(AdSize.BANNER);
            adview.setAdUnitId(AdsManager.videoapp_AdmobBanner);

            float density = context.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight() * density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
            adview.setLayoutParams(params);
            AdView finalAdview = adview;

            adview.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    try {
                        finalAdview.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    try {
                        finalAdview.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
            AdRequest request = new AdRequest.Builder().build();
            adview.loadAd(request);
        } catch (Exception e) {
            AdsManager.setdefaluts((Activity) context);
            adview = new AdView(context);
            adview.setAdSize(AdSize.BANNER);
            adview.setAdUnitId(AdsManager.videoapp_AdmobBanner);
            AdView finalAdview = adview;
            adview.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);

                    try {
                        finalAdview.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    try {
                        finalAdview.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
            float density = context.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight() * density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
            adview.setLayoutParams(params);

            AdRequest request = new AdRequest.Builder().build();
            adview.loadAd(request);

        }

        return new SliderAdapterVH(adview);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {


    }

    @Override
    public int getCount() {
        return 2;
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        public AdView adView;

        public SliderAdapterVH(View v) {
            super(v);
            if (!(itemView instanceof AdView)) {
                adView = v.findViewById(R.id.adView);
            }
        }
    }

}