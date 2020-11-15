package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.*
import java.time.Instant

data class UserEntity(var roomId: String, var userId: String, var polygon: Polygon?, var color: Color?, var lastSeenAt: Instant){

    fun toUser(): User {
        val shape = if(polygon != null && color != null) Shape(polygon!!, color!!) else throw HalfShapeException();
        return User(roomId, userId, shape, lastSeenAt)
    }
}

