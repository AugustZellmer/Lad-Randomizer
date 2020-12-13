package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.*
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserEntity{

    @Id
    lateinit var roomId: String
    lateinit var userId: String
    var polygon: Polygon? = null
    var color: Color? = null
    lateinit var lastSeenAt: Timestamp

    fun toUser(): User {
        val shape = if(polygon != null && color != null) Shape(polygon!!, color!!) else throw HalfShapeException();
        return User(roomId, userId, shape, lastSeenAt.toInstant())
    }
}
