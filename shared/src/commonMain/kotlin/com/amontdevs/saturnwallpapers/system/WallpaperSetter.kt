package com.amontdevs.saturnwallpapers.system

import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import com.amontdevs.saturnwallpapers.source.IFileManager
import com.amontdevs.saturnwallpapers.utils.ISaturnLogger

interface IWallpaperSetter {
    fun setWallpaper(
        screen: WallpaperScreen,
        path: String,
        platformSetWallpaper: (screen: WallpaperScreen, byteArray: ByteArray) -> SaturnResult<Unit>
    ): SaturnResult<Unit>
}
class WallpaperSetter(
    private val logger: ISaturnLogger,
    private val fileManager: IFileManager
) : IWallpaperSetter {
    override fun setWallpaper(
        screen: WallpaperScreen,
        path: String,
        platformSetWallpaper: (screen: WallpaperScreen,  byteArray: ByteArray) -> SaturnResult<Unit>
    ): SaturnResult<Unit> {
        return try {
            when (val result = fileManager.getPicture(path)){
                is SaturnResult.Success -> {
                    when (val wallpaperResult = platformSetWallpaper(screen, result.data)){
                        is SaturnResult.Success -> SaturnResult.Success(Unit)
                        is SaturnResult.Error -> {
                            logger.logError(
                                WallpaperSetter::class.toString(),
                                wallpaperResult.e,
                                "Error Setting Wallpaper"
                            )
                            SaturnResult.Error(wallpaperResult.e)
                        }
                    }
                }
                is SaturnResult.Error -> {
                    logger.logError(
                        WallpaperSetter::class.toString(),
                        result.e,
                        "Error Getting Picture"
                    )
                    SaturnResult.Error(result.e)
                }
            }
        } catch (e: Exception){
            logger.logError(WallpaperSetter::class.toString(), e)
            SaturnResult.Error(e)
        }
    }

}

