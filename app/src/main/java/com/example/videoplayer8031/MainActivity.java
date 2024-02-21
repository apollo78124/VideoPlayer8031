package com.example.videoplayer8031;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ExoPlayer ";
    private static final String VIDEO_URI =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4";
    private final Player.Listener playbackStateListener = new PlaybackStateListener();
    private ExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Handler handler = new Handler();
            BandwidthMeter bandwidthMeter =
                    new DefaultBandwidthMeter.Builder(MainActivity.this).build();
            bandwidthMeter.addEventListener(
                    handler,
                    (elapsedMs, bytesTransferred, bitrateEstimate) -> {
                        Log.d(TAG, "bytesTransferred, elapsedMs, bitrateEstimate = " +
                                bytesTransferred + ", " + elapsedMs + ", " + bitrateEstimate);
                    });
            DefaultLoadControl defaultLoadControl = new DefaultLoadControl.Builder().
                    setBufferDurationsMs(20000, 20000, 1000, 20000).build();
            player = new ExoPlayer.Builder(this).setLooper(Looper.getMainLooper()).
                    setBandwidthMeter(bandwidthMeter).setLoadControl(defaultLoadControl).build();
            StyledPlayerView playerView = findViewById(R.id.styledPlayerView);
            playerView.setPlayer(player);
            player.addListener(playbackStateListener);
            player.setMediaItem(MediaItem.fromUri(VIDEO_URI));
            player.prepare();
            player.setPlayWhenReady(true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.removeListener(playbackStateListener);
        player.release();
    }
    private static class PlaybackStateListener implements Player.Listener {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            try {
                String state = "";
                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        state = "IDLE";
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        state = "BUFFERING";
                        break;
                    case ExoPlayer.STATE_READY:
                        state = "READY";
                        break;
                    case ExoPlayer.STATE_ENDED:
                        state = "ENDED";
                        break;
                }
                Log.d(TAG, "Current State: " + state);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (isPlaying) {
                Log.e(TAG, "Playback Active");
            } else {
                Log.e(TAG, "Playback Stopped");
            }
        }
    }
}