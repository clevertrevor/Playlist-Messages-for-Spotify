package com.blogspot.spartandeveloper.playlistmessagesforspotify;

import android.app.Application;
import android.content.Context;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.component.ApplicationComponent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.component.DaggerApplicationComponent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.module.ApplicationModule;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BoilerplateApplication extends Application  {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Fabric.with(this, new Crashlytics());
        }
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
