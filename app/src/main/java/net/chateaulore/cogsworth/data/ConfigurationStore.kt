package net.chateaulore.cogsworth.data

import android.content.Context

data class Configuration(
    val displayDetailRatings: Boolean = true,
    val allowEditingRatings: Boolean = true,
    val displayListingRatings: Boolean = true
) {
    val canEditRatings: Boolean get() = displayDetailRatings && allowEditingRatings
    fun withDetailRatings(enabled: Boolean) = copy(
        displayDetailRatings = enabled,
        allowEditingRatings = if (enabled) allowEditingRatings else false
    )
}

class ConfigurationStore(context: Context) {
    private val prefs = context.getSharedPreferences("configuration", Context.MODE_PRIVATE)
    fun load() = Configuration(
        prefs.getBoolean("detailRatings", true),
        prefs.getBoolean("editRatings", true),
        prefs.getBoolean("listingRatings", true)
    ).let { if (it.displayDetailRatings) it else it.withDetailRatings(false) }
    fun save(value: Configuration) {
        prefs.edit().putBoolean("detailRatings", value.displayDetailRatings)
            .putBoolean("editRatings", value.canEditRatings)
            .putBoolean("listingRatings", value.displayListingRatings).apply()
    }
}
