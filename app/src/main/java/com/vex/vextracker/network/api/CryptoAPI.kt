package com.blockchain.vex.network.api

import com.blockchain.vex.network.BASE_CRYPTOCOMPARE_URL
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.vex.vextracker.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
Created by Pranay Airan
api provider for crypto compare and others.
 */

val cryptoCompareRetrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_CRYPTOCOMPARE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

val okHttpClient: OkHttpClient by lazy {
    val builder = OkHttpClient.Builder()
    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
        builder.addInterceptor(StethoInterceptor())
    }

    builder.build()
}

val api: API by lazy {
    cryptoCompareRetrofit.create(API::class.java)
}
