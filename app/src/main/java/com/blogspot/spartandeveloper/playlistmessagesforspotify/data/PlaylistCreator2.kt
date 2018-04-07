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

    private val MAX_SPOTIFY_OFFSET = 450
    private val INC_SPOTIFY_OFFSET = 50

    fun execute(): Boolean {

        val split = message.trim().split(" ")

        val it = split.iterator()
        val userQuery = LinkedList<String>()
        while (it.hasNext()) userQuery.addLast(it.next())
        val result = LinkedList<Track>()

        if (!executeUtil(userQuery, result)) {
            // FIXME change err msg
            EventBus.getDefault().post(CreatePlaylistErrorEvent(""))
            return false
        } else {
            EventBus.getDefault().post(LoadPlaylistsEvent())
            EventBus.getDefault().post(CreatePlaylistSuccessEvent(playlistName))
            return true
        }

    }

    private fun executeUtil(userQuery: MutableList<String>, result: MutableList<Track>): Boolean {

        // finished without any remaining words
        if (userQuery.isEmpty()) {
            return true
        }

        // iterate over all options
        for (i in 0 until userQuery.size) {

            val searchList = userQuery.subList(0, i + 1)
            val sb = StringBuilder()
            for (s: String in searchList) {
                sb.append(s).append(" ")
            }

            val subQuery = sb.toString().trim() // need to change to grab multiple words

            val foundTrack = search(subQuery)

            if (foundTrack != null) {

                // track successes
                result.add(foundTrack)
                for (s: String in searchList) {
                    userQuery.remove(s)
                }


                // bubble up successful query
                if (executeUtil(userQuery, result)) {
                    return true
                }

                // backtrack for failure
                result.remove(foundTrack)
                for (s: String in searchList) {
                    userQuery.add(0, subQuery)
                }
            }

        }

        return false
    }

    private fun search(query: String): Track? {

        var offset = 0
        val options = HashMap<String, Any>()
        options["limit"] = 50
        options["offset"] = offset

        // paginated search until found query or max offset
        while (offset <= MAX_SPOTIFY_OFFSET) {

            val pager = spotify.searchTracks(query, options)

            for (track in pager.tracks.items) {
                val trackName = track.name
                if (trackName.equals(query, ignoreCase = true)) {
                    Timber.i("found word: %s", trackName)
                    return track
                }
            }

            offset += INC_SPOTIFY_OFFSET
            options["offset"] = offset

        }

        return null
    }


}