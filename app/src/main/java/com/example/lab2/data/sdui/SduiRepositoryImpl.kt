package com.example.lab2.data.sdui

import com.example.lab2.data.api.ApiClient
import com.example.lab2.data.api.SduiApi

class SduiRepositoryImpl(
    private val api: SduiApi = ApiClient.sduiApi
) : SduiRepository {
    override suspend fun getSecondScreen(): SduiScreenDto = api.getSecondScreen()

    override suspend fun postEvent(event: AnalyticsEventDto): AnalyticsAckDto =
        api.postAnalyticsEvent(event)
}

