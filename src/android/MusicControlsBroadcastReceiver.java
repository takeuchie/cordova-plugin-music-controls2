package com.homerours.musiccontrols;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.media.AudioManager;
//import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

//import android.app.Activity;
//import android.os.Bundle;

public class MusicControlsBroadcastReceiver extends BroadcastReceiver {
	private CallbackContext cb;
	private MusicControls musicControls;

	public MusicControlsBroadcastReceiver(MusicControls musicControls) {
		this.musicControls = musicControls;
	}

	public void setCallback(CallbackContext cb) {
		this.cb = cb;
	}

	public void stopListening() {
		this.cb = CallbackUtils.sendMessage(this.cb, CallbackUtils.MUSIC_CONTROLS_STOP_LISTENING);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (this.cb != null) {
			String message = intent.getAction();
			// Log.w("MusicControls","Broadcast on Receive - Message : "+message);

			if (message.equals(Intent.ACTION_HEADSET_PLUG)) {
				// Headphone plug/unplug
				int state = intent.getIntExtra("state", -1);
				switch (state) {
					case 0:
						this.cb = CallbackUtils.sendMessage(this.cb, CallbackUtils.MUSIC_CONTROLS_HEADSET_UNPLUGGED);
						this.musicControls.unregisterMediaButtonEvent();
						break;
					case 1:
						this.cb = CallbackUtils.sendMessage(this.cb, CallbackUtils.MUSIC_CONTROLS_HEADSET_PLUGGED);
						this.musicControls.registerMediaButtonEvent();
						break;
					default:
						break;
				}
			} else if (message.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
				this.cb = CallbackUtils.sendMessage(this.cb, CallbackUtils.MUSIC_CONTROLS_AUDIO_BECOMING_NOISY);
			} else if (message.equals(CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON)) {
				// Media button
				KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
				if (event.getAction() == KeyEvent.ACTION_DOWN) {

					int keyCode = event.getKeyCode();
					switch (keyCode) {
						case KeyEvent.KEYCODE_MEDIA_NEXT:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_NEXT);
							break;
						case KeyEvent.KEYCODE_MEDIA_PAUSE:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_PAUSE);
							break;
						case KeyEvent.KEYCODE_MEDIA_PLAY:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_PLAY);
							break;
						case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_PLAY_PAUSE);
							break;
						case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_PREVIOUS);
							break;
						case KeyEvent.KEYCODE_MEDIA_STOP:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_STOP);
							break;
						case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_FAST_FORWARD);
							break;
						case KeyEvent.KEYCODE_MEDIA_REWIND:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_REWIND);
							break;
						case KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_SKIP_BACKWARD);
							break;
						case KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_SKIP_FORWARD);
							break;
						case KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_STEP_BACKWARD);
							break;
						case KeyEvent.KEYCODE_MEDIA_STEP_FORWARD:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_STEP_FORWARD);
							break;
						case KeyEvent.KEYCODE_META_LEFT:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_META_LEFT);
							break;
						case KeyEvent.KEYCODE_META_RIGHT:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_META_RIGHT);
							break;
						case KeyEvent.KEYCODE_MUSIC:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_MUSIC);
							break;
						case KeyEvent.KEYCODE_VOLUME_UP:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_VOLUME_UP);
							break;
						case KeyEvent.KEYCODE_VOLUME_DOWN:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_VOLUME_DOWN);
							break;
						case KeyEvent.KEYCODE_VOLUME_MUTE:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_VOLUME_MUTE);
							break;
						case KeyEvent.KEYCODE_HEADSETHOOK:
							this.cb = CallbackUtils.sendMessage(this.cb,
									CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON_HEADSET_HOOK);
							break;
						default:
							this.cb = CallbackUtils.sendMessage(this.cb, message);
							break;
					}
					this.cb = null;
				}
			} else if (message.equals(CallbackUtils.MUSIC_CONTROLS_DESTROY)) {
				// Close Button
				this.cb = CallbackUtils.sendMessage(this.cb, CallbackUtils.MUSIC_CONTROLS_DESTROY);
				this.musicControls.destroyPlayerNotification();
			} else {
				this.cb = CallbackUtils.sendMessage(this.cb, message);
			}

		}

	}
}
