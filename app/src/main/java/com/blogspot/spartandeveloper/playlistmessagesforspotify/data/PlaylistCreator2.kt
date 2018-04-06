package com.blogspot.spartandeveloper.playlistmessagesforspotify.data

import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.CreatePlaylistErrorEvent
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.CreatePlaylistSuccessEvent
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.LoadPlaylistsEvent
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.Track
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*

/**
 * Search for playlist names by largest length.
 */
internal class PlaylistCreator2 constructor (val userId: String, val spotify: SpotifyService,
                                             val playlistName: String, val message: String) {


    init {


    }

    fun execute() {

        val split = message.trim().split(" ")

        val tmp = mutableListOf<Track>()
        if (executeUtil(split, 0, tmp, 1) == false) {
            // FIXME change err msg
            EventBus.getDefault().post(CreatePlaylistErrorEvent(""))
            // create pl using tmp
        }

        EventBus.getDefault().post(LoadPlaylistsEvent())
        EventBus.getDefault().post(CreatePlaylistSuccessEvent(playlistName))

    }

    private fun executeUtil(userQueryList: List<String>, start: Int, tmp: Stack<Track>, end: Int): Boolean {

        // tmp contains a full playlist -- success
        if (isTrackListInStringList(userQueryList, tmp))  {
            return true
        }

        // failed to find a query
        if (end > userQueryList.size) {
            // backtrack
            // TODO
            return false
        }


        // create search query
        val sb = StringBuilder()
        for (i in start..end - 2) sb.append(userQueryList.get(i)).append(" ")
        sb.append(userQueryList.get(end-1))
        val query = sb.toString()
        var foundTrack = false

        var offset = 0
        val options = HashMap<String, Any>()
        options["limit"] = 50
        options["offset"] = offset

        // paginated search until found query or max offset
        while (offset <= 450 && !foundTrack) { // max offset is 450

            val pager = spotify.searchTracks(query, options)

            for (track in pager.tracks.items) {
                val trackName = track.name
                if (trackName.equals(query, ignoreCase = true)) {
                    Timber.i("found word: %s", trackName)
                    tmp.add(track)
                    foundTrack = true
                    break
                }
            }

            offset += 50
        }


        if (foundTrack) {
            val newStart = end + 1
            val newEnd = newStart + 1

            return executeUtil(userQueryList, newStart, tmp, newEnd)
        } else {
            return executeUtil(userQueryList, start, tmp, end + 1)
        }
    }

    // return true if the given track list's names are equivalent to the given String list
    private fun isTrackListInStringList(p0: List<String>, p1: List<Track>): Boolean {

        if (p0.size != p1.size) {
            return false
        }

        val sb0 = StringBuilder()
        val sb1 = StringBuilder()

        for (str in p0) { sb0.append(str).append(" ") }
        for (track in p1) { sb1.append(track.name).append(" ") }

        return sb0.toString().equals(sb1.toString(), true)
    }

}