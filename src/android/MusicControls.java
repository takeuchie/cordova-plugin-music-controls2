package com.homerours.musiccontrols;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.os.Build;
import android.media.AudioManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ConcurrentModificationException;

//import android.app.Notification;
//import android.app.Activity;
//import android.app.Service;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.R;
//import android.media.session.MediaSession.Token;
//import android.content.ServiceConnection;
//import android.content.ComponentName;
//import org.apache.cordova.PluginResult;

public class MusicControls extends CordovaPlugin {
	private MusicControlsNotification notification;
	private MediaSessionCompat mediaSessionCompat;
	private final int notificationID = 7824;
	private boolean mediaButtonAccess = true;
	private long playbackPosition = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
	private android.media.session.MediaSession.Token token;
	private int currentPlaybackState = PlaybackStateCompat.STATE_NONE;
	private boolean currentIsPlaying = false;

	private Activity cordovaActivity;

	private MediaSessionCallback mMediaSessionCallback;

	private synchronized void setMediaPlaybackState(int newState) {
		// Skip if the state hasn't changed
		if (newState == currentPlaybackState) {
			Log.d("MusicControls", "Skipping playback state update - already in state: " + newState);
			return;
		}

		long actions = PlaybackStateCompat.ACTION_PLAY_PAUSE |
				PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
				PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
				PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE |
				PlaybackStateCompat.ACTION_SEEK_TO;

		float playbackSpeed = 0;

		try {
			// Handling issues that can cause a crash by calling beginBroadcast() or
			// finishBroadcast() within mediaSessionCompat after calling
			// mediaSessionCompat.setPlaybackState
			if (this.mediaSessionCompat != null) {
				PlaybackStateCompat playbackState = new PlaybackStateCompat.Builder()
						.setActions(actions)
						.setState(newState, playbackPosition, playbackSpeed)
						.build();
				this.mediaSessionCompat.setPlaybackState(playbackState);
				currentPlaybackState = newState;
				Log.d("MusicControls", "Updated playback state: " + newState);
			} else {
				Log.w("MusicControls", "Attempted to set playback state but mediaSessionCompat is null");
			}
		} catch (IllegalStateException e) {
			Log.e("MusicControls", "IllegalStateException in setMediaPlaybackState", e);
		} catch (Exception e) {
			Log.e("MusicControls", "Unexpected error in setMediaPlaybackState", e);
		}
	}

	private void setMediaSession(Context context) {

		try {
			Intent headsetIntent = new Intent(CallbackUtils.MUSIC_CONTROLS_MEDIA_BUTTON); // music-controls-media-button
			headsetIntent.setPackage(context.getPackageName());
			PendingIntent mediaButtonPendingIntent = PendingIntent.getBroadcast(
					context, 0, headsetIntent,
					Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
							? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
							: PendingIntent.FLAG_UPDATE_CURRENT);

			this.mediaSessionCompat = new MediaSessionCompat(context, "cordova-music-controls-media-session", null,
					mediaButtonPendingIntent);

			this.mediaSessionCompat.setFlags(
					MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

			MediaSessionCompat.Token _token = this.mediaSessionCompat.getSessionToken();
			this.token = (android.media.session.MediaSession.Token) _token.getToken();
			setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED); 

			this.mMediaSessionCallback = new MediaSessionCallback(mediaSessionCompat);
			this.mediaSessionCompat.setCallback(this.mMediaSessionCallback);

			this.mediaSessionCompat.setActive(true);

		} catch (Exception e) {
			this.mediaButtonAccess = false;
			e.printStackTrace();
		}

	}

	public void destroyPlayerNotification() {
		this.notification.destroy();
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		final Activity activity = this.cordova.getActivity();
		final Context context = activity.getApplicationContext();

		// Notification Killer
		final MusicControlsServiceConnection mConnection = new MusicControlsServiceConnection(activity);

		this.cordovaActivity = activity;

		this.setMediaSession(context);

		this.notification = new MusicControlsNotification(this.cordovaActivity, this.notificationID, this.token) {
			@Override
			protected void onNotificationUpdated(Notification notification) {
				mConnection.setNotification(notification, this.infos.isPlaying);
			}

			@Override
			protected void onNotificationDestroyed() {
				mConnection.killNotification();
			}
		}; 

		Intent startServiceIntent = new Intent(activity, MusicControlsNotificationKiller.class);
		startServiceIntent.putExtra("notificationID", this.notificationID);
		activity.bindService(startServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext)
			throws JSONException {
		final Activity activity = this.cordova.getActivity();
		final Context context = this.cordova.getActivity().getApplicationContext();

		switch (action) {
			case "create":
				cordova.getThreadPool().execute(() -> {
					try {
						final MusicControlsInfos infos = new MusicControlsInfos(args);
						final MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

						Log.d("MusicControls", "Notification Title on create " + infos.track);

						// track title
						metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, infos.track);
						// artists
						metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, infos.artist);
						// album
						metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, infos.album);

						// scrubber
						if (infos.hasScrubbing) {
							metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, infos.duration);
							playbackPosition = infos.elapsed;
						} else {
							playbackPosition = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
						}

						Bitmap art = BitmapUtils.getBitmapCover(context, infos.cover);
						if (art != null) {
							metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, art);
							metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, art);
						}

						mediaSessionCompat.setMetadata(metadataBuilder.build());

						// Moving this update after metadataBuilder settings
						notification.updateNotification(infos);
						currentIsPlaying = infos.isPlaying;

						if (infos.isPlaying) {
							setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
						} else {
							setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
						}

						Log.d("MusicControls", "Created with initial playing state: " + infos.isPlaying);
						callbackContext.success("success");
					} catch (JSONException e) {
						e.printStackTrace();
						callbackContext.error(e.getLocalizedMessage());
					}
				});
				break;

			case "updateIsPlaying":
				cordova.getThreadPool().execute(() -> {
					try {
						final JSONObject params = args.getJSONObject(0);
						final boolean isPlaying = params.getBoolean("isPlaying");

						synchronized (this.notification) {
							try {
								// Skip if the playing state hasn't changed
								if (isPlaying == currentIsPlaying) {
									Log.d("MusicControls", "Skipping isPlaying update - already in state: " + isPlaying);
									callbackContext.success("success");
									return;
								}

								Log.d("MusicControls", "Updating isPlaying state to: " + isPlaying);
								this.notification.updateIsPlaying(isPlaying);
								currentIsPlaying = isPlaying;

								if (isPlaying) {
									setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
								} else {
									setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
								}

								callbackContext.success("success");
							} catch (ConcurrentModificationException e) {
								Log.e("MusicControls", "ConcurrentModificationException while updating isPlaying state", e);
								callbackContext.error("Failed to update playing state: Concurrent modification detected");
							} catch (Exception e) {
								Log.e("MusicControls", "Unexpected error while updating isPlaying state", e);
								callbackContext.error("Failed to update playing state: " + e.getMessage());
							}
						}
					} catch (JSONException e) {
						Log.e("MusicControls", "JSONException while parsing updateIsPlaying parameters", e);
						callbackContext.error("Invalid parameters: " + e.getMessage());
					}
				});
				break;

			case "updateShuffle":
				cordova.getThreadPool().execute(() -> {
					try {
						final JSONObject params = args.getJSONObject(0);
						final boolean isShuffle = params.getBoolean("isShuffle");
						mediaSessionCompat.setShuffleMode(
								isShuffle ? PlaybackStateCompat.SHUFFLE_MODE_ALL
										: PlaybackStateCompat.SHUFFLE_MODE_NONE);
						callbackContext.success("success");
					} catch (JSONException e) {
						e.printStackTrace();
						callbackContext.error(e.getLocalizedMessage());
					}
				});
				break;

			case "updateIsBuffering":
				cordova.getThreadPool().execute(() -> {
					try {
						final JSONObject params = args.getJSONObject(0);
						playbackPosition = params.getLong("elapsed");
						final boolean isBuffering = params.getBoolean("isBuffering");
						setMediaPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
						callbackContext.success("success");
					} catch (JSONException e) {
						e.printStackTrace();
						callbackContext.error(e.getLocalizedMessage());
					}
				});
				break;

			case "updateDismissable":
				cordova.getThreadPool().execute(() -> {
					try {
						final JSONObject params = args.getJSONObject(0);
						final boolean dismissable = params.getBoolean("dismissable");
						this.notification.updateDismissable(dismissable);
						callbackContext.success("success");
					} catch (JSONException e) {
						e.printStackTrace();
						callbackContext.error(e.getLocalizedMessage());
					}
				});
				break;

			case "updateElapsed":
				cordova.getThreadPool().execute(() -> {
					try {
						final JSONObject params = args.getJSONObject(0);
						playbackPosition = params.getLong("elapsed");
						final boolean isPlaying = params.getBoolean("isPlaying");
						Log.d("MusicControls", "Update elapsed position: " + playbackPosition + ", playing: " + isPlaying);

						synchronized (this.notification) {
							try {
								// Only update isPlaying state if it has changed
								if (isPlaying != currentIsPlaying) {
									Log.d("MusicControls", "Updating isPlaying state in elapsed update to: " + isPlaying);
									this.notification.updateIsPlaying(isPlaying);
									currentIsPlaying = isPlaying;

									if (isPlaying) {
										setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
									} else {
										setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
									}
								} else {
									Log.d("MusicControls", "Skipping isPlaying update in elapsed - already in state: " + isPlaying);
								}

								callbackContext.success("success");
							} catch (ConcurrentModificationException e) {
								Log.e("MusicControls", "ConcurrentModificationException while updating elapsed state", e);
								callbackContext.error("Failed to update elapsed state: Concurrent modification detected");
							} catch (Exception e) {
								Log.e("MusicControls", "Unexpected error while updating elapsed state", e);
								callbackContext.error("Failed to update elapsed state: " + e.getMessage());
							}
						}
					} catch (JSONException e) {
						Log.e("MusicControls", "JSONException while parsing updateElapsed parameters", e);
						callbackContext.error("Invalid parameters: " + e.getMessage());
					}
				});
				break;

			case "destroy":
				cordova.getThreadPool().execute(() -> {
					synchronized (this.notification) {
						try {
							Log.d("MusicControls", "Destroying music controls");
							this.notification.destroy();
							if (mediaSessionCompat != null) {
								mediaSessionCompat.setActive(false);
								mediaSessionCompat.release(); // Release the MediaSession when it's no longer needed
								mediaSessionCompat = null;
							}
							// Reset states
							currentPlaybackState = PlaybackStateCompat.STATE_NONE;
							currentIsPlaying = false;
							callbackContext.success("success");
						} catch (Exception e) {
							Log.e("MusicControls", "Error while destroying music controls", e);
							callbackContext.error("Failed to destroy music controls: " + e.getMessage());
						}
					}
				});
				break;

			case "watch":
				cordova.getThreadPool().execute(() -> {
					mMediaSessionCallback.setCallback(callbackContext);
				});
				break;
		}

		return true;

	}

	@Override
	public void onDestroy() {
		synchronized (this.notification) {
			try {
				Log.d("MusicControls", "Destroying music controls in onDestroy");
				this.notification.destroy();
				if (mediaSessionCompat != null) {
					mediaSessionCompat.setActive(false);
					mediaSessionCompat.release();
					mediaSessionCompat = null;
				}
				currentPlaybackState = PlaybackStateCompat.STATE_NONE;
				currentIsPlaying = false;
			} catch (Exception e) {
				Log.e("MusicControls", "Error in onDestroy", e);
			}
		}
		super.onDestroy();
	}

	@Override
	public void onReset() {
		synchronized (this.notification) {
			try {
				Log.d("MusicControls", "Resetting music controls");
				onDestroy();
			} catch (Exception e) {
				Log.e("MusicControls", "Error in onReset", e);
			}
		}
		super.onReset();
	}

}
