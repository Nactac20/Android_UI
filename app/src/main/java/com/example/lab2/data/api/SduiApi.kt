package com.example.lab2.data.api

import com.example.lab2.data.sdui.AnalyticsAckDto
import com.example.lab2.data.sdui.AnalyticsEventDto
import com.example.lab2.data.sdui.SduiScreenDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SduiApi {
    @GET("/ui/second")
    suspend fun getSecondScreen(): SduiScreenDto

    @POST("/analytics/events")
    suspend fun postAnalyticsEvent(@Body event: AnalyticsEventDto): AnalyticsAckDto
}

