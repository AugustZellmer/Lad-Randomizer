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
        var shape: Shape? = if(this.polygon != null && this.color != null){
            Shape(this.polygon!!, this.color!!)
        }
        else if(this.polygon == null && this.color == null){
            null
        }
        else throw HalfShapeException();
        return User(roomId, userId, shape, lastSeenAt.toInstant())
    }
}
