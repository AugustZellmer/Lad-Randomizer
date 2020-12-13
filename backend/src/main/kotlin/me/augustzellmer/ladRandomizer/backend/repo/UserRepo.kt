package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.DuplicateUserIdException
import me.augustzellmer.ladRandomizer.backend.objects.RoomIdNotFoundException
import me.augustzellmer.ladRandomizer.backend.objects.User
import me.augustzellmer.ladRandomizer.backend.objects.UserIdNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
open class UserRepo(@Autowired val db: JdbcTemplate, @Autowired val roomRepo: RoomRepo){

    fun getUser(userId: String): User?{
        val sql = "SELECT * FROM ladrandomizer.users WHERE userId=?;"
        val entity =  try {
            db.queryForObject(sql, arrayOf(userId), BeanPropertyRowMapper(UserEntity::class.java))!!;
        }catch(e: EmptyResultDataAccessException){
            return null;
        };
        return entity.toUser();
    }

    @Throws(RoomIdNotFoundException::class)
    fun getUsersInRoom(roomId: String): Set<User>{
        if(!roomIdExists(roomId)){
            throw RoomIdNotFoundException()
        }
        val sql = "SELECT * FROM ladrandomizer.users WHERE roomId=?;"
        val entities = try {
            db.query(sql, arrayOf(roomId), BeanPropertyRowMapper(UserEntity::class.java)).toSet();
        }catch(e: EmptyResultDataAccessException){
            emptySet<UserEntity>()
        };
        return entities.map { it.toUser() }.toSet()
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(RoomIdNotFoundException::class, DuplicateUserIdException::class)
    open fun addUser(user: User){
        if(!roomIdExists(user.roomId)){
            throw RoomIdNotFoundException()
        }
        if(userIdExists(user.userId)){
            throw DuplicateUserIdException()
        }
        val sql = "INSERT INTO ladrandomizer.users (roomId, userId, polygon, color, lastSeenAt) VALUES (?, ?, ?, ?, ?);"
        db.update(sql, user.roomId, user.userId, user.shape?.polygon, user.shape?.color, Timestamp.from(user.lastSeenAt))
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(UserIdNotFoundException::class, RoomIdNotFoundException::class)
    open fun updateUser(user: User){
        if(!userIdExists(user.userId)){
            throw UserIdNotFoundException()
        }
        if(!roomIdExists(user.roomId)){
            throw RoomIdNotFoundException()
        }
        val sql = "UPDATE ladrandomizer.users SET roomId=?, userId=?, polygon=?, color=?, lastSeenAt=?) WHERE userId=?"
        db.update(sql, user.roomId, user.userId, user.shape?.polygon, user.shape?.color, Timestamp.from(user.lastSeenAt), user.userId)
    }

    @Throws(UserIdNotFoundException::class)
    fun removeUser(userId: String){
        if(!userIdExists(userId)){
            throw UserIdNotFoundException()
        }
        val sql = "DELETE FROM ladrandomizer.users WHERE userId=?;"
        db.update(sql, userId)
    }

    private fun roomIdExists(roomId: String): Boolean{
        val maybeRoom = roomRepo.getRoom(roomId);
        return maybeRoom != null;
    }

    private fun userIdExists(userId: String): Boolean{
        val sql = "SELECT COUNT(*) FROM ladrandomizer.users WHERE userId=?;"
        val count = db.queryForObject(sql, arrayOf(userId), Integer::class.java)
        return count>0;
    }
}
