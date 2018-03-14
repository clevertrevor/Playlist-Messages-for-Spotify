package com.blogspot.spartandeveloper.playlistmessagesforspotify.data;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.DatabaseHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.PreferencesHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

@Singleton
public class DataManager {

    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<List<PlaylistSimple>> getPlaylists() {
        String accessToken = mPreferencesHelper.getSpotifyAccessToken();
        return mDatabaseHelper.getPlaylists(accessToken);
    }

}
