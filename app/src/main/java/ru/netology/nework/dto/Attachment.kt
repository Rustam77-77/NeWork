package ru.netology.nework.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class AttachmentType {
    @SerializedName("image")
    IMAGE,

    @SerializedName("video")
    VIDEO,

    @SerializedName("audio")
    AUDIO
}

data class Attachment(
    val url: String,
    val type: AttachmentType?  // Сделано nullable для безопасности
) : Serializable