package com.cogsworth.records

import com.cogsworth.records.data.BasicInformation
import com.cogsworth.records.data.CollectionItem
import com.cogsworth.records.data.DiscogsFolder
import com.cogsworth.records.data.NamedValue
import org.junit.Assert.assertEquals
import org.junit.Test

class AppStateTest {
    private fun record(id: Long, artist: String, title: String) = CollectionItem(
        id = id,
        instanceId = id,
        basic = BasicInformation(id = id, title = title, artists = listOf(NamedValue(artist)))
    )

    private val records = listOf(record(1, "Zappa, Frank", "Apostrophe"), record(2, "Bowie, David", "Ziggy Stardust"))

    @Test fun sortsByArtist() {
        assertEquals(listOf("Bowie, David", "Zappa, Frank"), AppState(releases = records).visibleReleases.map { it.basic.artistName })
    }

    @Test fun sortsByAlbum() {
        assertEquals(listOf("Apostrophe", "Ziggy Stardust"), AppState(releases = records, sort = SortMode.ALBUM).visibleReleases.map { it.basic.title })
    }

    @Test fun searchesArtistAndAlbumIgnoringCase() {
        assertEquals(1, AppState(releases = records, query = "bowie").visibleReleases.size)
        assertEquals(1, AppState(releases = records, query = "APOSTROPHE").visibleReleases.size)
    }

    @Test fun describesTwoSelectedCollections() {
        val folders = listOf(DiscogsFolder(1, "Lounge", 12), DiscogsFolder(2, "Soundtracks", 8))
        val state = AppState(folders = folders, selectedFolderIds = setOf(1, 2))
        assertEquals("Lounge + Soundtracks", state.collectionTitle)
    }
}
