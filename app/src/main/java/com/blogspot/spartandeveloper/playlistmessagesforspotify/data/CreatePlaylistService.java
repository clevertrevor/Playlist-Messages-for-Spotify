package com.blogspot.spartandeveloper.playlistmessagesforspotify.data;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.MyApp;
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
    private int totalTracks, runningTrackCount = 0;
    private ArrayList<Track> tracks;
    private String playlistName;
    private SpotifyService spotify;
    private String userId;

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
        spotify = ((MyApp)getApplicationContext()).getSpotifyService();
        playlistName = intent.getStringExtra(PLAYLIST_NAME);
        userId = intent.getStringExtra(USER_ID);
        String message = intent.getStringExtra(PLAYLIST_MESSAGE);


        String[] split = message.split(" ");
        totalTracks = split.length;
        tracks = new ArrayList<>(totalTracks);

        for (int i = 0; i < totalTracks; i++) {
            tracks.add(null);
        }

        // loop over all tracks
        for (int i = 0; i < totalTracks; i++) {
            String curr = split[i];
            searchTrack(curr, i, 0);
        }
    }

    private void searchTrack(final String curr, final int index, final int offset) {
        Map<String, Object> options = new HashMap<>();
        options.put("limit", 50);
        options.put("offset", offset);
        Timber.d("searchTracks. curr:%s, offset:%s, index:%s", curr, offset, index);

        spotify.searchTracks(curr, options, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {

                if (tracks.get(index) != null) {
                    Timber.e("SHOULD NOT REACH HERE. already found track for index :%s", index);
                    return;
                }

                boolean found = false;
                for (Track track : tracksPager.tracks.items) {
                    String trackName = track.name;
                    if (trackName.equalsIgnoreCase(curr)) {
                        Timber.i("found word: %s", trackName);
                        tracks.set(index, track);
                        runningTrackCount++;
                        found = true;
                        checkAndHandleCompletion();
                        break;
                    }
                }

                if (!found && offset <= 200) {
                    // search next 50
                    searchTrack(curr, index, offset + 50);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("retrofit error finding track");
                Timber.e(error);
            }
        });


    }

    private void checkAndHandleCompletion() {
        Timber.d("checkAndHandleCompletion. runningTrackCount:%s, totalTracks:%s", runningTrackCount, totalTracks);
        if (runningTrackCount < totalTracks) return;

        Map<String, Object> options = new HashMap<>();
        options.put("name", playlistName);
        spotify.createPlaylist(userId, options, new Callback<Playlist>() {
            @Override
            public void success(final Playlist playlist, Response response) {
                Timber.i("created playlist: %s", playlist.name);

                final Map<String, Object> addTrackBody = new HashMap<>();
                String[] uris = new String[tracks.size()];
                for (int i = 0; i < tracks.size(); i++) {
                    uris[i] = tracks.get(i).uri;
                }
                addTrackBody.put("uris", uris);

                spotify.addTracksToPlaylist(userId, playlist.id, null, addTrackBody,
                        new Callback<Pager<PlaylistTrack>>() {
                            @Override
                            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                                Timber.d("added tracks to playlist: %s", playlist.name);
                                Timber.d("response: %s", response.getReason());
                                EventBus.getDefault().post(new LoadPlaylistsEvent());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Timber.e("unable to add tracks to playlist");
                                Timber.e(error);
                            }
                        });
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("unable to create playlist");
                Timber.e(error);
            }
        });



    }


}
