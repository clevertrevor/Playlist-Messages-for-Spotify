package com.blogspot.spartandeveloper.playlistmessagesforspotify.data

import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.CreatePlaylistErrorEvent
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.CreatePlaylistSuccessEvent
import com.blogspot.spartandeveloper.playlistmessagesforspotify.util.events.LoadPlaylistsEvent
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.Track
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*

internal class PlaylistCreator (val spotify: SpotifyService, val playlistName: String, val message: String) {

    private val MAX_SPOTIFY_OFFSET = 450
    private val INC_SPOTIFY_OFFSET = 50

    fun execute(): Boolean {

        val split = message.trim().split(" ")

        val it = split.iterator()
        val userQuery = LinkedList<String>()
        while (it.hasNext()) userQuery.addLast(it.next())
        val result = LinkedList<Track>()

        if (!executeUtil(userQuery = userQuery, result = result)) {
            // FIXME change err msg
            EventBus.getDefault().post(CreatePlaylistErrorEvent(""))
            return false
        } else {
            EventBus.getDefault().post(LoadPlaylistsEvent())
            EventBus.getDefault().post(CreatePlaylistSuccessEvent(playlistName))
            return true
        }

    }

    private fun executeUtil(userQuery: MutableList<String>, startIndex: Int = 0, result: MutableList<Track>): Boolean {

        // finished; no any remaining words
        if (startIndex == userQuery.size) {
            return true
        }

        for (i in startIndex until userQuery.size) {

            // create query for search range
            val searchList = userQuery.subList(startIndex, i + 1)
            val sb = StringBuilder()
            for (s: String in searchList) {
                sb.append(s).append(" ")
            }
            val subQuery = sb.toString().trim()

            val foundTrack = search(subQuery)

            if (foundTrack != null) {

                // track successes
                result.add(foundTrack)

                // bubble up successful query
                if (executeUtil(userQuery, i + 1 , result)) {
                    return true
                }

                // backtrack for failure
                result.remove(foundTrack)
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