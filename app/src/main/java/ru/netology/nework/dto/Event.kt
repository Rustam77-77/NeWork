package ru.netology.nework.dto

import java.util.Date

enum class EventType { ONLINE, OFFLINE }

data class Event(
    val id: Long,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Date,
    val eventDate: Date,
    val type: EventType,
    val likedByMe: Boolean = false,
    val likesCount: Int = 0,
    val link: String? = null,
    val participants: List<Long> = emptyList(),
    val speakers: List<Long> = emptyList(),
    val authorJob: String? = null
)