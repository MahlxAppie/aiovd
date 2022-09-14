package com.infusiblecoder.allinonevideodownloader.extraFeatures.videolivewallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

public class CinimaWallService extends WallpaperService {
    public static String ACTION = "action";
    private static final String is_battery_saver = "is_battery_saver";
    private static final String is_plzay_begin = "is_plzay_begin";
    private static final String videoAudio = "vAudi";
    private static final String videoPath = "viPath";

    @Override
    public Engine onCreateEngine() {
        return new VideoWallpaperEngine();
    }

    public void setVidSource(Context context, String str) {
        SharedPrefUtils.saveData(context, videoPath, str);
        Intent intent = new Intent(getPackageName());
        intent.putExtra(ACTION, 1);
        context.sendBroadcast(intent);
    }

    public boolean isEnableVideoAudio(Context context) {
        return SharedPrefUtils.getBooleanData(context, videoAudio);
    }

    public String getVideoSource(Context context) {
        return SharedPrefUtils.getStringData(context, videoPath);
    }

    public boolean isPlayB(Context context) {
        return SharedPrefUtils.getBooleanData(context, is_plzay_begin);
    }

    public void setEnableVideoAudio(Context context, boolean z) {
        try {
            SharedPrefUtils.saveData(context, videoAudio, z);
            Intent intent = new Intent(context.getPackageName());
            intent.putExtra(ACTION, 2);
            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setPlayB(Context context, boolean z) {
        SharedPrefUtils.saveData(context, is_plzay_begin, !isPlayB(context));
    }

    public void setPlayBatterySaver(Context context, boolean z) {
        SharedPrefUtils.saveData(context, is_battery_saver, !isBatterySaver(context));
    }

    public boolean isBatterySaver(Context context) {
        return SharedPrefUtils.getBooleanData(context, is_battery_saver);
    }

    class VideoWallpaperEngine extends Engine {
        public MediaPlayer mMediaPlayer;
        private BroadcastReceiver mVideoReceiver;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            try {
                IntentFilter intentFilter = new IntentFilter(getPackageName());
                this.mVideoReceiver = new VideoVoiceControlReceiver();
                CinimaWallService.this.registerReceiver(this.mVideoReceiver, intentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDestroy() {
            try {
                CinimaWallService.this.unregisterReceiver(this.mVideoReceiver);
                super.onDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onVisibilityChanged(boolean z) {
            try {
                if (!z) {
                    this.mMediaPlayer.pause();
                    return;
                }
                CinimaWallService cinimaWallService = CinimaWallService.this;
                if (cinimaWallService.isPlayB(cinimaWallService.getApplicationContext())) {
                    this.mMediaPlayer.seekTo(0);
                    this.mMediaPlayer.start();
                    return;
                }
                this.mMediaPlayer.start();
            } catch (Exception r) {
                r.printStackTrace();
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
            try {
                CinimaWallService cinimaWallService = CinimaWallService.this;
                if (!TextUtils.isEmpty(cinimaWallService.getVideoSource(cinimaWallService.getApplicationContext()))) {

                    this.mMediaPlayer = new MediaPlayer();
                    this.mMediaPlayer.setSurface(surfaceHolder.getSurface());
                    float f = CinimaWallService.this.isEnableVideoAudio(CinimaWallService.this.getApplicationContext()) ? 1.0f : 0.0f;
                    this.mMediaPlayer.setVolume(f, f);
                    this.mMediaPlayer.setDataSource(CinimaWallService.this.getVideoSource(CinimaWallService.this.getApplicationContext()));
                    this.mMediaPlayer.setLooping(true);
                    this.mMediaPlayer.prepare();
                    this.mMediaPlayer.start();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            super.onSurfaceChanged(surfaceHolder, i, i2, i3);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder surfaceHolder) {
            super.onSurfaceDestroyed(surfaceHolder);
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                this.mMediaPlayer = null;
            }
        }

        class VideoVoiceControlReceiver extends BroadcastReceiver {
            VideoVoiceControlReceiver() {
            }

            @Override
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra(CinimaWallService.ACTION, 0);
                if (intExtra == 1) {
                    if (VideoWallpaperEngine.this.mMediaPlayer != null) {
                        try {
                            VideoWallpaperEngine.this.mMediaPlayer.reset();
                            VideoWallpaperEngine.this.mMediaPlayer.setDataSource(CinimaWallService.this.getVideoSource(CinimaWallService.this.getApplicationContext()));
                            VideoWallpaperEngine.this.mMediaPlayer.prepare();
                            return;
                        } catch (Exception e) {
                            Log.d("Log", e.getLocalizedMessage());
                        }
                    }
                } else if (intExtra != 2) {
                    return;
                }
                if (VideoWallpaperEngine.this.mMediaPlayer != null) {
                    float f = CinimaWallService.this.isEnableVideoAudio(CinimaWallService.this.getApplicationContext()) ? 1.0f : 0.0f;
                    VideoWallpaperEngine.this.mMediaPlayer.setVolume(f, f);
                }
            }
        }
    }
}
