package com.blogspot.spartandeveloper.playlistmessagesforspotify.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.component.ApplicationComponent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
