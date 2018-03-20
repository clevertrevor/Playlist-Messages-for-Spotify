package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.R;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_spotify_login)
    void onSpotifyLoginButtonClicked() {
        String CLIENT_ID = getString(R.string.spotify_client_id);
        String REDIRECT_URI = "playlistmessagesforspotify://callback";
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
                CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        String[] scopes = new String[]{"playlist-read-private", "playlist-modify-public",
                "playlist-modify-private", "playlist-read-collaborative"};
        builder.setScopes(scopes);
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginInBrowser(getActivity(), request);
    }

}
