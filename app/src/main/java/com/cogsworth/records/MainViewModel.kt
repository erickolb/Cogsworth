package com.cogsworth.records

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cogsworth.records.data.CollectionItem
import com.cogsworth.records.data.DiscogsFolder
import com.cogsworth.records.data.DiscogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SortMode { ARTIST, ALBUM }

data class AppState(
    val configured: Boolean = false,
    val loading: Boolean = false,
    val folders: List<DiscogsFolder> = emptyList(),
    val selectedFolderIds: Set<Int> = emptySet(),
    val libraryVisible: Boolean = false,
    val releases: List<CollectionItem> = emptyList(),
    val selectedRelease: CollectionItem? = null,
    val changeCollectionVisible: Boolean = false,
    val notice: String? = null,
    val acknowledgementsVisible: Boolean = false,
    val query: String = "",
    val sort: SortMode = SortMode.ARTIST,
    val error: String? = null
) {
    val selectedFolders: List<DiscogsFolder> get() = folders.filter { it.id in selectedFolderIds }
    val collectionTitle: String get() = when (selectedFolders.size) {
        0 -> "Collection"
        1 -> selectedFolders.first().name
        2 -> selectedFolders.joinToString(" + ") { it.name }
        else -> "${selectedFolders.size} collections"
    }

    val visibleReleases: List<CollectionItem> get() {
        val filtered = if (query.isBlank()) releases else releases.filter {
            it.basic.artistName.contains(query, true) ||
                it.basic.title.contains(query, true) ||
                it.basic.labelName.contains(query, true) ||
                it.basic.genres.any { genre -> genre.contains(query, true) }
        }
        return when (sort) {
            SortMode.ARTIST -> filtered.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.basic.artistName })
            SortMode.ALBUM -> filtered.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.basic.title })
        }
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DiscogsRepository(application)
    private val _state = MutableStateFlow(AppState(configured = repository.credentials.token() != null))
    val state: StateFlow<AppState> = _state.asStateFlow()

    init { if (_state.value.configured) loadFolders() }

    fun configure(username: String, token: String) {
        if (username.isBlank() || token.isBlank()) {
            _state.value = _state.value.copy(error = "Enter both your Discogs username and token.")
            return
        }
        repository.credentials.save(username, token)
        _state.value = AppState(configured = true)
        loadFolders()
    }

    fun loadFolders() = launchRequest {
        _state.value = _state.value.copy(folders = repository.folders())
    }

    fun toggleFolder(folder: DiscogsFolder) {
        val selected = _state.value.selectedFolderIds
        _state.value = _state.value.copy(
            selectedFolderIds = if (folder.id in selected) selected - folder.id else selected + folder.id
        )
    }

    fun browseSelectedFolders() {
        val folders = _state.value.selectedFolders
        if (folders.isEmpty()) return
        launchRequest {
            _state.value = _state.value.copy(libraryVisible = true, releases = emptyList())
            val combined = folders.flatMap { repository.releases(it.id) }.distinctBy { it.instanceId }
            _state.value = _state.value.copy(releases = combined)
        }
    }

    fun setQuery(value: String) { _state.value = _state.value.copy(query = value) }
    fun setSort(value: SortMode) { _state.value = _state.value.copy(sort = value) }
    fun show(item: CollectionItem) { _state.value = _state.value.copy(selectedRelease = item) }
    fun shuffle() { _state.value.releases.randomOrNull()?.let(::show) }
    fun dismissDetail() { _state.value = _state.value.copy(selectedRelease = null) }
    fun showChangeCollection() { _state.value = _state.value.copy(changeCollectionVisible = true) }
    fun dismissChangeCollection() { _state.value = _state.value.copy(changeCollectionVisible = false) }
    fun dismissNotice() { _state.value = _state.value.copy(notice = null) }

    fun moveSelectedRelease(destination: DiscogsFolder) {
        val item = _state.value.selectedRelease ?: return
        launchRequest {
            repository.move(item, destination.id)
            val movedItem = item.copy(folderId = destination.id)
            val oldFolderId = item.folderId
            _state.value = _state.value.copy(
                releases = if (destination.id in _state.value.selectedFolderIds) {
                    _state.value.releases.map { if (it.instanceId == item.instanceId) movedItem else it }
                } else {
                    _state.value.releases.filterNot { it.instanceId == item.instanceId }
                },
                folders = _state.value.folders.map { folder ->
                    when (folder.id) {
                        oldFolderId -> folder.copy(count = (folder.count - 1).coerceAtLeast(0))
                        destination.id -> folder.copy(count = folder.count + 1)
                        else -> folder
                    }
                },
                selectedRelease = null,
                changeCollectionVisible = false,
                notice = "Moved to ${destination.name}"
            )
        }
    }
    fun showAcknowledgements() { _state.value = _state.value.copy(acknowledgementsVisible = true) }
    fun dismissAcknowledgements() { _state.value = _state.value.copy(acknowledgementsVisible = false) }
    fun backToFolders() { _state.value = _state.value.copy(libraryVisible = false, releases = emptyList(), query = "") }
    fun dismissError() { _state.value = _state.value.copy(error = null) }
    fun signOut() { repository.credentials.clear(); _state.value = AppState() }

    private fun launchRequest(block: suspend () -> Unit) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        try { block() } catch (e: Exception) {
            _state.value = _state.value.copy(error = e.message ?: "Discogs could not be reached.")
        } finally { _state.value = _state.value.copy(loading = false) }
    }
}
