package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.MvpView;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public interface MainMvpView extends MvpView {

    void showError();
    void showPlaylists(List<PlaylistSimple> playlists);
    void showPlaylistsEmpty();
    void showLoginFragment();

    void showCreatePlaylistMessageError();
    void showCreatePlaylistNameError();

    void showLoginSuccessful(String accessToken);
    void showLoginFailed();

    void startCreatePlaylistService(String playlistName, String playlistMessage);

    void dismissCreatePlaylistDialog();
}
