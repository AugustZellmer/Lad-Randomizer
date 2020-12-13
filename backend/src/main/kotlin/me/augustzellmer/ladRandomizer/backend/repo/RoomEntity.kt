package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.Room
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class RoomEntity{

    @Id
    lateinit var roomId: String
    lateinit var lastAccessedAt: Timestamp

    fun toRoom(): Room {
        return Room(roomId, lastAccessedAt.toInstant());
    }
}
