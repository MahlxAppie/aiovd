package com.infusiblecoder.allinonevideodownloader.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.Objects;

public class AdapterNativeAdsSliderRecyclerView extends SliderViewAdapter<AdapterNativeAdsSliderRecyclerView.SliderAdapterNAd> {

    private final Context context;
    private NativeAd currentNativeAd;

    public AdapterNativeAdsSliderRecyclerView(Context context) {
        this.context = context;
    }


    @SuppressLint("MissingPermission")
    @Override
    public SliderAdapterNAd onCreateViewHolder(ViewGroup parent) {
        AdView adview;
        adview = new AdView(context);
        adview.setAdSize(AdSize.BANNER);

        adview.setAdUnitId(context.getString(R.string.AdmobBanner));

        float density = context.getResources().getDisplayMetrics().density;
        int height = Math.round(AdSize.BANNER.getHeight() * density);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
        adview.setLayoutParams(params);

        AdRequest request = new AdRequest.Builder().build();
        adview.loadAd(request);


        return new SliderAdapterNAd(adview);
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());

        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);

        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);

        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);

        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);

        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());

        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);

        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());

        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);

        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());

            adView.getStarRatingView().setVisibility(View.VISIBLE);

        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);

        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });

        }
    }

    @SuppressLint("MissingPermission")
    private void refreshAd() {
        try {
            AdLoader.Builder builder = new AdLoader.Builder(context, AdsManager.videoapp_AdmobNativeID);
            builder.forNativeAd(nativeAd -> {
                // OnUnifiedNativeAdLoadedListener implementation.
                // If this callback occurs after the activity is destroyed, you must call
                // destroy and return or you may get a memory leak.
                boolean activityDestroyed = false;
                activityDestroyed = ((Activity) context).isDestroyed();
                if (activityDestroyed || ((Activity) context).isFinishing() || ((Activity) context).isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                currentNativeAd.destroy();
                currentNativeAd = nativeAd;
                @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.layout_native_ads, null);

                populateNativeAdView(nativeAd, adView);
//TODO
//                    binding.flAdplaceholder.removeAllViews()
//                    binding.flAdplaceholder.addView(adView)
            });


            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("adload", "adload faild $error");

                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onBindViewHolder(SliderAdapterNAd viewHolder, final int position) {


    }

    @Override
    public int getCount() {
        return 3;
    }

    class SliderAdapterNAd extends ViewHolder {

        public AdView adView;

        public SliderAdapterNAd(View v) {
            super(v);
            if (!(itemView instanceof AdView)) {
                adView = (AdView) v.findViewById(R.id.adView);
            }
        }
    }

}