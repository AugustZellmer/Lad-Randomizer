package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.Room
import java.time.Instant

data class RoomEntity(var roomId: String, var lastAccessedAt: Instant){

    fun toRoom(): Room {
        return Room(roomId, lastAccessedAt);
    }
}
