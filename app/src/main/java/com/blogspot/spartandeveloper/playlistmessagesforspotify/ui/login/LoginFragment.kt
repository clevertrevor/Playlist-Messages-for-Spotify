package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.login

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blogspot.spartandeveloper.playlistmessagesforspotify.R
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*


class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.spotifyLoginBtn.setOnClickListener {
            val CLIENT_ID = getString(R.string.spotify_client_id)
            val REDIRECT_URI = "playlistmessagesforspotify://callback"
            val builder = AuthenticationRequest.Builder(
                    CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
            val scopes = arrayOf("playlist-read-private", "playlist-modify-public", "playlist-modify-private", "playlist-read-collaborative")
            builder.setScopes(scopes)
            val request = builder.build()
            AuthenticationClient.openLoginInBrowser(activity, request)
        }

        return view
    }

    companion object {

        val TAG = "LoginFragment"

        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}