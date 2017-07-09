package com.example.android.musicmap;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

/**
 * Created by 申源春 on 2017/7/8.
 */

public  class MediaStyleHelper{
    // Given a media session and its context (usually the component containing the session)
// Create a NotificationCompat.Builder

    public static NotificationCompat.Builder from(Context context, MediaSessionCompat mediaSession) {
// Get the session's metadata
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        //TODO: context
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder
// Add the metadata for the currently playing track
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())

// Enable launching the player by clicking the notification
                .setContentIntent(controller.getSessionActivity())

// Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_STOP))

// Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

// Add an app icon and set its accent color
// Be careful about the color
                .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                .setColor(ContextCompat.getColor(context, R.color.white))

// Add a pause button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_pause_black_24dp, context.getString(R.string.pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))

// Take advantage of MediaStyle features
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)
// Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_STOP)));

        return builder;
    }
}
