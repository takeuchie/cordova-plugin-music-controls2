package com.homerours.musiccontrols;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;
import android.util.Log;

import org.apache.cordova.CallbackContext;

/*
* The onMediaButtonEvent method is called when using external devices, such as bluetooth devices.
* The other methods like OnPlay, OnPause and etc are called from the notification itself.
* Couldn't find where the BroadcastReceiver class is used, but apparently it is used for wired phones.
*/

public class MediaSessionCallback extends MediaSessionCompat.Callback {

  private CallbackContext cb;

  public void setCallback(CallbackContext cb) {
    this.cb = cb;
  }

  @Override
  public void onPlay() {
    super.onPlay();
    cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_PLAY, "music-controls-media-button-play 1");
  }

  @Override
  public void onPause() {
    super.onPause();
    cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_PAUSE, "music-controls-media-button-pause 1");
  }

  @Override
  public void onSkipToNext() {
    super.onSkipToNext();
    cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_NEXT, "music-controls-media-button-next 1");
  }

  @Override
  public void onSkipToPrevious() {
    super.onSkipToPrevious();
    cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_PREVIOUS, "music-controls-media-button-previous 1");
  }

  @Override
  public void onSeekTo(long pos) {
    super.onSeekTo(pos);
    cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_SEEK_TO, "music-controls-media-button-seek-to",
        Long.toString(pos));
  }

  @Override
  public void onPlayFromMediaId(String mediaId, Bundle extras) {
    super.onPlayFromMediaId(mediaId, extras);
  }

  @Override
  public void onSetShuffleMode(int shuffleMode) {
    super.onSetShuffleMode(shuffleMode);
    //Log.w("MusicControls", "On Set Shuffle Mode");
    cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_SET_SHUFFLE_MODE,
        "music-controls-media-button-set-shuffle-mode", Integer.toString(shuffleMode));
  }

  @Override
  public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
    final KeyEvent event = (KeyEvent) mediaButtonIntent.getExtras().get(Intent.EXTRA_KEY_EVENT);
    //Log.w("MusicControls", "on Media Button Event");
    if (event == null) {
      return super.onMediaButtonEvent(mediaButtonIntent);
    }

    //Log.w("MusicControls", "on Media Button Event - Action: " + event.getAction());

    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      final int keyCode = event.getKeyCode();
      //Log.w("MusicControls", "on Media Button Event - Key Code: " + keyCode);
      switch (keyCode) {
        case KeyEvent.KEYCODE_MEDIA_PAUSE:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_PAUSE, "music-controls-media-button-pause");
          break;
        case KeyEvent.KEYCODE_MEDIA_PLAY:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_PLAY, "music-controls-media-button-play");
          break;
        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_PREVIOUS,
              "music-controls-media-button-previous");
          break;
        case KeyEvent.KEYCODE_MEDIA_NEXT:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_NEXT, "music-controls-media-button-next");
          break;
        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_TOGGLE_PLAY_PAUSE,
              "music-controls-media-button-play-pause");
          break;
        case KeyEvent.KEYCODE_MEDIA_STOP:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_STOP, "music-controls-media-button-stop");
          break;
        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_FORWARD,
              "music-controls-media-button-forward");
          break;
        case KeyEvent.KEYCODE_MEDIA_REWIND:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_REWIND, "music-controls-media-button-rewind");
          break;
        default:
          cb = CallbackUtils.sendMessage(cb, CallbackUtils.MUSIC_CONTROLS_UNKNOWN,
              "music-controls-media-button-unknown", Integer.toString(keyCode));
          return super.onMediaButtonEvent(mediaButtonIntent);
      }
    }

    return true;
  }
}