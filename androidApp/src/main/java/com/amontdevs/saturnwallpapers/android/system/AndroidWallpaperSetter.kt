package com.amontdevs.saturnwallpapers.android.system

import android.app.WallpaperManager
import android.content.Context
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import org.koin.core.context.GlobalContext
import java.io.InputStream

interface IAndroidWallpaperSetter {
    fun setWallpaper(screen: WallpaperScreen, byteArray: ByteArray): SaturnResult<Unit>
}
class AndroidWallpaperSetter : IAndroidWallpaperSetter {
    override fun setWallpaper(screen: WallpaperScreen, byteArray: ByteArray): SaturnResult<Unit> {
        return try{
            val context = GlobalContext.get().get<Context>()
            val wallpaperManager = WallpaperManager.getInstance(context)
            when(screen) {
                WallpaperScreen.LOCK_SCREEN -> wallpaperManager.setStream(byteArray.inputStream(),null,true, WallpaperManager.FLAG_LOCK)
                WallpaperScreen.HOME_SCREEN -> wallpaperManager.setStream(byteArray.inputStream(),null,true, WallpaperManager.FLAG_SYSTEM)
                else -> {
                    wallpaperManager.setStream(byteArray.inputStream(),null,true, WallpaperManager.FLAG_LOCK)
                    wallpaperManager.setStream(byteArray.inputStream(),null,true, WallpaperManager.FLAG_SYSTEM)
                }
            }
            SaturnResult.Success(Unit)
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }
}