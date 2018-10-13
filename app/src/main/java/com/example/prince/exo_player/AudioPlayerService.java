package com.example.prince.exo_player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.example.prince.exo_player.C.MEDIA_SESSION_TAG;
import static com.example.prince.exo_player.C.PLAYBACK_CHANNEL_ID;
import static com.example.prince.exo_player.C.PLAYBACK_NOTIFICATION_ID;
import static com.example.prince.exo_player.Samples.SAMPLES;



public class AudioPlayerService extends Service {

   private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;

    private MediaSessionConnector mediaSessionConnector;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;

        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "AudioDemo"));
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();

        for(Samples.Sample sample: SAMPLES) {
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(sample.uri);
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        player.prepare(concatenatingMediaSource);
        player.setPlayWhenReady(true);

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(context, PLAYBACK_CHANNEL_ID, R.string.playback_channel_name, PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return SAMPLES[player.getCurrentWindowIndex()].title;
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(context,MainActivity.class);
                        return PendingIntent.getActivity(context, 0, intent ,PendingIntent.FLAG_UPDATE_CURRENT);

                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return SAMPLES[player.getCurrentWindowIndex()].description;
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return Samples.getBitmap(
                                context,SAMPLES[player.getCurrentWindowIndex()].bitmapResource);
                    }
                }
        );
        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId,notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                stopSelf();
            }
        });
        playerNotificationManager.setPlayer(player);

        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(int windowIndex) {
                return Samples.getMediaDescription(context, SAMPLES[windowIndex]);
            }
        });
        mediaSessionConnector.setPlayer(player, null);
    }

   @Override
    public void onDestroy(){
        mediaSession.release();
        mediaSessionConnector.setPlayer(null,null);
        playerNotificationManager.setPlayer(null);
        player.release();
        player=null;

   }

   @Nullable
    @Override
    public IBinder onBind(Intent intent){return null;}

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){return START_STICKY;}

}

