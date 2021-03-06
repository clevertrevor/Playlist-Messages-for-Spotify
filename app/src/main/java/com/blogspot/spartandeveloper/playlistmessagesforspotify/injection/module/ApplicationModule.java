package com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.module;

import android.app.Application;
import android.content.Context;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.ApplicationContext;

import dagger.Module;
import dagger.Provides;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

}
