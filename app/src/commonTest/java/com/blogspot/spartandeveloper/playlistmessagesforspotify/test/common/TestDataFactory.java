package com.blogspot.spartandeveloper.playlistmessagesforspotify.test.common;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.model.Name;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.model.Profile;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.data.model.Ribot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
public class TestDataFactory {

    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    public static Ribot makeRibot(String uniqueSuffix) {
        return Ribot.create(makeProfile(uniqueSuffix));
    }

    public static List<Ribot> makeListRibots(int number) {
        List<Ribot> ribots = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            ribots.add(makeRibot(String.valueOf(i)));
        }
        return ribots;
    }

    public static Profile makeProfile(String uniqueSuffix) {
        return Profile.builder()
                .setName(makeName(uniqueSuffix))
                .setEmail("email" + uniqueSuffix + "@ribot.com.uk")
                .setDateOfBirth(new Date())
                .setHexColor("#0066FF")
                .setAvatar("http://api.ribot.io/images/" + uniqueSuffix)
                .setBio(randomUuid())
                .build();
    }

    public static Name makeName(String uniqueSuffix) {
        return Name.create("Name-" + uniqueSuffix, "Surname-" + uniqueSuffix);
    }

    public static List<PlaylistSimple> makePlaylists(int i) {
        List<PlaylistSimple> list = new ArrayList<>();
        for (; i > 0; i--) {
            PlaylistSimple newPlaylist = new PlaylistSimple();
            newPlaylist.name = Integer.toString(i);
            list.add(newPlaylist);
        }
        return list;
    }

    // returns 1 playlist with all info except for tracks
    public static List<PlaylistSimple> makeActualPlaylist() {

        PlaylistSimple playlist = new PlaylistSimple();
        playlist.id = "7aKvz8nVEMeugOfME0bjui";
        playlist.href = "https://api.spotify.com/v1/users/1243473592/playlists/7aKvz8nVEMeugOfME0bjui";
        playlist.name = "Aluna! ";
        playlist.uri = "spotify:user:1243473592:playlist:7aKvz8nVEMeugOfME0bjui";

        List<PlaylistSimple> list = new ArrayList<>();
        list.add(playlist);
        return list;

    }
}