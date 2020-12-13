package me.augustzellmer.ladRandomizer.backend.objects

import java.time.Instant

data class Room(var roomId: String, var lastAccessedAt: Instant)
