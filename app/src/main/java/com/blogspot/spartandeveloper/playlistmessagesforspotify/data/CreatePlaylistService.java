package com.blogspot.spartandeveloper.playlistmessagesforspotify.data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.MyApp;

import kaaes.spotify.webapi.android.SpotifyService;
import timber.log.Timber;


public class CreatePlaylistService extends IntentService {

    public final static String PLAYLIST_NAME = "playlist_name", PLAYLIST_MESSAGE = "playlist_message";

    public static Intent newInstance(@NonNull Context context,
                                     @NonNull String playlistName,
                                     @NonNull String playlistMessage) {

        Intent intent = new Intent(context, CreatePlaylistService.class);
        intent.putExtra(CreatePlaylistService.PLAYLIST_NAME, playlistName);
        intent.putExtra(CreatePlaylistService.PLAYLIST_MESSAGE, playlistMessage);
        return intent;
    }

    public CreatePlaylistService() {
        super("CreatePlaylistService");
    }

    public CreatePlaylistService(String name) {
        super(name);
        Timber.d("created CreatePlaylistService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("received new playlist intent");
        SpotifyService spotify = ((MyApp)getApplicationContext()).getSpotifyService();
        String playlistName = intent.getStringExtra(PLAYLIST_NAME);
        String message = intent.getStringExtra(PLAYLIST_MESSAGE);

        PlaylistCreator pc = new PlaylistCreator(spotify, playlistName, message);
        pc.execute();

    }

}