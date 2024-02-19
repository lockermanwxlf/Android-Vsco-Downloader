package com.example.vscodownloader.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.provider.DocumentsContractCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.vscodownloader.viewmodels.ProfileMediaViewViewModel
import com.example.vscodownloader.vsco.dataclasses.VscoProfile

@Composable
fun ProfileMediaView(
    vscoProfile: VscoProfile,
    modifier: Modifier = Modifier
) {
    val vm: ProfileMediaViewViewModel = viewModel()
    val media by vm.media.collectAsState()
    LaunchedEffect(1) {
        vm.fetchMedia(vscoProfile)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = vscoProfile.imageUri,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.3F)
                        .aspectRatio(1F)
                        .clip(CircleShape)
                )
                Text(vscoProfile.name)
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { vm.download(vscoProfile, media) }) {
                    Text("Download Posts")
                }
            }
        }
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.padding(it)
        ) {
            items(media) {
                var loaded by rememberSaveable {
                    mutableStateOf(false)
                }
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        it.downloadUri,
                        contentDescription = "Post",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        contentScale = ContentScale.FillWidth,
                        onSuccess = { loaded = true }
                    )
                    if (!loaded) {
                        CircularProgressIndicator()
                    }
                }

            }
        }
    }
}