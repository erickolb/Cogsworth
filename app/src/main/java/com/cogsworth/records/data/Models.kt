package com.cogsworth.records.data

import com.google.gson.annotations.SerializedName

data class FolderResponse(val folders: List<DiscogsFolder> = emptyList())
data class DiscogsFolder(val id: Int, val name: String, val count: Int)
data class CollectionResponse(val pagination: Pagination, val releases: List<CollectionItem> = emptyList())
data class Pagination(val page: Int = 1, val pages: Int = 1)
data class CollectionItem(
    val id: Long,
    @SerializedName("instance_id") val instanceId: Long,
    @SerializedName("basic_information") val basic: BasicInformation
)
data class BasicInformation(
    val id: Long,
    val title: String,
    val year: Int = 0,
    @SerializedName("cover_image") val coverImage: String? = null,
    val thumb: String? = null,
    val artists: List<NamedValue> = emptyList(),
    val labels: List<NamedValue> = emptyList(),
    val formats: List<DiscogsFormat> = emptyList(),
    val genres: List<String> = emptyList(),
    val styles: List<String> = emptyList()
) {
    val artistName: String get() = artists.joinToString(", ") { it.name }
    val labelName: String get() = labels.joinToString(", ") { it.name }.ifBlank { "Unknown label" }
    val formatName: String get() = formats.joinToString(", ") { f ->
        listOfNotNull(f.name, f.descriptions?.joinToString(" / ")).joinToString(" · ")
    }.ifBlank { "Unknown format" }
}
data class NamedValue(val name: String)
data class DiscogsFormat(val name: String? = null, val descriptions: List<String>? = null)
