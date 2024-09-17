package com.homerours.musiccontrols;

import org.apache.cordova.CallbackContext;
import androidx.annotation.Nullable;
import android.util.Log;

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

}