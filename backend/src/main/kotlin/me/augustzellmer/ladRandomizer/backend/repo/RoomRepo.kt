package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.DuplicateRoomIdException
import me.augustzellmer.ladRandomizer.backend.objects.Room
import me.augustzellmer.ladRandomizer.backend.objects.RoomIdNotFoundException
import me.augustzellmer.ladRandomizer.backend.objects.UserIdNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
class RoomRepo(@Autowired val db: JdbcTemplate, @Lazy @Autowired val userRepo: UserRepo){

    fun getRoom(roomId: String): Room?{
        val sql = "SELECT * FROM ladrandomizer.rooms WHERE roomId=?;"
        val entity = try {
            db.queryForObject(sql, arrayOf(roomId), BeanPropertyRowMapper(RoomEntity::class.java))!!;
        }catch(e: EmptyResultDataAccessException){
            return null
        }
        return entity.toRoom();
    }

    fun getRooms(): Set<Room>{
        val sql = "SELECT * FROM ladrandomizer.rooms;"
        val entities = try {
            db.query(sql, BeanPropertyRowMapper(RoomEntity::class.java));
        }catch(e: EmptyResultDataAccessException){
            emptySet<RoomEntity>()
        }
        return entities.map { it.toRoom() }.toSet()
    }

    @Throws(UserIdNotFoundException::class)
    fun getRoomContainingUser(userId: String): Room?{
        if(!userIdExists(userId)){
            throw UserIdNotFoundException()
        }
        val sql = "SELECT r.roomId, r.lastAccessedAt FROM ladrandomizer.rooms r JOIN ladrandomizer.users u ON r.roomId=u.roomId WHERE userId=?;"
        val entity = try {
            db.queryForObject(sql, arrayOf(userId), BeanPropertyRowMapper(RoomEntity::class.java))!!;
        }catch(e: EmptyResultDataAccessException){
            return null
        }
        return entity.toRoom();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(DuplicateRoomIdException::class)
    open fun addRoom(room: Room){
        if(roomIdExists(room.roomId)){
            throw DuplicateRoomIdException()
        }
        val sql = "INSERT INTO ladrandomizer.rooms (roomId, lastAccessedAt) VALUES (?, ?);"
        db.update(sql, room.roomId, Timestamp.from(room.lastAccessedAt))
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(RoomIdNotFoundException::class)
    open fun updateRoom(room: Room){
        if(!roomIdExists(room.roomId)){
            throw RoomIdNotFoundException()
        }
        val sql = "UPDATE ladrandomizer.rooms SET roomId=?, lastAccessedAt=? WHERE roomId=?"
        db.update(sql, room.roomId, Timestamp.from(room.lastAccessedAt), room.roomId)
    }

    @Throws(RoomIdNotFoundException::class)
    fun removeRoom(roomId: String){
        if(!roomIdExists(roomId)){
            throw RoomIdNotFoundException()
        }
        val sql = "DELETE FROM ladrandomizer.rooms WHERE roomId=?;"
        db.update(sql, roomId)
    }

    private fun roomIdExists(roomId: String): Boolean{
        val sql = "SELECT COUNT(*) FROM ladrandomizer.rooms WHERE roomId=?;"
        val count = db.queryForObject(sql, arrayOf(roomId), Integer::class.java)
        return count>0;
    }

    private fun userIdExists(userId: String): Boolean{
        val maybeUser = userRepo.getUser(userId);
        return maybeUser != null;
    }
}
