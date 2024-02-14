package com.example.videoplayer

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object VideoAppModule {

    @Provides
    @ViewModelScoped
    fun providesVideoPlayer(app: Application): Player {
        return ExoPlayer.Builder(app).build()
    }
    @Provides
    @ViewModelScoped
    fun providesMetaDataReader(app: Application): MetaDataReader {
        return MetaDataReaderImpl(app)
    }
}