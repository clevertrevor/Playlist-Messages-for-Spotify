package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.login;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.base.MvpView;

public interface LoginMvpView extends MvpView {

    void showSignInSuccessful();
    void showSignInCancelled();
    void showSignInFailure();

}
