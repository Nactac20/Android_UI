package com.example.lab2.data.sdui

interface SduiRepository {
    suspend fun getSecondScreen(): SduiScreenDto
    suspend fun postEvent(event: AnalyticsEventDto): AnalyticsAckDto
}

