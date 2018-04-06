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

    fun execute() {

        val split = message.trim().split(" ")

        val it = split.iterator()
        val userQuery = LinkedList<String>()
        while (it.hasNext()) userQuery.addLast(it.next())
        val result = LinkedList<Track>()

        if (!executeUtil(userQuery, result)) {
            // FIXME change err msg
            EventBus.getDefault().post(CreatePlaylistErrorEvent(""))
        } else {
            EventBus.getDefault().post(LoadPlaylistsEvent())
            EventBus.getDefault().post(CreatePlaylistSuccessEvent(playlistName))
        }

    }

    private fun executeUtil(userQuery: MutableList<String>, result: MutableList<Track>): Boolean {

        // finished without any remaining words
        if (userQuery.isEmpty()) {
            return true
        }

        // iterate over all options
        for (i in 0 until userQuery.size) {

            val str = userQuery[i] // will need to change to grab multiple words

            val foundTrack = search(str)

            if (foundTrack != null) {

                result.add(foundTrack)
                userQuery.remove(str)

                if (executeUtil(userQuery, result)) {
                    // bubble up successful query
                    return true
                }

                // backtrack -- not tested
                result.remove(foundTrack)
                userQuery.add(0, str)
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