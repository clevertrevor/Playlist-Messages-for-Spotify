package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.afollestad.materialdialogs.Theme;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.MyApp;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.R;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.PreferencesHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.BaseActivity;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.createplaylist.CreatePlaylistService;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.login.LoginFragment;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.PlaylistAdapter.OnPlaylistItemClicked;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.DialogFactory;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.Util;
import com.scalified.fab.ActionButton;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView, OnPlaylistItemClicked {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "com.blogspot.spartandeveloper.playlistcreatorforspotify.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject MainPresenter mMainPresenter;
    @Inject PlaylistAdapter playlistAdapter;
    @Inject PreferencesHelper prefs;

    @BindView(R.id.rv_playlists) RecyclerView mRecyclerView;
    @BindView(R.id.fab_create_playlist_dialog) ActionButton fab;

    private MaterialDialog createPlaylistDialog;
    private int expireTime;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        playlistAdapter.setListener(this);
        mRecyclerView.setAdapter(playlistAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMainPresenter.attachView(this);

        fab.setOnClickListener(getFabOnClickListener());


        long threeMinutesFromNow = (System.currentTimeMillis() / 1000) + TimeUnit.MINUTES.toSeconds(3);
        if (prefs.getExpireTimeSeconds() < threeMinutesFromNow) {
            // user must login - open login fragment
            Timber.d("user must login");
            showLoginFragment();
            fab.hide();

        } else {
            mMainPresenter.loadPlaylists();
        }

    }

    @Override
    public void showLoginFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = LoginFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_container, loginFragment, LoginFragment.TAG);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, 0);
        fragmentTransaction.show(loginFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri spotifyUri = intent.getData();

        if(spotifyUri != null) {
            AuthenticationResponse response = AuthenticationResponse.fromUri(spotifyUri);
            switch(response.getType()) {
            case TOKEN:
                Timber.i("successful Spotify login");
                ((MyApp) getApplicationContext()).initSpotifyService(response.getAccessToken());
                setUserDetails();
                prefs.setSpotifyAccessToken(response.getAccessToken());
                setExpireTime(response.getExpiresIn());
                mMainPresenter.loadPlaylists();
                hideLoginFragment();
                break;
            case ERROR:
                Timber.i("failed Spotify login");
                break;
            default:
                Timber.i("user probably cancelled Spotify login");
            }
        }
    }

    private void hideLoginFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(LoginFragment.TAG);
        fragmentTransaction.hide(loginFragment);
        fragmentTransaction.commit();
    }

    private void setUserDetails() {
        final SpotifyService spotify = ((MyApp)getApplicationContext()).getSpotifyService();
        Observable<Void> userPrivateObservable = Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                UserPrivate userPrivate = spotify.getMe();
                prefs.setSpotifyUserId(userPrivate.id);
                return null;
            };
        });

        userPrivateObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    private OnClickListener getFabOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlaylistDialog = new MaterialDialog.Builder(MainActivity.this)
                        .title(getString(R.string.enter_pl_info))
                        .theme(Theme.DARK)
                        .customView(R.layout.dialog_create_playlist, false)
                        .onPositive(getPositiveCallback())
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .show();
            }
        };
    }

    private SingleButtonCallback getPositiveCallback() {
        return new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                TextInputEditText playlistNameEt = ButterKnife.findById(dialog, R.id.et_playlist_name);
                TextInputEditText playlistMessageEt = ButterKnife.findById(dialog, R.id.et_playlist_message);

                String name = playlistNameEt.getText().toString();
                String message = playlistMessageEt.getText().toString();

                mMainPresenter.createPlaylist(name, message);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_privacy_policy:{
            openPrivacyPolicy();
            return true;
        }
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void openPrivacyPolicy() {
        MaterialDialog.Builder builder = new Builder(this)
                .content(getString(R.string.privacy_policy_description))
                .positiveText(getString(android.R.string.ok))
                .positiveColor(ContextCompat.getColor(this, android.R.color.black));
        builder.show();
    }

    /***** MVP View methods implementation *****/


    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_playlists))
                .show();
    }

    @Override
    public void showPlaylists(List<PlaylistSimple> playlists) {
        playlistAdapter.setPlaylists(playlists);
    }

    @Override
    public void showPlaylistsEmpty() {
        playlistAdapter.setPlaylists(Collections.<PlaylistSimple>emptyList());
        Toast.makeText(this, R.string.empty_playlists, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showCreatePlaylistNameAndMessageError() {
        showCreatePlaylistNameError();
        showCreatePlaylistMessageError();
    }

    @Override
    public void showCreatePlaylistMessageError() {
        TextInputLayout layout = ButterKnife.findById(createPlaylistDialog, R.id.layout_playlist_message);
        layout.setError("Enter a message");
    }

    @Override
    public void showCreatePlaylistNameError() {
        TextInputLayout layout = ButterKnife.findById(createPlaylistDialog, R.id.layout_playlist_name);
        layout.setError("Enter a playlist name");
    }

    @Override
    public void startCreatePlaylistService(String playlistName, String playlistMessage) {
        Timber.i("startCreatePlaylistService");
        Intent intent = new Intent(this, CreatePlaylistService.class);
        intent.putExtra(CreatePlaylistService.PLAYLIST_NAME, playlistName);
        intent.putExtra(CreatePlaylistService.PLAYLIST_MESSAGE, playlistMessage);
        intent.putExtra(CreatePlaylistService.USER_ID, prefs.getSpotifyUserId());
        startService(intent);
    }

    @Override
    public void onPlaylistItemClicked(String uri) {
        if (Util.isSpotifyInstalled(this)) {
            Intent launcher = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(launcher);
        } else {
            Toast.makeText(this, getString(R.string.toast_spotify_not_installed), Toast.LENGTH_SHORT).show();
        }
    }

    private void setExpireTime(int remainingSeconds) {
        long expireTime = (System.currentTimeMillis() / 1000) + remainingSeconds;
        prefs.setExpireTime(expireTime);
    }
}