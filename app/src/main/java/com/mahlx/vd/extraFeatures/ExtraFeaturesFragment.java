package com.infusiblecoder.allinonevideodownloader.extraFeatures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.activities.InstagramBulkDownloaderSearch;
import com.infusiblecoder.allinonevideodownloader.extraFeatures.videolivewallpaper.MainActivityLivewallpaper;
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.FacebookPrivateWebview;
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager;
import com.infusiblecoder.allinonevideodownloader.utils.Constants;


public class ExtraFeaturesFragment extends Fragment {
    private String nn = "nnn";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_extra_features, container, false);


        if (isAdded()) {
            AdsManager.loadInterstitialAd(requireActivity());

            view.findViewById(R.id.btn_one)
                    .setOnClickListener(v -> {

                        startActivity(new Intent(requireActivity(), MainActivityLivewallpaper.class));

                        showAdmobAds();

                    });

            view.findViewById(R.id.facebookprivate)
                    .setOnClickListener(v -> {

                        startActivity(new Intent(requireActivity(), FacebookPrivateWebview.class));

                        showAdmobAds();

                    });


            if (Constants.show_earning_card_in_extrafragment) {
                view.findViewById(R.id.card_extra).setVisibility(View.VISIBLE);

            } else {
                view.findViewById(R.id.card_extra).setVisibility(View.GONE);

            }

            view.findViewById(R.id.earnmoneycard)
                    .setOnClickListener(v -> {

                        startActivity(new Intent(requireActivity(), EarningAppWebviewActivity.class));

                        showAdmobAds();
//TODO
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                            Intent i = new Intent(requireActivity(), FloatingWidgetDownload.class);
//
//
//                            requireActivity().startService(i);
//                        } else {
//                            Toast.makeText(requireActivity(),
//                                    "avaliable for android LOLLIPOP or above", Toast.LENGTH_SHORT).show();
//
//                        }
                    });

            view.findViewById(R.id.instabulkcard)
                    .setOnClickListener(v -> {

                        startActivity(new Intent(requireActivity(), InstagramBulkDownloaderSearch.class));

                        showAdmobAds();

                    });


            SharedPreferences prefs = requireActivity().getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE);
            nn = prefs.getString("inappads", "nnn");


        }
        return view;
    }


    private void showAdmobAds() {
        if (Constants.show_Ads) {
            if (nn.equals("nnn")) {

                AdsManager.showAdmobInterstitialAd(requireActivity(), new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        AdsManager.loadInterstitialAd(requireActivity());

                    }
                });

            }
        }
    }

}
