package com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events;

public class CreatePlaylistSuccessEvent {
    public final String playlistName;
    public CreatePlaylistSuccessEvent(String playlistName){
        this.playlistName = playlistName;
    }

}
