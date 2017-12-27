package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.createplaylist;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.BoilerplateApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class CreatePlaylistService extends IntentService {

    public final static String PLAYLIST_NAME = "playlist_name", PLAYLIST_MESSAGE = "playlist_message",
            USER_ID = "user_id";
    private int totalTracks, runningTrackCount = 0;
    private ArrayList<Track> tracks;
    private String playlistName;
    private SpotifyService spotify;
    private String userId;

    public CreatePlaylistService(String name) {
        super(name);
        spotify = ((BoilerplateApplication)getApplicationContext()).getSpotifyService();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("received new playlist intent");
        playlistName = intent.getStringExtra(PLAYLIST_NAME);
        userId = intent.getStringExtra(USER_ID);
        String message = intent.getStringExtra(PLAYLIST_MESSAGE);


        String[] split = message.split(" ");
        totalTracks = split.length;
        tracks = new ArrayList<>(totalTracks);

        for (int i = 0; i < totalTracks; i++) {
            String curr = split[i];

            final int finalI = i;
            spotify.searchTracks(curr, new Callback<TracksPager>() {
                @Override
                public void success(TracksPager tracksPager, Response response) {
                    tracks.set(finalI, tracksPager.tracks.items.get(0));
                    runningTrackCount++;
                    checkAndHandleCompletion();
                }

                @Override
                public void failure(RetrofitError error) {
                    runningTrackCount++;
                    checkAndHandleCompletion();
                }
            });
        }

    }

    private void checkAndHandleCompletion() {
        Timber.d("checkAndHandleCompletion. runningTrackCount:%s, totalTracks:%s", runningTrackCount, totalTracks);
        if (runningTrackCount < totalTracks) return;

        Map<String, Object> options = new HashMap<>();
        options.put("name", playlistName);
        spotify.createPlaylist(userId, options, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                Timber.i("created playlist: %s", playlist.name);

                spotify.addTracksToPlaylist()
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error);
            }
        });



    }


}
