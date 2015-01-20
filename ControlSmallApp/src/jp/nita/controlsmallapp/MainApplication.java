/*
 * Copyright 2011, 2012 Sony Corporation
 * Copyright (C) 2012 Sony Mobile Communications AB.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jp.nita.controlsmallapp;

import java.lang.reflect.Method;

import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sony.smallapp.SmallAppWindow;
import com.sony.smallapp.SmallAppWindow.OnWindowStateChangeListener;
import com.sony.smallapp.SmallApplication;
import com.sony.smallapp.SmallAppWindow.WindowState;

public class MainApplication extends SmallApplication {

	private static MainApplication instance=null;
	private Camera camera = null;

	boolean initializing = false;

	@Override
	public void onCreate() {
		super.onCreate();
		setContentView(R.layout.main);
		setTitle(R.string.app_name);

		SmallAppWindow.Attributes attr = getWindow().getAttributes();
		attr.minWidth = 512; /* The minimum width of the application, if it's resizable.*/
		attr.minHeight = 420; /*The minimum height of the application, if it's resizable.*/
		attr.width = 680;  /*The requested width of the application.*/
		attr.height = 720;  /*The requested height of the application.*/
		attr.flags |= SmallAppWindow.Attributes.FLAG_RESIZABLE;   /*Use this flag to enable the application window to be resizable*/
		attr.flags |= SmallAppWindow.Attributes.FLAG_NO_TITLEBAR;  /*Use this flag to remove the titlebar from the window*/
		//        attr.flags |= SmallAppWindow.Attributes.FLAG_HARDWARE_ACCELERATED;  /* Use this flag to enable hardware accelerated rendering*/

		getWindow().setAttributes(attr); /*setting window attributes*/

		camera = Camera.open();
		camera.startPreview();

		findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
			}
		});

		findViewById(R.id.prev_small).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
			}
		});

		findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
			}
		});

		findViewById(R.id.play_small).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
			}
		});

		findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_MEDIA_NEXT);
			}
		});

		findViewById(R.id.next_small).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_MEDIA_NEXT);
			}
		});

		findViewById(R.id.up_small).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_VOLUME_UP);
			}
		});

		findViewById(R.id.down_small).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_VOLUME_DOWN);
			}
		});

		findViewById(R.id.home_off).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getWindow().setWindowState(WindowState.MINIMIZED);
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
		});

		//		findViewById(R.id.search_off).setOnClickListener(new View.OnClickListener() {
		//			public void onClick(View v) {
		//				getWindow().setWindowState(WindowState.MINIMIZED);
		//				Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//				intent.addCategory(Intent.CATEGORY_DEFAULT);
		//				startActivity(intent);
		//			}
		//		});

		findViewById(R.id.switcher_off).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
					Method getService = serviceManagerClass.getMethod("getService", String.class);
					IBinder retbinder = (IBinder) getService.invoke(serviceManagerClass, "statusbar");
					Class<?> statusBarClass = Class.forName(retbinder.getInterfaceDescriptor());
					Object statusBarObject = statusBarClass.getClasses()[0].getMethod("asInterface", IBinder.class).invoke(null, new Object[] { retbinder });
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
						Method preloadRecentApps = statusBarClass.getMethod("preloadRecentApps");
						preloadRecentApps.setAccessible(true);
						preloadRecentApps.invoke(statusBarObject);
					}
					Method toggleRecentApps = statusBarClass.getMethod("toggleRecentApps");
					toggleRecentApps.setAccessible(true);
					toggleRecentApps.invoke(statusBarObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		findViewById(R.id.camera_off).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getWindow().setWindowState(WindowState.MINIMIZED);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				startActivity(intent);
			}
		});

		findViewById(R.id.music_title).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getWindow().setWindowState(WindowState.MINIMIZED);
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_APP_MUSIC);
				startActivity(intent);
			}
		});

		findViewById(R.id.music_title_small).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getWindow().setWindowState(WindowState.MINIMIZED);
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_APP_MUSIC);
				startActivity(intent);
			}
		});

		/*
		findViewById(R.id.call_large).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KeyEventSender sender = new KeyEventSender();
				sender.execute(KeyEvent.KEYCODE_CALL);
			}
		});
		 */

		findViewById(R.id.minimize).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getWindow().setWindowState(WindowState.MINIMIZED);
			}
		});

		findViewById(R.id.minimize_small).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getWindow().setWindowState(WindowState.MINIMIZED);
			}
		});

		findViewById(R.id.rotate_off).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(v.getContext(), getString(R.string.accelerometer_rotation_on), Toast.LENGTH_LONG).show();
				Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
				findViewById(R.id.rotate_off).setVisibility(View.GONE);
				findViewById(R.id.rotate_on).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.rotate_on).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(v.getContext(), getString(R.string.accelerometer_rotation_off), Toast.LENGTH_LONG).show();
				Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
				findViewById(R.id.rotate_on).setVisibility(View.GONE);
				findViewById(R.id.rotate_off).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.wifi_off).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
				Toast.makeText(v.getContext(), getString(R.string.wifi_on), Toast.LENGTH_LONG).show();
				wifi.setWifiEnabled(true);
				findViewById(R.id.wifi_off).setVisibility(View.GONE);
				findViewById(R.id.wifi_on).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.wifi_on).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
				Toast.makeText(v.getContext(), getString(R.string.wifi_off), Toast.LENGTH_LONG).show();
				wifi.setWifiEnabled(false);
				findViewById(R.id.wifi_on).setVisibility(View.GONE);
				findViewById(R.id.wifi_off).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.bluetooth_off).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
				Toast.makeText(v.getContext(), getString(R.string.bluetooth_on), Toast.LENGTH_LONG).show();
				bluetooth.enable();
				findViewById(R.id.bluetooth_off).setVisibility(View.GONE);
				findViewById(R.id.bluetooth_on).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.bluetooth_on).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
				Toast.makeText(v.getContext(), getString(R.string.bluetooth_off), Toast.LENGTH_LONG).show();
				bluetooth.disable();
				findViewById(R.id.bluetooth_on).setVisibility(View.GONE);
				findViewById(R.id.bluetooth_off).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.autosync_off).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(v.getContext(), getString(R.string.autosync_on), Toast.LENGTH_LONG).show();
				ContentResolver.setMasterSyncAutomatically(true);
				findViewById(R.id.autosync_off).setVisibility(View.GONE);
				findViewById(R.id.autosync_on).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.autosync_on).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(v.getContext(), getString(R.string.autosync_off), Toast.LENGTH_LONG).show();
				ContentResolver.setMasterSyncAutomatically(false);
				findViewById(R.id.autosync_on).setVisibility(View.GONE);
				findViewById(R.id.autosync_off).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.led_off).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Camera.Parameters params = camera.getParameters();
				params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				camera.setParameters(params);
				findViewById(R.id.led_off).setVisibility(View.GONE);
				findViewById(R.id.led_on).setVisibility(View.VISIBLE);
			}
		});

		findViewById(R.id.led_on).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Camera.Parameters params = camera.getParameters();
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				camera.setParameters(params);
				findViewById(R.id.led_on).setVisibility(View.GONE);
				findViewById(R.id.led_off).setVisibility(View.VISIBLE);
			}
		});

		((SeekBar)findViewById(R.id.light_seekbar)).setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,arg1);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				updateSettingButtons();
			}
		});

		((SeekBar)findViewById(R.id.music_seekbar)).setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				int musicVolume = arg1*manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/255;
				manager.setStreamVolume(AudioManager.STREAM_MUSIC,musicVolume,0);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				updateSettingButtons();
				
				int arg1=arg0.getProgress();
				AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				int musicVolume = arg1*manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/255;
				manager.setStreamVolume(AudioManager.STREAM_MUSIC,musicVolume,initializing?0:AudioManager.FLAG_PLAY_SOUND);
			}
		});

		((SeekBar)findViewById(R.id.notification_seekbar)).setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				int ringVolume = arg1*manager.getStreamMaxVolume(AudioManager.STREAM_RING)/255;
				manager.setStreamVolume(AudioManager.STREAM_RING,ringVolume,0);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				updateSettingButtons();
				
				int arg1=arg0.getProgress();
				AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				int ringVolume = arg1*manager.getStreamMaxVolume(AudioManager.STREAM_RING)/255;
				manager.setStreamVolume(AudioManager.STREAM_RING,ringVolume,initializing?0:AudioManager.FLAG_PLAY_SOUND);
			}
		});

		getWindow().setOnWindowStateChangeListener(new OnWindowStateChangeListener(){
			@Override
			public void onWindowStateChanged(WindowState state) {
				updateLayout();
			}
		});

		instance=this;
		updateSettingButtons();

	}

	public void updateSettingButtons(){
		try {
			initializing=true;

			WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
			if(wifi.isWifiEnabled()) {
				findViewById(R.id.wifi_off).setVisibility(View.GONE);
				findViewById(R.id.wifi_on).setVisibility(View.VISIBLE);
			}else{
				findViewById(R.id.wifi_on).setVisibility(View.GONE);
				findViewById(R.id.wifi_off).setVisibility(View.VISIBLE);
			}
			BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
			if(bluetooth.isEnabled()) {
				findViewById(R.id.bluetooth_off).setVisibility(View.GONE);
				findViewById(R.id.bluetooth_on).setVisibility(View.VISIBLE);
			}else{
				findViewById(R.id.bluetooth_on).setVisibility(View.GONE);
				findViewById(R.id.bluetooth_off).setVisibility(View.VISIBLE);
			}

			if(ContentResolver.getMasterSyncAutomatically()){
				findViewById(R.id.autosync_off).setVisibility(View.GONE);
				findViewById(R.id.autosync_on).setVisibility(View.VISIBLE);
			}else{
				findViewById(R.id.autosync_on).setVisibility(View.GONE);
				findViewById(R.id.autosync_off).setVisibility(View.VISIBLE);
			}

			if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) > 0){
				findViewById(R.id.rotate_off).setVisibility(View.GONE);
				findViewById(R.id.rotate_on).setVisibility(View.VISIBLE);
			} else{
				findViewById(R.id.rotate_on).setVisibility(View.GONE);
				findViewById(R.id.rotate_off).setVisibility(View.VISIBLE);
			}
			if(!SongChangedReceiver.songTitle.equals("")){
				((TextView)findViewById(R.id.music_title)).setText(SongChangedReceiver.songTitle);
				((TextView)findViewById(R.id.music_title_small)).setText(SongChangedReceiver.songTitle);
			}
			if(!SongChangedReceiver.songArtist.equals("")){
				((TextView)findViewById(R.id.music_artist)).setText(SongChangedReceiver.songArtist);
				((TextView)findViewById(R.id.music_artist_small)).setText(SongChangedReceiver.songArtist);
			}

			int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
			((SeekBar)findViewById(R.id.light_seekbar)).setProgress(brightness);

			AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

			int musicVolume = 255*manager.getStreamVolume(AudioManager.STREAM_MUSIC)/manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			((SeekBar)findViewById(R.id.music_seekbar)).setProgress(musicVolume);

			int ringVolume = 255*manager.getStreamVolume(AudioManager.STREAM_RING)/manager.getStreamMaxVolume(AudioManager.STREAM_RING);
			((SeekBar)findViewById(R.id.notification_seekbar)).setProgress(ringVolume);

			initializing=false;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		updateSettingButtons();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		instance=null;
		camera.stopPreview();
		camera.release();
	}

	private class KeyEventSender extends AsyncTask<Integer, Object, Object> {
		@Override
		protected Object doInBackground(Integer... params) {
			int keycode = (Integer)(params[0]);
			Instrumentation ist = new Instrumentation();
			ist.sendKeyDownUpSync(keycode);
			return null;
		}
	}

	public void setSongTitle(String title,String artist){
		((TextView)findViewById(R.id.music_title)).setText(title);
		((TextView)findViewById(R.id.music_title_small)).setText(title);
		((TextView)findViewById(R.id.music_artist)).setText(artist);
		((TextView)findViewById(R.id.music_artist_small)).setText(artist);
	}

	public static void songHasChanged(String title,String artist){
		if(instance!=null){
			instance.setSongTitle(title,artist);
		}
	}

	public static void updateLayout(){
		if(instance!=null){
			instance.updateSettingButtons();
			instance.findViewById(R.id.layout).invalidate();
		}
	}
}
