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
<<<<<<< HEAD
    val type: AttachmentType?  // Сделано nullable для безопасности
=======
    val type: AttachmentType
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
) : Serializable