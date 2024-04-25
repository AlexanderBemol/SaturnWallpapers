package com.amontdevs.saturnwallpapers.source

import com.amontdevs.saturnwallpapers.model.ApodModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel

interface IAPODService{
    suspend fun getPhotoOfADay(date: String): ApodModel
    suspend fun getPhotoOfDays(startDate: String, endDate: String): List<ApodModel>
    suspend fun downloadPhoto(url: String): ByteReadChannel
}

class APODService(
    private val client: HttpClient
) : IAPODService {
    override suspend fun getPhotoOfADay(date: String) =
        client.get(SERVICE_HOST){
            url {
                parameters.append("api_key", API_KEY)
                parameters.append("date", date)
                parameters.append("thumbs","true")
            }
        }.body<ApodModel>()

    override suspend fun getPhotoOfDays(startDate: String, endDate: String) =
        client.get(SERVICE_HOST){
            url {
                parameters.append("api_key", API_KEY)
                parameters.append("start_date", startDate)
                parameters.append("end_date", endDate)
                parameters.append("thumbs","true")
            }
        }.body<List<ApodModel>>()

    override suspend fun downloadPhoto(url: String) =
        client.get(url).bodyAsChannel()

    companion object {
        const val SERVICE_HOST = "https://api.nasa.gov/planetary/apod"
        const val API_KEY = "GVWuXLsKY0JFFo1kvZ1YgeHyD9AONA1S2wcrwaSq"
    }
}