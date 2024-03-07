package com.curatedev.vscodownloader.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.curatedev.vscodownloader.vsco.dataclasses.VscoProfile

@Composable
fun ProfileListView(profiles: List<VscoProfile>, onProfileSelected: (VscoProfile)->Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .padding(15.dp)
    ) {
        items(profiles) {
            Surface(
                modifier = Modifier.padding(3.dp),
                shadowElevation = 5.dp,
            ) {
                Column {
                    AsyncImage(
                        model = it.imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.7F)
                            .clickable {
                                onProfileSelected(it)
                            }
                    )
                    Text(
                        text = it.name,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }

            }
        }
    }
}