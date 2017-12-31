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
            SPOTIFY_USER_ID = "spotify_user_id",
            SPOTIFY_TOKEN_EXPIRE_TIME = "spotify_token_expire_time",
            SPOTIFY_ACCESS_TOKEN = "spotify_access_token";

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

    public void setExpireTime(long expireTime) {
        editor.putLong(SPOTIFY_TOKEN_EXPIRE_TIME, expireTime).apply();
    }

    public long getExpireTimeSeconds() {
        return mPref.getLong(SPOTIFY_TOKEN_EXPIRE_TIME, -1);
    }

    public void setSpotifyAccessToken(String accessToken) {
        editor.putString(SPOTIFY_ACCESS_TOKEN, accessToken).apply();
    }

    public String getSpotifyAccessToken() {
        return mPref.getString(SPOTIFY_ACCESS_TOKEN, "");
    }



}
