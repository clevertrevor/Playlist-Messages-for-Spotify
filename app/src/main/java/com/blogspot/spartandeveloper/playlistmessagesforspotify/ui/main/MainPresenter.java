package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.DataManager;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.PreferencesHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.injection.ConfigPersistent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.BasePresenter;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.RxUtil;

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
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposable != null) mDisposable.dispose();
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
        if (playlistMessage == null && playlistName == null) {
            getMvpView().showCreatePlaylistNameAndMessageError();
        }
//        if (TextUtils.isEmpty(playlistName) && TextUtils.isEmpty(playlistMessage)) {
//            getMvpView().showCreatePlaylistNameAndMessageError();
//        } else if (TextUtils.isEmpty(playlistName)) {
//            getMvpView().showCreatePlaylistNameError();
//        } else if (TextUtils.isEmpty(playlistMessage)) {
//            getMvpView().showCreatePlaylistMessageError();
//        }

        getMvpView().startCreatePlaylistService(playlistName, playlistMessage);
    }

//    public void loadRibots() {
//        checkViewAttached();
//        RxUtil.dispose(mDisposable);
//        mDataManager.getRibots()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Observer<List<Ribot>>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        mDisposable = d;
//                    }
//
//                    @Override
//                    public void onNext(@NonNull List<Ribot> ribots) {
//                        if (ribots.isEmpty()) {
//                            getMvpView().showRibotsEmpty();
//                        } else {
//                            getMvpView().showRibots(ribots);
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Timber.e(e, "There was an error loading the ribots.");
//                        getMvpView().showError();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

}
