package com.blogspot.spartandeveloper.playlistmessagesforspotify;

import android.app.Application;
import android.content.Context;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.component.ApplicationComponent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.component.DaggerApplicationComponent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.module.ApplicationModule;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import timber.log.Timber;

public class BoilerplateApplication extends Application  {

    ApplicationComponent mApplicationComponent;
    private SpotifyService spotifyService;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Fabric.with(this, new Crashlytics());
        }
    }

    public void initSpotifyService(final String accessToken) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + accessToken);
                    }
                })
                .build();

        spotifyService = restAdapter.create(SpotifyService.class);
    }

    public SpotifyService getSpotifyService() {
        if (spotifyService == null) {
            Timber.e("Spotify service not initialized");
            return null;
        }
        return spotifyService;
    }

    public static BoilerplateApplication get(Context context) {
        return (BoilerplateApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
