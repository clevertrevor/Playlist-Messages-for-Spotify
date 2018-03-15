package com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local;

import android.support.annotation.VisibleForTesting;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Singleton
public class DatabaseHelper {


    @Inject
    public DatabaseHelper() {
        this(Schedulers.io());
    }

    @VisibleForTesting
    public DatabaseHelper(Scheduler scheduler) {}

    public Observable<List<PlaylistSimple>> getPlaylists(final String accessToken) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + accessToken);
                    }
                })
                .build();

        final SpotifyService spotify = restAdapter.create(SpotifyService.class);

        return Observable.fromCallable(new Callable<List<PlaylistSimple>>() {
            @Override
            public List<PlaylistSimple> call() throws Exception {
                return spotify.getMyPlaylists().items;
            }
        });

    }
}
