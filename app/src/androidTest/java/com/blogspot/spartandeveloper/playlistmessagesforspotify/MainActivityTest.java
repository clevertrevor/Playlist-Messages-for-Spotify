package com.blogspot.spartandeveloper.playlistmessagesforspotify;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.test.common.TestComponentRule;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.test.common.TestDataFactory;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.Observable;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    public final TestComponentRule component =
            new TestComponentRule(InstrumentationRegistry.getTargetContext());
    public final ActivityTestRule<MainActivity> main =
            new ActivityTestRule<MainActivity>(MainActivity.class, false, false) {
                @Override
                protected Intent getActivityIntent() {
                    // Override the default intent so we pass a false flag for syncing so it doesn't
                    // start a sync service in the background that would affect  the behaviour of
                    // this test.
                    return MainActivity.getStartIntent(
                            InstrumentationRegistry.getTargetContext(), false);
                }
            };

    // allows mocking of intents
    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule =
            new IntentsTestRule<>(MainActivity.class);

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public final TestRule chain = RuleChain.outerRule(component).around(main);

    @Test
    public void clickFabOpensCreatePlaylistDialog() {
        onView(ViewMatchers.withId(R.id.fab_create_playlist_dialog))
                .perform(ViewActions.click());
        onView(withText(R.string.enter_pl_info)).check(matches(isDisplayed()));
    }

    @Test
    public void clickPlaylistOpensSpotifyIntent() {
        List<PlaylistSimple> list = TestDataFactory.makeActualPlaylist();
        when(component.getMockDataManager().getPlaylists(""))
                .thenReturn(Observable.just(list));

        main.launchActivity(null);


        onView(ViewMatchers.withId(R.id.rv_playlists))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(toPackage("com.spotify.music"));
        //https://developer.android.com/training/testing/espresso/intents.html
    }

    @Test
    public void emptyListOfPlaylistsShows() {

        List<PlaylistSimple> testPlaylists = TestDataFactory.makePlaylists(0);
        when(component.getMockDataManager().getPlaylists(""))
                .thenReturn(Observable.just(testPlaylists));

        main.launchActivity(null);

        onView(withId(R.id.rv_playlists)).check(matches(isDisplayed()));

    }

    @Test
    public void retrievedListOfPlaylistsShows() {

        List<PlaylistSimple> testPlaylists = TestDataFactory.makePlaylists(5);
        when(component.getMockDataManager().getPlaylists(""))
                .thenReturn(Observable.just(testPlaylists));

        main.launchActivity(null);

        onView(withId(R.id.rv_playlists)).check(matches(isDisplayed()));

        int position = 0;
        for (PlaylistSimple playlist : testPlaylists) {
            onView(withId(R.id.rv_playlists))
                    .perform(RecyclerViewActions.scrollToPosition(position));
            String name = playlist.name;
            onView(withText(name)).check(matches(isDisplayed()));
            position++;
        }

    }

//    @Test
//    public void listOfRibotsShows() {
//        List<Ribot> testDataRibots = TestDataFactory.makeListRibots(20);
//        when(component.getMockDataManager().getRibots())
//                .thenReturn(Observable.just(testDataRibots));
//
//        main.launchActivity(null);
//
//        int position = 0;
//        for (Ribot ribot : testDataRibots) {
//            onView(withId(R.id.recycler_view))
//                    .perform(RecyclerViewActions.scrollToPosition(position));
//            String name = String.format("%s %s", ribot.profile().name().first(),
//                    ribot.profile().name().last());
//            onView(withText(name))
//                    .check(matches(isDisplayed()));
//            onView(withText(ribot.profile().email()))
//                    .check(matches(isDisplayed()));
//            position++;
//        }
//    }

}
