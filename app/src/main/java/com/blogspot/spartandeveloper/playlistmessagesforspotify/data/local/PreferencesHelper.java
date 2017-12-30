package com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_NAME = "file_pref_helper";

    private final SharedPreferences mPref;
    private final Editor editor;

    private final static String
        SPOTIFY_USER_ID = "spotify_user_id";

    @Inject
    public PreferencesHelper(@ApplicationContext Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = mPref.edit();
    }

    public void setSpotifyUserId(String userId) {
        editor.putString(SPOTIFY_USER_ID, userId).apply();
    }

    public String getSpotifyUserId() {
        return mPref.getString(SPOTIFY_USER_ID, null);
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

}
