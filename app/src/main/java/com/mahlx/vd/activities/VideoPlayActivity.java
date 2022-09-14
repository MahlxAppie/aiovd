package com.infusiblecoder.allinonevideodownloader.activities;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.infusiblecoder.allinonevideodownloader.databinding.ActivityVideoPlayBinding;
import com.universalvideoview.UniversalVideoView;

public class VideoPlayActivity extends AppCompatActivity {

    String urls;
    PictureInPictureParams.Builder pictureInPictureParamsBuilder;
    private ActivityVideoPlayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setupPlayer();

        binding.imgPictureinpicture.setOnClickListener(v -> startPictureInPictureFeature());


        binding.back.setOnClickListener(view1 -> VideoPlayActivity.this.onBackPressed());


        binding.storyVideo.setOnClickListener(view12 -> {
            binding.storyVideo.setVisibility(View.GONE);
            binding.videoView.start();
        });

    }


    void setupPlayer() {
        try {
            urls = getIntent().getStringExtra("videourl");
            binding.videoView.setMediaController(binding.mediaController);
            binding.videoView.setVideoURI(Uri.parse(urls));
            binding.videoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {

                @Override
                public void onScaleChange(boolean isFullscreen) {

                }

                @Override
                public void onPause(MediaPlayer mediaPlayer) {
                    binding.storyVideo.setVisibility(View.VISIBLE);

                }

                @Override
                public void onStart(MediaPlayer mediaPlayer) {

                }

                @Override
                public void onBufferingStart(MediaPlayer mediaPlayer) {// steam start loading
                }

                @Override
                public void onBufferingEnd(MediaPlayer mediaPlayer) {// steam end loading
                }

            });

            binding.storyVideo.setVisibility(View.GONE);
            binding.videoView.start();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {

        urls = getIntent().getStringExtra("videourl");

        setupPlayer();

        super.onNewIntent(intent);
    }

    private void startPictureInPictureFeature() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                pictureInPictureParamsBuilder = new PictureInPictureParams.Builder();

                Rational aspectRatio = new Rational(16, 9);

                pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();

                enterPictureInPictureMode(pictureInPictureParamsBuilder.build());

            } else {

                Toast.makeText(VideoPlayActivity.this, "Picture in picture mode is not supported in this device", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(VideoPlayActivity.this, "Picture in picture mode error", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!isInPictureInPictureMode()) {
                Rational aspectRatio = new Rational(binding.videoView.getWidth(), binding.videoView.getHeight());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
                        enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
                    } catch (Exception e) {
                        //    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode,
                                              Configuration newConfig) {
        if (isInPictureInPictureMode) {


        } else {


        }
    }


    public void onDestroy() {
        super.onDestroy();
        binding.videoView.closePlayer();

    }

}
