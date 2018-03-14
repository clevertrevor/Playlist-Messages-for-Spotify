package com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.component;

import android.app.Application;
import android.content.Context;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.DataManager;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.DatabaseHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.PreferencesHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.ApplicationContext;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.module.ApplicationModule;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.RxEventBus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ApplicationContext
    Context context();
    Application application();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    RxEventBus eventBus();

}
