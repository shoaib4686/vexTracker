package com.vex.vextracker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.blockchain.vex.data.database.CoinBitDatabase
import com.facebook.stetho.Stetho
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
Created by Pranay Airan
 */

class CoinBitApplication : Application() {

    companion object {

        private const val DATABASE_NAME = "coinbit.db"

        private lateinit var appContext: Context
        var database: CoinBitDatabase? = null

        @JvmStatic
        fun getGlobalAppContext(): Context {
            return appContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        } else {
            Timber.plant(CrashReportingTree())
        }

        database = Room.databaseBuilder(this, CoinBitDatabase::class.java, DATABASE_NAME).build()
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (priority == Log.ERROR) {
               // FirebaseCrashlytics.getInstance().log("E/$tag:$message")
            } else if (priority == Log.WARN) {
               // FirebaseCrashlytics.getInstance().log("W/$tag:$message")
            }
        }
    }
}
