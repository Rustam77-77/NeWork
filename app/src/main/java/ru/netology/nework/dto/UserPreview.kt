package ru.netology.nework.dto

import java.io.Serializable

data class UserPreview(
    val id: Long,
    val name: String,
    val avatar: String?
) : Serializable