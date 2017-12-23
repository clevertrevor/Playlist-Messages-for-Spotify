package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.R;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.BaseActivity;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.PlaylistAdapter.OnPlaylistItemClicked;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.DialogFactory;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.Util;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView, OnPlaylistItemClicked {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "com.blogspot.spartandeveloper.playlistcreatorforspotify.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject MainPresenter mMainPresenter;
    @Inject PlaylistAdapter playlistAdapter;

    @BindView(R.id.rv_playlists) RecyclerView mRecyclerView;

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

//        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
//            startService(SyncService.getStartIntent(this));
//        }

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            Timber.d("EXTRA_TRIGGER_SYNC_FLAG true");
            String CLIENT_ID = "76edc333f1f74a99878e80d5b2874372";
            String REDIRECT_URI = "playlistmessagesforspotify://callback";
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
            String[] scopes = new String[]{"playlist-read-private", "playlist-read-collaborative"};
            builder.setScopes(scopes);
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginInBrowser(this, request);
        } else {
            // test code
            mMainPresenter.loadPlaylists(null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri spotifyUri = intent.getData();

        if (spotifyUri != null) {
            AuthenticationResponse response = AuthenticationResponse.fromUri(spotifyUri);
            switch(response.getType()) {
            case TOKEN:
                Timber.i("successful Spotify login");
                mMainPresenter.loadPlaylists(response);
                break;
            case ERROR:
                Timber.i("failed Spotify login");
                break;
            default:
                Timber.i("user probably cancelled Spotify login");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMainPresenter.detachView();
    }

    @OnClick(R.id.fab_create_playlist_dialog)
    void openCreatePlaylistDialog() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.enter_pl_info))
                .theme(Theme.DARK)
                .customView(R.layout.dialog_create_playlist, false)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .show();
    }

    /***** MVP View methods implementation *****/


    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_ribots))
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
    public void onPlaylistItemClicked(String uri) {
        if (Util.isSpotifyInstalled(this)) {
            Intent launcher = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(launcher);
        } else {
            Toast.makeText(this, getString(R.string.toast_spotify_not_installed), Toast.LENGTH_SHORT).show();
        }
    }
}