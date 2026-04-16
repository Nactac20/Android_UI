package com.example.lab2.data.sdui

import com.google.gson.annotations.SerializedName

data class SduiScreenDto(
    @SerializedName("components")
    val components: List<SduiComponentDto>
)

data class SduiComponentDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("action")
    val action: SduiActionDto? = null,
    @SerializedName("analytics")
    val analytics: SduiAnalyticsDto? = null,
    @SerializedName("payload")
    val payload: Map<String, Any?>? = null,
    @SerializedName("style")
    val style: Map<String, Any?>? = null
)

data class SduiActionDto(
    @SerializedName("type")
    val type: String,
    @SerializedName("payload")
    val payload: Map<String, Any?>? = null
)

data class SduiAnalyticsDto(
    @SerializedName("impressionEvent")
    val impressionEvent: String? = null,
    @SerializedName("clickEvent")
    val clickEvent: String? = null
)

data class AnalyticsEventDto(
    @SerializedName("event")
    val event: String,
    @SerializedName("params")
    val params: Map<String, String>? = null,
    @SerializedName("timestamp")
    val timestamp: Long
)

data class AnalyticsAckDto(
    @SerializedName("status")
    val status: String?,
    @SerializedName("received")
    val received: Int?
)

