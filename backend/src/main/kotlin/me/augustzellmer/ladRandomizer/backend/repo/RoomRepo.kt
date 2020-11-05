package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
open class RoomRepo(@Autowired val db: JdbcTemplate){

    fun getRoom(roomId: String): Room?{
        val sql = "SELECT * FROM rooms WHERE roomId=?;"
        return try {
            db.queryForObject(sql, arrayOf(roomId), BeanPropertyRowMapper(Room::class.java));
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
        val sql = "INSERT INTO rooms (roomId) VALUES (?);"
        db.update(sql, room.roomId)
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
}
