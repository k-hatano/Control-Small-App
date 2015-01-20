package jp.nita.controlsmallapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SongChangedReceiver extends BroadcastReceiver {
	public static String songTitle="";
	public static String songArtist="";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String title=intent.getExtras().getString("TRACK_NAME");
		String artist=intent.getExtras().getString("ARTIST_NAME");
		String album=intent.getExtras().getString("ALBUM_NAME");
		
		if(title==null||"".equals(title)){
			songTitle="Music";
		}else{
			songTitle=title;
		}
		if(artist==null||"".equals(artist)){
			songArtist="";
		}else{
			if(album==null||"".equals(album)){
				songArtist=artist;
			}else{
				songArtist=artist+" - "+album;
			}
		}
		MainApplication.songHasChanged(songTitle,songArtist);
	}

}
