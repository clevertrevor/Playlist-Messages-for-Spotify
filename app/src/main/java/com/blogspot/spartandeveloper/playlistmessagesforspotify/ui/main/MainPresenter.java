package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.DataManager;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.PreferencesHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.ConfigPersistent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.BasePresenter;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.RxUtil;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.LoadPlaylistsEvent;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import timber.log.Timber;

@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {

    private final DataManager mDataManager;
    private Disposable mDisposable;
    private PreferencesHelper prefs;

    @Inject
    public MainPresenter(DataManager dataManager, PreferencesHelper preferencesHelper) {
        mDataManager = dataManager;
        this.prefs = preferencesHelper;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposable != null) mDisposable.dispose();
        EventBus.getDefault().unregister(this);
    }

    public void loadPlaylists() {
        checkViewAttached();
        RxUtil.dispose(mDisposable);
        mDataManager.getPlaylists()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PlaylistSimple>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(List<PlaylistSimple> playlists) {
                        if (playlists.isEmpty()) {
                            getMvpView().showPlaylistsEmpty();
                        } else {
                            getMvpView().showPlaylists(playlists);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e( "There was an error loading the playlists.");
                        Timber.e(e);
                        getMvpView().showError();
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    public void createPlaylist(String playlistName, String playlistMessage) {
        Timber.d("createPlaylist: %s, %s", playlistName, playlistMessage);
        if (playlistMessage.equals("")) {
            getMvpView().showCreatePlaylistMessageError();
            return;
        } else if (playlistName.equals("")) {
            getMvpView().showCreatePlaylistNameError();
            return;
        }
        getMvpView().dismissCreatePlaylistDialog();
        getMvpView().startCreatePlaylistService(playlistName, playlistMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoadPlaylistsEvent event) {
        loadPlaylists();
    }


    public void handleSpotifyCallback(AuthenticationResponse response) {

        switch(response.getType()) {
        case TOKEN:
            Timber.i("successful Spotify login");
            prefs.setSpotifyAccessToken(response.getAccessToken());
            getMvpView().showLoginSuccessful(response.getAccessToken());
            long expireTime = (System.currentTimeMillis() / 1000) + response.getExpiresIn();
            prefs.setExpireTime(expireTime);
            loadPlaylists();
            break;
        case ERROR:
            Timber.i("failed Spotify login");
            getMvpView().showLoginFailed();
            break;
        default:
            Timber.i("user probably cancelled Spotify login");
            getMvpView().showLoginFailed();
        }

    }
}
