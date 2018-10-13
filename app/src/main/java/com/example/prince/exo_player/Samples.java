package com.example.prince.exo_player;

/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

public final class Samples {

    public static final class Sample {
        public final Uri uri;
        public final String mediaId;
        public final String title;
        public final String description;
        public final int bitmapResource;

        public Sample(
                String uri, String mediaId, String title, String description, int bitmapResource) {
            this.uri = Uri.parse(uri);
            this.mediaId = mediaId;
            this.title = title;
            this.description = description;
            this.bitmapResource = bitmapResource;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public static final Sample[] SAMPLES = new Sample[] {
            new Sample(
                    "https://media-audio.mio.to/by_artist/H/Harrdy%20Sandhu/Backbone%20%282017%29/1_1%20-%20Backbone-vbr-V5.mp3",
                    "audio_1",
                    "Jazz in Paris",
                    "Jazz for the masses",
                    R.drawable.album_art_1),
            new Sample(
                    "http://mp3load.info/song/Full%20Mp3%20Song/Bollywood%20Artist%20Collection%20Songs/Vidya%20Vox%20%20All%20Mp3%20Songs/Teri%20Khair%20Mangdi%20n%20Sandcastles%20-%20(Mashup%20Cover)%20Vidya%20Ft.%20Devender%20Singh/Teri%20Khair%20Mangdi%20n%20Sandcastles%20-%20(Mashup%20Cover)%20Vidya%20Ft.%20Devender%20Singh%20-%2064Kbps.mp3",
                    "audio_2",
                    "The messenger",
                    "Hipster guide to London",
                    R.drawable.album_art_2),
            new Sample(
                    "http://storage.googleapis.com/automotive-media/Talkies.mp3",
                    "audio_3",
                    "Talkies",
                    "If it talks like a duck and walks like a duck.",
                    R.drawable.album_art_3),
    };

    public static MediaDescriptionCompat getMediaDescription(Context context, Sample sample) {
        Bundle extras = new Bundle();
        Bitmap bitmap = getBitmap(context, sample.bitmapResource);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);
        return new MediaDescriptionCompat.Builder()
                .setMediaId(sample.mediaId)
                .setIconBitmap(bitmap)
                .setTitle(sample.title)
                .setDescription(sample.description)
                .setExtras(extras)
                .build();
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int bitmapResource) {
        return ((BitmapDrawable) context.getResources().getDrawable(bitmapResource)).getBitmap();
    }

}
