package com.amontdevs.saturnwallpapers.android.sdktests

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class RepositoryTests : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    @Test
    fun testPopulate() = runBlocking  {

    }

}