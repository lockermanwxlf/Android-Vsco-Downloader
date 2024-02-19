package com.example.vscodownloader.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.vscodownloader.R
import com.example.vscodownloader.vsco.dataclasses.VscoProfile

@Composable
fun ProfileListView(profiles: List<VscoProfile>, onProfileSelected: (VscoProfile)->Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(profiles) {
            AsyncImage(
                model = it.imageUri?: R.drawable.baseline_boy_24,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(0.7F)
                    .clickable {
                        onProfileSelected(it)
                    }
            )
        }
    }
}