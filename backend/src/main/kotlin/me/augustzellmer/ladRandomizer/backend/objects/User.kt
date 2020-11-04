package me.augustzellmer.ladRandomizer.backend.objects

import java.time.Instant

data class User(val roomId: String, val userId: String, val polygon: Polygon?, val color: Color?, val lastSeenAt: Instant)
