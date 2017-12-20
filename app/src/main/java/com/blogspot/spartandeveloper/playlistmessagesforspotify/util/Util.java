package com.blogspot.spartandeveloper.playlistmessagesforspotify.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class Util {

    // Return true if Spotify is installed
    public static boolean isSpotifyInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return null != pm.getPackageInfo("com.spotify.music", PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
