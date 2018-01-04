package com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events;

/**
 * Wraps a missing word after failing to find it when creating a playlist
 */
public class CreatePlaylistErrorEvent {
    public final String word;
    public CreatePlaylistErrorEvent(String missingWord) {
        this.word = missingWord;
    }
}
