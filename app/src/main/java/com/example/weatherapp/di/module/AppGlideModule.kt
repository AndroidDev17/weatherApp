package com.example.weatherapp.di.module

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.weatherapp.WeatherApp
import okhttp3.OkHttpClient
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named


@GlideModule
class MyAppGlideModule() : AppGlideModule() {

    @Inject
    lateinit var client: OkHttpClient

    @JvmField
    @field:[Inject Named("memoryCacheSizeBytes")]
    var memoryCacheSizeBytes :Long? = null

    init {
        WeatherApp.instance.appComponent.injectInto(this)
    }


    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes!!))
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, memoryCacheSizeBytes!!))
        builder.setDefaultRequestOptions(requestOptions(context))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        val factory = OkHttpUrlLoader.Factory(client)

        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }

    private fun requestOptions(context: Context): RequestOptions? {
        return RequestOptions()
            .signature(
                ObjectKey(
                    System.currentTimeMillis() / (24 * 60 * 60 * 1000)
                )
            )
            .override(200, 200)
            .centerCrop()
            .encodeFormat(Bitmap.CompressFormat.PNG)
            .encodeQuality(100)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(false)
    }
}