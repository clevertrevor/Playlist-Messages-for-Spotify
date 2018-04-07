package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.MyApp;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.R;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.CreatePlaylistService;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.local.PreferencesHelper;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.BaseActivity;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.login.LoginFragment;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.PlaylistAdapter.OnPlaylistItemClicked;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.DialogFactory;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.Util;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.CreatePlaylistErrorEvent;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.CreatePlaylistSuccessEvent;
import com.scalified.fab.ActionButton;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

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
    private TextInputEditText playlistNameEt;

    private android.support.v7.app.AlertDialog createPlaylistDialog;

    /**
     * Return an Intent to start this Activity.
     * cruft: triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        String accessToken = prefs.getSpotifyAccessToken();
        ((MyApp) getApplicationContext()).initSpotifyService(accessToken);

        playlistAdapter.setListener(this);
        mRecyclerView.setAdapter(playlistAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMainPresenter.attachView(this);

        createCreatePlaylistDialog();
        fab.setOnClickListener(getFabOnClickListener());

        mMainPresenter.handleLogin();

    }

    @Override
    protected void onResume() {
        super.onResume();
        final SpotifyService spotify = ((MyApp)getApplicationContext()).getSpotifyService();
        if (spotify != null) {
            setUserDetails();
        }
    }

    @Override
    public void showLoginFragment() {
        fab.hide();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = LoginFragment.Companion.newInstance();
        fragmentTransaction.add(R.id.fragment_container, loginFragment, LoginFragment.Companion.getTAG());
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, 0);
        fragmentTransaction.show(loginFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri spotifyUri = intent.getData();

        if (spotifyUri != null) {
            AuthenticationResponse response = AuthenticationResponse.fromUri(spotifyUri);
            mMainPresenter.handleSpotifyCallback(response);
        }
    }

    private void hideLoginFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(LoginFragment.Companion.getTAG());
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
        EventBus.getDefault().unregister(this);
    }

    private OnClickListener getFabOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {


                createPlaylistDialog.show();

                playlistNameEt = (TextInputEditText) createPlaylistDialog.findViewById(R.id.et_playlist_name);
                TextInputEditText playlistMessageDesc = (TextInputEditText) createPlaylistDialog.findViewById(R.id.et_playlist_message);
                playlistNameEt.setText("I love you");
                playlistMessageDesc.setText("I love you");
        }};
    }

    private void createCreatePlaylistDialog() {

        View layout = getLayoutInflater().inflate(R.layout.dialog_create_playlist, null);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
                .setTitle(getString(R.string.enter_pl_info))
                .setView(layout)
                .setPositiveButton(getString(android.R.string.search_go), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextInputEditText playlistNameEt = ButterKnife.findById(
                                MainActivity.this.createPlaylistDialog, R.id.et_playlist_name);
                        TextInputEditText playlistMessageEt = ButterKnife.findById(
                                MainActivity.this.createPlaylistDialog, R.id.et_playlist_message);

                        String name = playlistNameEt.getText().toString();
                        String message = playlistMessageEt.getText().toString();

                        mMainPresenter.createPlaylist(name, message);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // placeholder to show 'cancel' button
                    }
                });


        createPlaylistDialog = builder.create();

        // remove empty space at top
        AppCompatTextView uselessTitleToHide = (AppCompatTextView) createPlaylistDialog.findViewById(android.R.id.title);
        if (uselessTitleToHide != null) uselessTitleToHide.setVisibility(View.GONE);

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
    public void showCreatePlaylistMessageError() {
        TextInputLayout layout = ButterKnife.findById(createPlaylistDialog, R.id.layout_playlist_message);
        layout.setError(getString(R.string.error_enter_a_message));
    }

    @Override
    public void showCreatePlaylistNameError() {
        TextInputLayout layout = ButterKnife.findById(createPlaylistDialog, R.id.layout_playlist_name);
        layout.setError(getString(R.string.error_enter_a_playlist_name));
    }

    @Override
    public void showLoginSuccessful(String accessToken) {
        Toast.makeText(this, R.string.logged_in, Toast.LENGTH_SHORT).show();
        fab.show();
        setUserDetails();
        ((MyApp) getApplicationContext()).initSpotifyService(accessToken);
        hideLoginFragment();
    }

    @Override
    public void showLoginFailed() {
        Toast.makeText(this, R.string.failed_to_login, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startCreatePlaylistService(String playlistName, String playlistMessage) {
        Timber.i("startCreatePlaylistService");
        Intent intent = CreatePlaylistService.newInstance(this, playlistName, playlistMessage,
                prefs.getSpotifyUserId());
        startService(intent);
    }

    @Override
    public void dismissCreatePlaylistDialog() {
        createPlaylistDialog.dismiss();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CreatePlaylistSuccessEvent event) {
        String text = getString(R.string.successfully_created_playlist) + " \"" + event.playlistName + "\"";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CreatePlaylistErrorEvent event) {
        String text = getString(R.string.failed_to_find) + " \"" + event.word + "\"";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


}