package com.example.videoplayer.model

import android.net.Uri
import androidx.media3.common.MediaItem

data class VideoDataModel(
    val contentUri: Uri,
    val mediaItem:MediaItem,
    val videoName:String
)
