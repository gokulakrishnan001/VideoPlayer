package com.example.videoplayer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.videoplayer.model.VideoDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player,
    private val metaDataReader: MetaDataReader
) :ViewModel(){

  val branch=1;

    init {
        player.prepare()
    }
    private val videoUri=savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())

    val videoItems=videoUri.map {uris->
        uris.map {uri->
            VideoDataModel(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                videoName  = metaDataReader.getMetaDataReader(uri)?.fileName ?: "file1"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())


    fun addVideoItems(uri: Uri){
        savedStateHandle["videoUris"]=videoUri.value + uri
        player.addMediaItem(MediaItem.fromUri(uri))
    }

    fun playVideo(uri: Uri){
        player.setMediaItem(
            videoItems.value.find {
                it.contentUri==uri
            }?.mediaItem ?: return
        )
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}