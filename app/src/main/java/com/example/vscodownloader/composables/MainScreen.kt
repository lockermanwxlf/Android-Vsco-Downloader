package com.example.vscodownloader.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vscodownloader.R
import com.example.vscodownloader.viewmodels.MainScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val vm: MainScreenViewModel = viewModel()
    val searchResult by vm.searchResult.collectAsState()
    val searchInput by vm.searchInput.collectAsState()
    val selectedProfile by vm.selectedProfile.collectAsState()

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Main") {
        composable("Main") {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text(context.getString(R.string.app_name)) })
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    SearchBar(
                        query = searchInput,
                        onQueryChange = {vm.setSearchInput(it)},
                        onSearch = {vm.search()},
                        active = false,
                        onActiveChange = {}
                    ) {

                    }
                    ProfileListView(profiles = searchResult, onProfileSelected = {
                        vm.selectProfile(it)
                        navController.navigate("Profile")
                    })



                }
            }
        }
        composable("Profile") {
            ProfileMediaView(vscoProfile = selectedProfile!!)
        }
    }
}