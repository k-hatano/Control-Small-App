package jp.nita.controlsmallapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SongChangedReceiver extends BroadcastReceiver {
	public static String songTitle="";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		songTitle=intent.getExtras().getString("TRACK_NAME");
		MainApplication.songHasChanged(songTitle);
	}

}
