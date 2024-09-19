package com.homerours.musiccontrols;

import org.apache.cordova.CallbackContext;
import androidx.annotation.Nullable;
//import android.util.Log;

class CallbackUtils {

    @Nullable
    public static CallbackContext sendMessage(CallbackContext cb, String message, @Nullable String source,
            @Nullable String extra) {
        if (cb != null) {
            String cbStr = "{\"message\": \"" + message + "\"";
            if (source != null && source != "") {
                cbStr = cbStr + ", \"source\": \"" + source + "\"";
            }
            if (extra != null && extra != "") {
                cbStr = cbStr + ", \"extra\": \"" + extra + "\"";
            }
            cbStr = cbStr + "}";
            cb.success(cbStr);
        }
        return null;
    }

    @Nullable
    public static CallbackContext sendMessage(CallbackContext cb, String message, @Nullable String source) {
        return sendMessage(cb,message,source,null);
    }

    @Nullable
    public static CallbackContext sendMessage(CallbackContext cb, String message) {
        return sendMessage(cb,message,null,null);
    }

    public static String MUSIC_CONTROLS_PLAY = "music-controls-play";
    public static String MUSIC_CONTROLS_PAUSE = "music-controls-pause";
    public static String MUSIC_CONTROLS_PREVIOUS = "music-controls-previous";
    public static String MUSIC_CONTROLS_NEXT = "music-controls-next";
    public static String MUSIC_CONTROLS_SEEK_TO = "music-controls-seek-to";
    public static String MUSIC_CONTROLS_SET_SHUFFLE_MODE = "music-controls-set-shuffle-mode";
    public static String MUSIC_CONTROLS_TOGGLE_PLAY_PAUSE = "music-controls-toggle-play-pause";
    public static String MUSIC_CONTROLS_STOP = "music-controls-stop";
    public static String MUSIC_CONTROLS_FORWARD = "music-controls-forward";
    public static String MUSIC_CONTROLS_REWIND = "music-controls-rewind";
    public static String MUSIC_CONTROLS_UNKNOWN = "music-controls-unknown";

    public static String MUSIC_CONTROLS_DESTROY ="music-controls-destroy";
    public static String MUSIC_CONTROLS_STOP_LISTENING ="music-controls-stop-listening";

    public static String MUSIC_CONTROLS_HEADSET_UNPLUGGED = "music-controls-headset-unplugged";
    public static String MUSIC_CONTROLS_HEADSET_PLUGGED = "music-controls-headset-plugged";
    public static String MUSIC_CONTROLS_AUDIO_BECOMING_NOISY = "music-controls-audio-becoming-noisy";

    public static String MUSIC_CONTROLS_MEDIA_BUTTON = "music-controls-media-button";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_NEXT ="music-controls-media-button-next";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_PREVIOUS ="music-controls-media-button-previous";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_PAUSE ="music-controls-media-button-pause";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_PLAY ="music-controls-media-button-play";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_PLAY_PAUSE ="music-controls-media-button-play-pause";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_STOP ="music-controls-media-button-stop";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_FAST_FORWARD ="music-controls-media-button-fast-forward";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_REWIND ="music-controls-media-button-rewind";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_SKIP_BACKWARD ="music-controls-media-button-skip-backward";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_SKIP_FORWARD ="music-controls-media-button-skip-forward";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_STEP_BACKWARD ="music-controls-media-button-step-backward";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_STEP_FORWARD ="music-controls-media-button-step-forward";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_META_LEFT ="music-controls-media-button-meta-left";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_META_RIGHT ="music-controls-media-button-meta-right";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_MUSIC ="music-controls-media-button-music";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_VOLUME_UP ="music-controls-media-button-volume-up";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_VOLUME_DOWN ="music-controls-media-button-volume-down";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_VOLUME_MUTE ="music-controls-media-button-volume-mute";
    public static String MUSIC_CONTROLS_MEDIA_BUTTON_HEADSET_HOOK ="music-controls-media-button-headset-hook";

}