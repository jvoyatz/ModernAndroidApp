package dev.jvoyatz.modern.android.network.moshi

import com.squareup.moshi.Json

//models
internal data class BoredActivity(
    @Json(name = "activity")
    val activity: String
)

internal data class BoredActivityInvalid(
    @Json(name = "activityii")
    val activity: String
)

internal data class BoredActivityError(
    @Json(name = "error")
    val error: String
)

internal data class BoredActivityErrorInvalid(
    @Json(name = "errorrrr")
    val error: String
)