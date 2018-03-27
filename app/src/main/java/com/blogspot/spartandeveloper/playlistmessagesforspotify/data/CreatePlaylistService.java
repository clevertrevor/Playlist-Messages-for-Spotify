package com.blogspot.spartandeveloper.playlistmessagesforspotify.data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.MyApp;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.CreatePlaylistErrorEvent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.CreatePlaylistSuccessEvent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.LoadPlaylistsEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class CreatePlaylistService extends IntentService {

    public final static String PLAYLIST_NAME = "playlist_name", PLAYLIST_MESSAGE = "playlist_message",
            USER_ID = "user_id";

    public static Intent newInstance(@NonNull Context context,
                                     @NonNull String playlistName,
                                     @NonNull String playlistMessage,
                                     @NonNull String userId) {

        Intent intent = new Intent(context, CreatePlaylistService.class);
        intent.putExtra(CreatePlaylistService.PLAYLIST_NAME, playlistName);
        intent.putExtra(CreatePlaylistService.PLAYLIST_MESSAGE, playlistMessage);
        intent.putExtra(CreatePlaylistService.USER_ID, userId);
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
        String userId = intent.getStringExtra(USER_ID);
        String message = intent.getStringExtra(PLAYLIST_MESSAGE);

//        PlaylistCreator pc = new PlaylistCreator(userId, spotify, playlistName, message);
        PlaylistCreator2 pc = new PlaylistCreator2(userId, spotify, playlistName, message);
        pc.execute();

    }

}