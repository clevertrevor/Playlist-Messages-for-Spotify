package com.blogspot.spartandeveloper.playlistmessagesforspotify;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.DataManager;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.PreferencesHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.test.common.TestDataFactory;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.MainMvpView;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.MainPresenter;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.RxSchedulersOverrideRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock MainMvpView mMockMainMvpView;
    @Mock DataManager mMockDataManager;
    @Mock PreferencesHelper preferencesHelper;
    private MainPresenter mMainPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        mMainPresenter = new MainPresenter(mMockDataManager, preferencesHelper);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() {
        mMainPresenter.detachView();
    }

    @Test
    public void whenEmptyPlaylistInfo_thenShowPlaylistError() {

        mMainPresenter.createPlaylist("", "");

        verify(mMockMainMvpView, never()).showPlaylists(null);
        verify(mMockMainMvpView, never()).showPlaylistsEmpty();
        verify(mMockMainMvpView, never()).showError();
        verify(mMockMainMvpView).showCreatePlaylistMessageError();
    }

    @Test
    public void loadPlaylistsReturnsPlaylists() {
        List<PlaylistSimple> playlists = TestDataFactory.makePlaylists(10);

        when(mMockDataManager.getPlaylists())
                .thenReturn(Observable.just(playlists));

        mMainPresenter.loadPlaylists();
        verify(mMockMainMvpView).showPlaylists(playlists);
        verify(mMockMainMvpView, never()).showPlaylistsEmpty();
        verify(mMockMainMvpView, never()).showError();
    }

    @Test
    public void loadPlaylistsReturnsEmptyList() {
        when(mMockDataManager.getPlaylists())
                .thenReturn(Observable.just(Collections.<PlaylistSimple>emptyList()));

        mMainPresenter.loadPlaylists();
        verify(mMockMainMvpView).showPlaylistsEmpty();
        verify(mMockMainMvpView, never()).showPlaylists(ArgumentMatchers.<PlaylistSimple>anyList());
        verify(mMockMainMvpView, never()).showError();
    }

    @Test
    public void loadPlaylistsFails() {
        when(mMockDataManager.getPlaylists())
                .thenReturn(Observable.<List<PlaylistSimple>>error(new RuntimeException()));

        mMainPresenter.loadPlaylists();
        verify(mMockMainMvpView).showError();
        verify(mMockMainMvpView, never()).showPlaylistsEmpty();
        verify(mMockMainMvpView, never()).showPlaylists(ArgumentMatchers.<PlaylistSimple>anyList());
    }

}