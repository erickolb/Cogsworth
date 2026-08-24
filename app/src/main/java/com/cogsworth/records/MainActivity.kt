package com.cogsworth.records

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cogsworth.records.data.CollectionItem
import com.cogsworth.records.data.DiscogsFolder

private val Night = Color(0xFF101014)
private val Charcoal = Color(0xFF1A1A20)
private val Cloud = Color(0xFFF4F1F8)
private val Lavender = Color(0xFFA9A7FF)

class MainActivity : ComponentActivity() {
    private val model: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CogsworthTheme { CogsworthApp(model) } }
    }
}

@Composable private fun CogsworthTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Lavender,
            onPrimary = Color(0xFF17152A),
            primaryContainer = Color(0xFF353358),
            onPrimaryContainer = Color(0xFFE4E1FF),
            secondary = Color(0xFFC8BFFF),
            background = Night,
            onBackground = Cloud,
            surface = Charcoal,
            onSurface = Cloud,
            surfaceVariant = Color(0xFF25252D),
            onSurfaceVariant = Color(0xFFCAC6D0),
            outline = Color(0xFF918D99)
        ),
        content = content
    )
}

@Composable private fun CogsworthApp(model: MainViewModel) {
    val state by model.state.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxSize(), color = Night) {
        when {
            !state.configured -> SetupScreen(model::configure)
            state.acknowledgementsVisible -> AcknowledgementsScreen(model::dismissAcknowledgements)
            state.selectedRelease != null -> RecordDetail(state.selectedRelease!!, model::dismissDetail)
            state.libraryVisible -> LibraryScreen(state, model)
            else -> FolderScreen(state, model)
        }
        if (state.loading) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .12f)), Alignment.Center) { CircularProgressIndicator() }
    }
    state.error?.let { error ->
        AlertDialog(onDismissRequest = model::dismissError, confirmButton = { TextButton(onClick = model::dismissError) { Text("OK") } },
            title = { Text("Something went wrong") }, text = { Text(error) })
    }
}

@Composable private fun SetupScreen(onSave: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center) {
        Text("COGSWORTH", letterSpacing = 4.sp, fontWeight = FontWeight.Black, fontSize = 32.sp)
        Text("Your shelves, shuffled.", color = Lavender, fontSize = 18.sp)
        Spacer(Modifier.height(36.dp))
        Text("Connect Discogs once", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("Create a personal token in Discogs → Settings → Developers. It stays encrypted on this device.", modifier = Modifier.padding(vertical = 10.dp))
        OutlinedTextField(username, { username = it }, label = { Text("Discogs username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(token, { token = it }, label = { Text("Personal API token") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Button({ onSave(username, token) }, Modifier.fillMaxWidth().padding(top = 20.dp)) { Text("Connect collection") }
    }
}

@Composable private fun FolderScreen(state: AppState, model: MainViewModel) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(22.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("COGSWORTH", letterSpacing = 3.sp, fontWeight = FontWeight.Black, fontSize = 26.sp); Text("Choose a shelf", color = Lavender) }
            IconButton(model::loadFolders) { Icon(Icons.Rounded.Refresh, "Refresh") }
            IconButton(model::showAcknowledgements) { Icon(Icons.Rounded.Info, "Acknowledgements") }
        }
        Text("Select one or more collections", Modifier.padding(horizontal = 22.dp, vertical = 6.dp), color = Color.Gray)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.folders, key = { it.id }) { folder ->
                val selected = folder.id in state.selectedFolderIds
                Card(Modifier.fillMaxWidth().clickable { model.toggleFolder(folder) }, shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = if (selected) Lavender.copy(alpha = .18f) else MaterialTheme.colorScheme.surfaceContainerLow)) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text(folder.name, fontWeight = FontWeight.Bold, fontSize = 20.sp); Text("${folder.count} records", color = Color.Gray) }
                        Checkbox(selected, { model.toggleFolder(folder) })
                    }
                }
            }
        }
        if (!state.loading && state.folders.isEmpty()) Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) { Text("No custom collection folders found.") }
        Button(model::browseSelectedFolders, enabled = state.selectedFolderIds.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp)) {
            Text(if (state.selectedFolderIds.size == 1) "Browse selected collection" else "Browse ${state.selectedFolderIds.size} collections")
        }
        TextButton(model::signOut, Modifier.align(Alignment.CenterHorizontally).navigationBarsPadding()) { Text("Disconnect Discogs") }
    }
}

@Composable private fun AcknowledgementsScreen(back: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(back) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") }
            Text("Acknowledgements", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
        Column(Modifier.padding(24.dp)) {
            Text("App icon", color = Lavender, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                "Ui icons created by Rooman12 - Flaticon",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { uriHandler.openUri("https://www.flaticon.com/free-icons/ui") }
            )
            Text("Tap the attribution to visit Flaticon.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable private fun LibraryScreen(state: AppState, model: MainViewModel) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(model::backToFolders) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Collections") }
            Column(Modifier.weight(1f)) { Text(state.collectionTitle, fontWeight = FontWeight.Bold, fontSize = 22.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${state.releases.size} records", color = Color.Gray, fontSize = 13.sp) }
            FilledTonalIconButton(model::shuffle, enabled = state.releases.isNotEmpty()) { Icon(Icons.Rounded.Casino, "Shuffle") }
        }
        OutlinedTextField(state.query, model::setQuery, placeholder = { Text("Search artist, album, label, genre") }, leadingIcon = { Icon(Icons.Rounded.Search, null) },
            trailingIcon = { if (state.query.isNotEmpty()) IconButton({ model.setQuery("") }) { Icon(Icons.Rounded.Clear, "Clear") } },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        TabRow(state.sort.ordinal, containerColor = Night, modifier = Modifier.padding(top = 8.dp)) {
            Tab(state.sort == SortMode.ARTIST, { model.setSort(SortMode.ARTIST) }, text = { Text("ARTIST") })
            Tab(state.sort == SortMode.ALBUM, { model.setSort(SortMode.ALBUM) }, text = { Text("ALBUM") })
        }
        LazyColumn(contentPadding = PaddingValues(vertical = 6.dp, horizontal = 12.dp)) {
            items(state.visibleReleases, key = { it.instanceId }) { RecordRow(it, model::show) }
        }
    }
}

@Composable private fun RecordRow(item: CollectionItem, onClick: (CollectionItem) -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick(item) }.padding(7.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(item.basic.thumb ?: item.basic.coverImage, null, Modifier.size(62.dp), contentScale = ContentScale.Crop)
        Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
            Text(item.basic.artistName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.basic.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(listOf(item.basic.year.takeIf { it > 0 }?.toString(), item.basic.formatName).filterNotNull().joinToString(" · "), color = Color.Gray, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable private fun RecordDetail(item: CollectionItem, back: () -> Unit) {
    val album = item.basic
    BoxWithConstraints(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        val compact = maxHeight < 680.dp
        Column(Modifier.fillMaxSize().padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(back) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") }
                Spacer(Modifier.weight(1f)); Text("NOW SPINNING", letterSpacing = 2.sp, fontSize = 12.sp, color = Lavender)
            }
            AsyncImage(album.coverImage ?: album.thumb, "${album.artistName} — ${album.title}",
                Modifier.size(if (compact) 250.dp else 330.dp).padding(vertical = 8.dp), contentScale = ContentScale.Crop)
            Text(album.artistName, fontWeight = FontWeight.Black, fontSize = if (compact) 22.sp else 27.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(album.title, fontSize = if (compact) 19.sp else 23.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            HorizontalDivider(Modifier.padding(vertical = if (compact) 10.dp else 18.dp), color = Lavender.copy(alpha = .35f))
            Metadata("YEAR", album.year.takeIf { it > 0 }?.toString() ?: "Unknown")
            Metadata("LABEL", album.labelName)
            Metadata("FORMAT", album.formatName)
            Metadata("GENRE", (album.genres + album.styles).distinct().joinToString(" · ").ifBlank { "Unknown" })
        }
    }
}

@Composable private fun Metadata(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, Modifier.width(66.dp), color = Lavender, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(value, Modifier.weight(1f), fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
