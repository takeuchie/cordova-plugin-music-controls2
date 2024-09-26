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
* Couldn't find where the BroadcastReceiver class is used, but apparently it is used for wired headsets (?).
*/

public class MediaSessionCallback extends MediaSessionCompat.Callback {

  private CallbackContext cb;

  public void setCallback(CallbackContext cb) {
    this.cb = cb;
  }

  public void sendMessage(String message) {
    sendMessage(message, null);
  }

  public void sendMessage(String message, String extra) {
    if (message != CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_PAUSE
        && message != CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_PLAY
        && message != CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_PLAY) {
      //If we are sending a play / pause these events will still trigger the OnPause and OnPLay as we call Super, so we don't send the callback.
      cb = CallbackUtils.sendMessage(cb, message, extra);
    }
  }

  @Override
  public void onPlay() {
    Log.w("MusicControls", "On Play");
    super.onPlay();
    sendMessage(CallbackUtils.MUSIC_CONTROLS_PLAY);
  }

  @Override
  public void onPause() {
    Log.w("MusicControls", "On Pause");
    super.onPause();
    sendMessage(CallbackUtils.MUSIC_CONTROLS_PAUSE);
  }

  @Override
  public void onSkipToNext() {
    Log.w("MusicControls", "On Skip to Next");
    super.onSkipToNext();
    sendMessage(CallbackUtils.MUSIC_CONTROLS_NEXT);
  }

  @Override
  public void onSkipToPrevious() {
    Log.w("MusicControls", "On Skip To Previous");
    super.onSkipToPrevious();
    sendMessage(CallbackUtils.MUSIC_CONTROLS_PREVIOUS);
  }

  @Override
  public void onSeekTo(long pos) {
    Log.w("MusicControls", "On Seek To");
    super.onSeekTo(pos);
    sendMessage(CallbackUtils.MUSIC_CONTROLS_SEEK_TO, Long.toString(pos));
  }

  @Override
  public void onPlayFromMediaId(String mediaId, Bundle extras) {
    Log.w("MusicControls", "On Play From Media");
    super.onPlayFromMediaId(mediaId, extras);
  }

  @Override
  public void onSetShuffleMode(int shuffleMode) {
    Log.w("MusicControls", "On Set Shuffle Mode");
    super.onSetShuffleMode(shuffleMode);
    sendMessage(CallbackUtils.MUSIC_CONTROLS_SET_SHUFFLE_MODE, Long.toString(shuffleMode));
  }

  @Override
  public void onCustomAction(String action, Bundle extras) {
    Log.w("MusicControls", "Unknown custom action " + action);
    super.onCustomAction(action, extras);
  }

  @Override
  public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
    Log.w("MusicControls", "on Media Button Event");
    final KeyEvent event = (KeyEvent) mediaButtonIntent.getExtras().get(Intent.EXTRA_KEY_EVENT);
    if (event == null) {
      return super.onMediaButtonEvent(mediaButtonIntent);
    }

    Log.w("MusicControls", "on Media Button Event - Action: " + event.getAction());

    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      final int keyCode = event.getKeyCode();
      final String message = CallbackUtils.switchMediaButtonKeyCode(keyCode);
      Log.w("MusicControls", "on Media Button Event - Key Code: " + keyCode);

      if (message == CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_UNKNOWN) {
        sendMessage(message, Integer.toString(keyCode));
      } else {
        sendMessage(message);
      }

      return super.onMediaButtonEvent(mediaButtonIntent);
    }

    return false;
  }
}