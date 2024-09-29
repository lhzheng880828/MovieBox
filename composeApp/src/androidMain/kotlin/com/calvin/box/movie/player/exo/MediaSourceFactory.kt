package com.calvin.box.movie.player.exo

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.source.ConcatenatingMediaSource2
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory
import androidx.media3.extractor.ts.TsExtractor
import com.calvin.box.movie.ContextProvider
import com.calvin.box.movie.getPlatform
import okhttp3.OkHttpClient

@UnstableApi
class MediaSourceFactory @OptIn(markerClass = [UnstableApi::class]) constructor() :
    MediaSource.Factory {

   private val defaultMediaSourceFactory: DefaultMediaSourceFactory by lazy {
       DefaultMediaSourceFactory(dataSourceFactory, extractorsFactory)
   }

    private val httpDataSourceFactory: HttpDataSource.Factory by lazy {
        val okhttpClient =  getPlatform().getHostOkhttp() as OkHttpClient
        OkHttpDataSource.Factory(okhttpClient)
    }

    private val dataSourceFactory: DataSource.Factory by lazy {
        buildReadOnlyCacheDataSource(
            DefaultDataSource.Factory(
                (ContextProvider.context as Context),
                httpDataSourceFactory
            )
        )
    }

    private val extractorsFactory: ExtractorsFactory by lazy {
        DefaultExtractorsFactory()
            .setTsExtractorFlags(DefaultTsPayloadReaderFactory.FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS)
            .setTsExtractorTimestampSearchBytes(TsExtractor.DEFAULT_TIMESTAMP_SEARCH_BYTES * 3)
    }


    override fun setDrmSessionManagerProvider(drmSessionManagerProvider: DrmSessionManagerProvider): MediaSource.Factory {
        return defaultMediaSourceFactory.setDrmSessionManagerProvider(drmSessionManagerProvider)
    }

    override fun setLoadErrorHandlingPolicy(loadErrorHandlingPolicy: LoadErrorHandlingPolicy): MediaSource.Factory {
        return defaultMediaSourceFactory.setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
    }

    override fun getSupportedTypes(): IntArray {
       return defaultMediaSourceFactory.supportedTypes
    }



    override fun createMediaSource(mediaItem: MediaItem): MediaSource {
        val mimetype = mediaItem.localConfiguration?.mimeType
        val mediaId  = mediaItem.mediaId
        return when {
            (mediaItem.localConfiguration?.uri.toString().endsWith(".m3u8") ||
                   MimeTypes.APPLICATION_M3U8 == mimetype) -> {
                HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            }
            (mediaItem.localConfiguration?.uri.toString().endsWith(".mpd") ||
                    MimeTypes.APPLICATION_MPD == mimetype)  -> {
                DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            }
            (mediaId.contains("***") && mediaId.contains("|||")) ->{
                createConcatenatingMediaSource(setHeader(mediaItem))
            }
            else -> defaultMediaSourceFactory.createMediaSource(setHeader(mediaItem))
        }

    }

    private fun setHeader(mediaItem: MediaItem): MediaItem {
        val headers: MutableMap<String, String> = HashMap()
        for (key in mediaItem.requestMetadata.extras!!.keySet()) headers[key] =
            mediaItem.requestMetadata.extras!![key].toString()
        httpDataSourceFactory.setDefaultRequestProperties(headers)
        return mediaItem
    }

    private fun createConcatenatingMediaSource(mediaItem: MediaItem): MediaSource {
        val builder: ConcatenatingMediaSource2.Builder = ConcatenatingMediaSource2.Builder()
        for (split in mediaItem.mediaId.split("\\*\\*\\*".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()) {
            val info =
                split.split("\\|\\|\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (info.size >= 2) builder.add(
                defaultMediaSourceFactory.createMediaSource(
                    mediaItem.buildUpon().setUri(
                        Uri.parse(
                            info[0]
                        )
                    ).build()
                ), info[1].toLong()
            )
        }
        return builder.build()
    }

    private fun buildReadOnlyCacheDataSource(upstreamFactory: DataSource.Factory): CacheDataSource.Factory {
        return CacheDataSource.Factory().setCache(CacheManager.get().getCache())
            .setUpstreamDataSourceFactory(upstreamFactory).setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}
