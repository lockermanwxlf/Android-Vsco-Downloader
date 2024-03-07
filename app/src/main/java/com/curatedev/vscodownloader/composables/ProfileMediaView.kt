package com.curatedev.vscodownloader.composables

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.curatedev.vscodownloader.viewmodels.ProfileMediaViewViewModel
import com.curatedev.vscodownloader.vsco.dataclasses.VscoProfile

@Composable
fun ProfileMediaView(
    vscoProfile: VscoProfile,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vm: ProfileMediaViewViewModel = hiltViewModel()
    val media by vm.media.collectAsState()
    LaunchedEffect(1) {
        vm.fetchMedia(vscoProfile)
    }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {

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
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        vm.download(vscoProfile, media)
                        Toast.makeText(context, "Progress shown in notification. Avoid pressing again.", Toast.LENGTH_SHORT).show()
                    }) {
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
                VscoAsyncImage(
                    vscoMedia = it,
                    contentDescription = "Post",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                )
            }
        }
    }
}