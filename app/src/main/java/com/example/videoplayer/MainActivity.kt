package com.example.videoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.example.videoplayer.ui.theme.VideoPlayerTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerTheme {
                // A surface container using the 'background' color from the theme
                val viewModel= hiltViewModel<VideoViewModel>()
                val videoItems by viewModel.videoItems.collectAsState()

                val selectVideoLauncher= rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                    onResult = {uri->
                        uri?.let(
                            viewModel::addVideoItems
                        )
                    }
                )

                var lifecycle by remember{
                    mutableStateOf(Lifecycle.Event.ON_CREATE)
                }

                val lifecycleOwner= LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner, effect ={
                    val observer=LifecycleEventObserver{_,event->
                        lifecycle=event
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                } )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    AndroidView(factory = {context->
                        PlayerView(context).also{playerView ->
                            playerView.player=viewModel.player
                        }
                    },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f),

                        update = {playerView->
                            when(lifecycle){
                                Lifecycle.Event.ON_PAUSE-> {
                                    playerView.onPause()
                                    playerView.player?.pause()
                                }
                            Lifecycle.Event.ON_RESUME->{
                                playerView.onResume()
                                playerView.player?.play()
                            }
                                else -> {Unit}
                            }
                        }

                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    IconButton(onClick = {
                        selectVideoLauncher.launch("video/*")
                    }) {
                        Icon(imageVector = Icons.Default.FileOpen, contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        items(videoItems){videoModel->
                            Text(
                               text= videoModel.videoName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.playVideo(videoModel.contentUri)
                                    }
                                    .padding(20.dp)
                            )
                        }
                    }
                }

            }
        }
    }
}




