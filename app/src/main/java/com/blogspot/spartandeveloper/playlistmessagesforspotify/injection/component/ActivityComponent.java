package com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.component;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.PerActivity;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.module.ActivityModule;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.MainActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
