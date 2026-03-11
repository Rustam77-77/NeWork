package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO
}

@Parcelize
data class Attachment(
    val url: String,
    val type: AttachmentType
) : Parcelable