package me.augustzellmer.ladRandomizer.backend.objects

import java.time.Instant

data class User(var roomId: String, var userId: String, var shape: Shape?, var lastSeenAt: Instant)
