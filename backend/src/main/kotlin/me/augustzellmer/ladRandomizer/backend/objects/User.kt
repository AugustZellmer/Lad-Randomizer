package me.augustzellmer.ladRandomizer.backend.objects

import java.time.Instant

data class User(var roomId: String, var userId: String, var polygon: Polygon?, var color: Color?, var lastSeenAt: Instant)
