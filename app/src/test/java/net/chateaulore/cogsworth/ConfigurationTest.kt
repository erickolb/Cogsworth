package net.chateaulore.cogsworth

import net.chateaulore.cogsworth.data.Configuration
import net.chateaulore.cogsworth.data.CollectionItem
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class ConfigurationTest {
    @Test fun allRatingOptionsDefaultOn() {
        val config = Configuration()
        assertTrue(config.displayDetailRatings)
        assertTrue(config.canEditRatings)
        assertTrue(config.displayListingRatings)
    }
    @Test fun hidingDetailsTurnsEditingOffAndPreservesListingPreference() {
        val hidden = Configuration().withDetailRatings(false)
        assertFalse(hidden.allowEditingRatings)
        assertFalse(hidden.canEditRatings)
        assertTrue(hidden.displayListingRatings)
        assertFalse(hidden.withDetailRatings(true).canEditRatings)
        assertTrue(hidden.withDetailRatings(true).copy(allowEditingRatings = true).canEditRatings)
    }
    @Test fun hiddenRatingsCannotBeEditedEvenWithStalePreference() {
        assertFalse(Configuration(displayDetailRatings = false).canEditRatings)
    }
    @Test fun readsPersonalCollectionRatingAndDefaultsUnratedToZero() {
        val gson = Gson()
        val base = """{"id":1,"instance_id":2,"folder_id":3,"basic_information":{"id":1,"title":"Album"}"""
        assertEquals(5, gson.fromJson(base + ",\"rating\":5}", CollectionItem::class.java).rating)
        assertEquals(0, gson.fromJson(base + "}", CollectionItem::class.java).rating)
    }
}
