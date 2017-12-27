package com.blogspot.spartandeveloper.playlistmessagesforspotify;

class SpotifyInitException extends Exception {
    String message;
    public SpotifyInitException(String s) {
        message = s;
    }
}
