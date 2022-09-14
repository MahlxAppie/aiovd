package com.infusiblecoder.allinonevideodownloader.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.infusiblecoder.allinonevideodownloader.R;
import com.infusiblecoder.allinonevideodownloader.databinding.ActivityGalleryBinding;
import com.infusiblecoder.allinonevideodownloader.fragments.GalleryAudiosFragment;
import com.infusiblecoder.allinonevideodownloader.fragments.GalleryImagesFragment;
import com.infusiblecoder.allinonevideodownloader.fragments.GalleryStatusSaver;
import com.infusiblecoder.allinonevideodownloader.fragments.GalleryVideosFragment;
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager;
import com.infusiblecoder.allinonevideodownloader.utils.Constants;
import com.infusiblecoder.allinonevideodownloader.utils.iUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class GalleryActivity extends AppCompatActivity {

    private ActivityGalleryBinding binding;
    private boolean isChecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            final MyAdapter adapter = new MyAdapter(getSupportFragmentManager(), 4);
            binding.viewpagergallery.setAdapter(adapter);


            SharedPreferences prefs = getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE);
            String nn = prefs.getString("inappads", "nnn");
            boolean istutshownlong = prefs.getBoolean("istutshownlong", false);
            if (!istutshownlong) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                builder.setTitle(R.string.newop);
                builder.setMessage(R.string.singleclickandlongclick);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.close, (dialog, which) -> {
                    SharedPreferences sharedPreference = getSharedPreferences("whatsapp_pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putBoolean("istutshownlong", true);
                    editor.apply();
                });
                builder.show();
            }


            if (Constants.show_Ads) {
                if (nn.equals("nnn")) {
                    AdsManager.loadBannerAdsAdapter(this, binding.bannerContainer);
                } else {
                    binding.bannerContainer.setVisibility(View.GONE);
                }
            }

            binding.bottomNavBargallery.setOnItemSelectedListener((OnItemSelectedListener) i -> {
                binding.viewpagergallery.setCurrentItem(i);
                return false;
            });

            binding.viewpagergallery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    binding.bottomNavBargallery.setItemActiveIndex(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gallery_context_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.enablebio);
        SwitchCompat mySwitch = (SwitchCompat) menuItem.getActionView();
        mySwitch.setChecked(iUtils.getIsBioLoginEnabled(GalleryActivity.this));

        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            GalleryActivity.this.isChecked = mySwitch.isChecked();
            mySwitch.setChecked(isChecked);

            SharedPreferences sharedPreference = getSharedPreferences("whatsapp_pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putBoolean("isBio", GalleryActivity.this.isChecked);
            editor.apply();

            Toast.makeText(
                    GalleryActivity.this,
                    "Lock Enabled " + GalleryActivity.this.isChecked,
                    Toast.LENGTH_SHORT
            ).show();
        });
        return true;

    }


    public class MyAdapter extends FragmentPagerAdapter {

        int totalTabs;

        public MyAdapter(FragmentManager fm, int totalTabs) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.totalTabs = totalTabs;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new GalleryVideosFragment();
                case 1:
                    return new GalleryStatusSaver();
                case 2:
                    return new GalleryImagesFragment();
                case 3:
                    return new GalleryAudiosFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }


}
