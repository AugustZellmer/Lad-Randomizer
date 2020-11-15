package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Lazy
@Repository
class RoomRepo(@Autowired val db: JdbcTemplate, @Autowired val userRepo: UserRepo){

    fun getRoom(roomId: String): Room?{
        val sql = "SELECT * FROM rooms WHERE roomId=?;"
        return try {
            db.queryForObject(sql, arrayOf(roomId), BeanPropertyRowMapper(Room::class.java));
        }catch(e: EmptyResultDataAccessException){
            null
        };
    }

    @Throws(UserIdNotFoundException::class)
    fun getRoomContainingUser(userId: String): Room?{
        if(!userIdExists(userId)){
            throw UserIdNotFoundException()
        }
        val sql = "SELECT * FROM rooms WHERE userId=?;"
        return try {
            db.queryForObject(sql, arrayOf(userId), BeanPropertyRowMapper(Room::class.java));
        }catch(e: EmptyResultDataAccessException){
            null
        };
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(DuplicateRoomIdException::class)
    open fun addRoom(room: Room){
        if(roomIdExists(room.roomId)){
            throw DuplicateRoomIdException()
        }
        val sql = "INSERT INTO rooms (roomId, lastMutatedAt) VALUES (?, ?);"
        db.update(sql, room.roomId, room.lastMutatedAt)
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(RoomIdNotFoundException::class)
    open fun updateRoom(room: Room){
        if(roomIdExists(room.roomId)){
            throw RoomIdNotFoundException()
        }
        val sql = "UPDATE rooms SET roomId=?, lastMutatedAt=?) WHERE roomId=?"
        db.update(sql, room.roomId, room.lastMutatedAt, room.roomId)
    }

    @Throws(RoomIdNotFoundException::class)
    fun removeRoom(roomId: String){
        if(!roomIdExists(roomId)){
            throw RoomIdNotFoundException()
        }
        val sql = "DELETE FROM rooms WHERE roomId=?;"
        db.update(sql, roomId)
    }

    private fun roomIdExists(roomId: String): Boolean{
        val sql = "SELECT COUNT(*) FROM rooms WHERE roomId=?;"
        try {
            db.queryForObject(sql, arrayOf(roomId), Integer::class.java)
        }catch (e: EmptyResultDataAccessException){
            return false
        }
        return true
    }

    private fun userIdExists(userId: String): Boolean{
        val maybeUser = userRepo.getUser(userId);
        return maybeUser != null;
    }
}
