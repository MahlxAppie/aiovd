package com.infusiblecoder.allinonevideodownloader.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.adapters.AdapterBannerAdsSliderRecyclerView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.File;
import java.util.Objects;
import java.util.Random;

public class AdsManager {

    //TODO change this if you want to limit interstishal ads
    public static int NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION = 12;
    //TODO dont touch this
    public static int NUMBER_OF_INTERSTISHAL_ADS_SHOWN = 0;


    //TODO change this if you want to limit banner ads
    public static int NUMBER_OF_BANNER_ADS_PER_SESSION = 5;
    //TODO dont touch this
    public static int NUMBER_OF_BANNER_ADS_SHOWN = 0;


    //ALL Ads Ids
    public static String
            videoapp_AdmobAppId,
            videoapp_AdmobBanner,
            videoapp_AdmobInterstitial,
            videoapp_AdmobNativeID,
            videoapp_AdmobRewardID;


    private static com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    public static RewardedAd mRewardedAd;


    public static void setdefaluts(final Activity activity) {

        if (AdsManager.videoapp_AdmobInterstitial == null || AdsManager.videoapp_AdmobInterstitial.equals("")) {

            AdsManager.videoapp_AdmobNativeID = activity.getResources().getString(R.string.AdmobNativeID);
            AdsManager.videoapp_AdmobRewardID = activity.getResources().getString(R.string.AdmobRewardID);
            AdsManager.videoapp_AdmobInterstitial = activity.getResources().getString(R.string.AdmobInterstitial);
            AdsManager.videoapp_AdmobBanner = activity.getResources().getString(R.string.AdmobBanner);
            AdsManager.videoapp_AdmobAppId = activity.getResources().getString(R.string.AdmobAppId);
        }
    }

    public static void loadInterstitialAd(final Activity activity) {

        loadAdmobInterstitialAd(activity);


    }


    @SuppressLint("MissingPermission")
    public static void loadBannerAd(final Activity activity, final ViewGroup adContainer) {
        if (AdsManager.NUMBER_OF_BANNER_ADS_SHOWN < AdsManager.NUMBER_OF_BANNER_ADS_PER_SESSION) {
            AdsManager.NUMBER_OF_BANNER_ADS_SHOWN++;

            com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(activity);
            adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
            adView.setAdUnitId(videoapp_AdmobBanner);
            adView.loadAd(new AdRequest.Builder().build());
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    try {
                        adContainer.setVisibility(View.GONE);
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
                        adContainer.setVisibility(View.VISIBLE);
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
            adContainer.addView(adView);

        } else {
            adContainer.setVisibility(View.GONE);
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);

            AlertDialog.Builder cacheSIzeAlert = new AlertDialog.Builder(context);
            cacheSIzeAlert.setTitle("Cache Cleared");
            cacheSIzeAlert.setMessage("Cache has been cleared");
            final AlertDialog cacheDialog = cacheSIzeAlert.create();
            cacheSIzeAlert.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());


            cacheDialog.show();
        } catch (Exception e) {
            Log.d("error", "" + e.getLocalizedMessage());

        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < (children != null ? children.length : 0); i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }


    public static void showAdmobInterstitialAd(final Context context, FullScreenContentCallback contentCallback) {

        try {
            Random random = new Random();
            int a = random.nextInt(2);
            //  if (a==1 ) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) context);
                AdsManager.NUMBER_OF_INTERSTISHAL_ADS_SHOWN++;

                mInterstitialAd.setFullScreenContentCallback(contentCallback);

            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
            loadAdmobInterstitialAd(context);
            //  }
        } catch (Exception ignored) {
        }
    }


    public static void loadAdmobInterstitialAd(final Context context) {


        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(context, videoapp_AdmobInterstitial, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                if (AdsManager.NUMBER_OF_INTERSTISHAL_ADS_SHOWN < AdsManager.NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION) {
                    mInterstitialAd = interstitialAd;

                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });


    }


    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        try {


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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public static void loadAdmobNativeAd(Context context, FrameLayout frameLayout) {
        try {


            AdLoader.Builder builder = new AdLoader.Builder(context, videoapp_AdmobNativeID);
            builder.forNativeAd(nativeAd -> {
                // Assumes you have a placeholder FrameLayout in your View layout
                // (with id fl_adplaceholder) where the ad is to be placed.

                // Assumes that your ad layout is in a file call native_ad_layout.xml
                // in the res/layout folder
                @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater()
                        .inflate(R.layout.layout_native_ads, null);
                // This method sets the text, images and the native ad, etc into the ad
                // view.
                populateNativeAdView(nativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadBannerAdsAdapter(Context context, SliderView sliderView) {
        try {
            AdapterBannerAdsSliderRecyclerView adapterBannerAdsRecyclerView = new AdapterBannerAdsSliderRecyclerView(context);
            sliderView.setSliderAdapter(adapterBannerAdsRecyclerView);
            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderView.startAutoCycle();
        } catch (Exception e) {
            e.printStackTrace();
            sliderView.setVisibility(View.GONE);
        }

    }


    public static void loadVideoAdAdmob(final Activity activity, FullScreenContentCallback fullScreenContentCallback) {
        try {
            SharedPreferences prefs = activity.getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE);
            String nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.


            if (nn != null && nn.equals("nnn") && Constants.show_Ads) {
                System.out.println("ads working");

                AdRequest adRequest = new AdRequest.Builder().build();
                if (!activity.isFinishing()) {
                    RewardedAd.load(activity, AdsManager.videoapp_AdmobRewardID,
                            adRequest, new RewardedAdLoadCallback() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Handle the error.
                                    Log.d("TAG", loadAdError.getMessage());
                                    mRewardedAd = null;
                                }

                                @Override
                                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                    mRewardedAd = rewardedAd;
                                    Log.d("TAG", "Ad was loaded.");
                                    mRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                                }
                            });
                }

            }
        } catch (Exception ignored) {
        }

    }

    public static void showVideoAdAdmob(final Activity activity, OnUserEarnedRewardListener onUserEarnedRewardListener) {
        try {
            SharedPreferences prefs = activity.getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE);
            String nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.


            if (nn != null && nn.equals("nnn") && Constants.show_Ads) {

                if (mRewardedAd != null && !activity.isFinishing()) {
                    mRewardedAd.show(activity, onUserEarnedRewardListener);
                } else {
                    Log.d("TAG", "The rewarded ad wasn't ready yet.");
                    Toast.makeText(activity, activity.getResources().getString(R.string.videonotavaliabl), Toast.LENGTH_SHORT).show();

                }

            }
        } catch (Exception ignored) {
        }
    }


}


///TODO adsmanager fb ads
//public class AdsManager {
//
//    //TODO change this if you want to limit interstishal ads
//    public static int NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION = 12;
//    //TODO dont touch this
//    public static int NUMBER_OF_INTERSTISHAL_ADS_SHOWN = 0;
//
//
//    //TODO change this if you want to limit banner ads
//    public static int NUMBER_OF_BANNER_ADS_PER_SESSION = 5;
//    //TODO dont touch this
//    public static int NUMBER_OF_BANNER_ADS_SHOWN = 0;
//    static com.facebook.ads.AdView fanAdView;
//
//    static com.facebook.ads.NativeAd nativeAd;
//
//    static com.facebook.ads.InterstitialAd fanInterstitialAd;
//    private int retryAttempt;
//    private int counter = 1;
//
//    //Manage Ads ID - FACEBOOK
//    public static final String FACEBOOK_BANNER_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
//    public static final String FACEBOOK_INTER_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
//    public static final String FACEBOOK_NATIVE_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
//
//    @SuppressLint("MissingPermission")
//    public static void loadBannerAd(final Activity activity, final ViewGroup adContainer) {
//        if (AdsManager.NUMBER_OF_BANNER_ADS_SHOWN < AdsManager.NUMBER_OF_BANNER_ADS_PER_SESSION) {
//            AdsManager.NUMBER_OF_BANNER_ADS_SHOWN++;
//
//
//            fanAdView = new com.facebook.ads.AdView(activity, FACEBOOK_BANNER_ID, AdSize.BANNER_HEIGHT_50);
//            // Add the ad view to your activity layout
//            adContainer.addView(fanAdView);
//            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
//                @Override
//                public void onError(Ad ad, AdError adError) {
//                    adContainer.setVisibility(View.GONE);
//                    Log.d("TAG", "Failed to load Audience Network : " + adError.getErrorMessage() + " "  + adError.getErrorCode());
//                }
//
//                @Override
//                public void onAdLoaded(Ad ad) {
//                    adContainer.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onAdClicked(Ad ad) {
//
//                }
//
//                @Override
//                public void onLoggingImpression(Ad ad) {
//
//                }
//            };
//            com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig().withAdListener(adListener).build();
//            fanAdView.loadAd(loadAdConfig);
//
//        } else {
//            adContainer.setVisibility(View.GONE);
//        }
//    }
//
//
//
//    public static void showFacebookInterstitialAd(final Context context) {
//
//        try {
//            if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
//                if (AdsManager.NUMBER_OF_INTERSTISHAL_ADS_SHOWN < AdsManager.NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION) {
//                    fanInterstitialAd.show();
//                }
//            }
//        } catch (Exception ignored) {
//        }
//    }
//
//
//    public static void loadFacebookInterstitialAd(final Context context) {
//
//        fanInterstitialAd = new com.facebook.ads.InterstitialAd(context, FACEBOOK_INTER_ID);
//        InterstitialAdListener adListener = new InterstitialAdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                Log.d("TAG", "FAN Interstitial Ad loaded...");
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//
//            }
//
//            @Override
//            public void onInterstitialDisplayed(Ad ad) {
//
//            }
//
//            @Override
//            public void onInterstitialDismissed(Ad ad) {
//                fanInterstitialAd.loadAd();
//            }
//        };
//
//        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd.buildLoadAdConfig().withAdListener(adListener).build();
//        fanInterstitialAd.loadAd(loadAdConfig);
//
//
//    }
//
//
//    @SuppressLint("MissingPermission")
//    public static void loadFacebookNativeAd(Context context, LinearLayout frameLayout) {
//        try {
//
//            nativeAd = new com.facebook.ads.NativeAd(context, FACEBOOK_NATIVE_ID);
//
//
//            NativeAdListener nativeAdListener = new NativeAdListener() {
//                @Override
//                public void onMediaDownloaded(Ad ad) {
//
//                }
//
//                @Override
//                public void onError(Ad ad, AdError adError) {
//
//                }
//
//                @Override
//                public void onAdLoaded(Ad ad) {
//                    // Render the Native Ad Template
//                    View adView = com.facebook.ads.NativeAdView.render(context, nativeAd);
//
//                    // Add the Native Ad View to your ad container.
//                    // The recommended dimensions for the ad container are:
//                    // Width: 280dp - 500dp
//                    // Height: 250dp - 500dp
//                    // The template, however, will adapt to the supplied dimensions.
//                    frameLayout.addView(adView, new LinearLayout.LayoutParams(MATCH_PARENT, 800));
//                }
//
//                @Override
//                public void onAdClicked(Ad ad) {
//
//                }
//
//                @Override
//                public void onLoggingImpression(Ad ad) {
//
//                }
//            };
//
//            // Initiate a request to load an ad.
//            nativeAd.loadAd(
//                    nativeAd.buildLoadAdConfig()
//                            .withAdListener(nativeAdListener)
//                            .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
//                            .build());
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//}


//todo applovin ads
//public class AdsManager {
//
//    //TODO change this if you want to limit interstishal ads
//    public static int NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION = 12;
//    //TODO dont touch this
//    public static int NUMBER_OF_INTERSTISHAL_ADS_SHOWN = 0;
//
//
//    //TODO change this if you want to limit banner ads
//    public static int NUMBER_OF_BANNER_ADS_PER_SESSION = 5;
//    //TODO dont touch this
//    public static int NUMBER_OF_BANNER_ADS_SHOWN = 0;
//
//
//    public static MaxAdView adView;
//    public static MaxInterstitialAd interstitialAd;
//    public static int retryAttempt;
//
//
//    public static MaxRewardedAd rewardedAd;
//    public static int retryAttemptvideo;
//
//    public static MaxNativeAdLoader nativeAdLoader;
//    public static MaxAd nativeAd;
//
//
//    @SuppressLint("MissingPermission")
//    public static void loadBannerAd(final Activity activity, final ViewGroup adContainer, boolean slamm) {
//        if (AdsManager.NUMBER_OF_BANNER_ADS_SHOWN < AdsManager.NUMBER_OF_BANNER_ADS_PER_SESSION) {
//            AdsManager.NUMBER_OF_BANNER_ADS_SHOWN++;
//
//            adView = new MaxAdView("4fce746e596e78a4", activity);
//            adView.setListener(new MaxAdViewAdListener() {
//                @Override
//                public void onAdExpanded(MaxAd ad) {
//
//                }
//
//                @Override
//                public void onAdCollapsed(MaxAd ad) {
//
//                }
//
//                @Override
//                public void onAdLoaded(MaxAd ad) {
//
//                }
//
//                @Override
//                public void onAdDisplayed(MaxAd ad) {
//
//                }
//
//                @Override
//                public void onAdHidden(MaxAd ad) {
//
//                }
//
//                @Override
//                public void onAdClicked(MaxAd ad) {
//
//                }
//
//                @Override
//                public void onAdLoadFailed(String adUnitId, MaxError error) {
//
//                }
//
//                @Override
//                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
//
//                }
//            });
//
//            // Stretch to the width of the screen for banners to be fully functional
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//
//            // Get the adaptive banner height.
//            int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(activity).getHeight();
//            int heightPx = AppLovinSdkUtils.dpToPx(activity, heightDp);
//
//            adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
//            adView.setExtraParameter("adaptive_banner", "true");
//            adContainer.addView(adView);
//
//            // Load the ad
//            adView.loadAd();
//
//
//        } else {
//            adContainer.setVisibility(View.GONE);
//        }
//    }
//
//
//    public static void loadInterstitialAd(final Context context) {
//
//
//        if (AdsManager.NUMBER_OF_INTERSTISHAL_ADS_SHOWN < AdsManager.NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION) {
//
//            interstitialAd = new MaxInterstitialAd("793894cce69278b7", (Activity) context);
//            interstitialAd.setListener(new MaxAdListener() {
//                @Override
//                public void onAdLoaded(MaxAd ad) {
//                    // Reset retry attempt
//                    retryAttempt = 0;
//
//                }
//
//                @Override
//                public void onAdLoadFailed(final String adUnitId, final MaxError error) {
//                    // Interstitial ad failed to load
//                    // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)
//
//                    retryAttempt++;
//                    long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            interstitialAd.loadAd();
//                        }
//                    }, delayMillis);
//                }
//
//                @Override
//                public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error) {
//                    // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
//                    interstitialAd.loadAd();
//                }
//
//                @Override
//                public void onAdDisplayed(final MaxAd maxAd) {
//                }
//
//                @Override
//                public void onAdClicked(final MaxAd maxAd) {
//                }
//
//                @Override
//                public void onAdHidden(final MaxAd maxAd) {
//                    // Interstitial ad is hidden. Pre-load the next ad
//                    interstitialAd.loadAd();
//                }
//            });
//
//            // Load the first ad
//            interstitialAd.loadAd();
//        }
//
//    }
//    public static void showInterstitialAd(final Context context) {
//
//
//        if (AdsManager.NUMBER_OF_INTERSTISHAL_ADS_SHOWN < AdsManager.NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION) {
//
//
//            if (interstitialAd.isReady()) {
//                interstitialAd.showAd();
//            }
//
//        }
//
//    }
//
//
//    public static void loadVideoAdAdmob(final Activity activity) {
//        try {
//            SharedPreferences prefs = activity.getSharedPreferences("whatsapp_pref",
//                    Context.MODE_PRIVATE);
//            String nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.
//
//
//            if (nn != null && nn.equals("nnn") && Constants.show_Ads) {
//                System.out.println("ads working");
//
//
//                if (!activity.isFinishing()) {
//                    rewardedAd = MaxRewardedAd.getInstance("0d4dacca795e2e5f", activity);
//                    rewardedAd.setListener(new MaxRewardedAdListener() {
//                        // MAX Ad Listener
//                        @Override
//                        public void onAdLoaded(final MaxAd maxAd) {
//                            // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'
//
//                            // Reset retry attempt
//                            retryAttemptvideo = 0;
//                        }
//
//                        @Override
//                        public void onAdLoadFailed(final String adUnitId, final MaxError error) {
//                            // Rewarded ad failed to load
//                            // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
//
//                            retryAttemptvideo++;
//                            long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttemptvideo)));
//
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    rewardedAd.loadAd();
//                                }
//                            }, delayMillis);
//                        }
//
//                        @Override
//                        public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error) {
//                            // Rewarded ad failed to display. We recommend loading the next ad
//                            rewardedAd.loadAd();
//                        }
//
//                        @Override
//                        public void onAdDisplayed(final MaxAd maxAd) {
//                        }
//
//                        @Override
//                        public void onAdClicked(final MaxAd maxAd) {
//                        }
//
//                        @Override
//                        public void onAdHidden(final MaxAd maxAd) {
//                            // rewarded ad is hidden. Pre-load the next ad
//                            rewardedAd.loadAd();
//                        }
//
//                        @Override
//                        public void onRewardedVideoStarted(final MaxAd maxAd) {
//                        }
//
//                        @Override
//                        public void onRewardedVideoCompleted(final MaxAd maxAd) {
//                        }
//
//                        @Override
//                        public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward) {
//                            // Rewarded ad was displayed and user should receive the reward
//                        }
//                    });
//
//                    rewardedAd.loadAd();
//                }
//
//            }
//        } catch (Exception ignored) {
//        }
//
//    }
//
//    public static void showVideoAdAdmob(final Activity activity) {
//        try {
//            SharedPreferences prefs = activity.getSharedPreferences("whatsapp_pref",
//                    Context.MODE_PRIVATE);
//            String nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.
//
//
//            if (nn != null && nn.equals("nnn") && Constants.show_Ads) {
//
//                if (!activity.isFinishing()) {
//
//                    if (rewardedAd.isReady()) {
//                        rewardedAd.showAd();
//                    }
//                } else {
//                    Log.d("TAG", "The rewarded ad wasn't ready yet.");
//                    Toast.makeText(activity, activity.getResources().getString(R.string.videonotavaliabl), Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        } catch (Exception ignored) {
//        }
//    }
//
//    public static void loadNativeAds(final Activity activity, final FrameLayout adContainer) {
//        try {
//            SharedPreferences prefs = activity.getSharedPreferences("whatsapp_pref",
//                    Context.MODE_PRIVATE);
//            String nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.
//
//
//            if (nn != null && nn.equals("nnn") && Constants.show_Ads) {
//
//                if (!activity.isFinishing()) {
//
//                    nativeAdLoader = new MaxNativeAdLoader("0d4dacca795e2e5f", activity);
//                    nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
//                        @Override
//                        public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
//                            // Clean up any pre-existing native ad to prevent memory leaks.
//                            if (nativeAd != null) {
//                                nativeAdLoader.destroy(nativeAd);
//                            }
//
//                            // Save ad for cleanup.
//                            nativeAd = ad;
//
//                            // Add ad view to view.
//                            adContainer.removeAllViews();
//                            adContainer.addView(nativeAdView);
//                        }
//
//                        @Override
//                        public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
//                            // We recommend retrying with exponentially higher delays up to a maximum delay
//                        }
//
//                        @Override
//                        public void onNativeAdClicked(final MaxAd ad) {
//                            // Optional click callback
//                        }
//                    });
//
//                    nativeAdLoader.loadAd();
//
//
//                } else {
//                    Log.d("TAG", "The rewarded ad wasn't ready yet.");
//                    Toast.makeText(activity, activity.getResources().getString(R.string.videonotavaliabl), Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        } catch (Exception ignored) {
//        }
//    }
//
//
//}

