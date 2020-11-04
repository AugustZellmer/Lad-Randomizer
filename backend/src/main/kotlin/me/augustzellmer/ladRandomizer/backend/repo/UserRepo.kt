package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

open class UserRepo(@Autowired val db: JdbcTemplate, @Autowired val roomRepo: RoomRepo){

    fun getUser(userId: String): User?{
        val sql = "SELECT * FROM users WHERE userId=?;"
        return try {
            db.queryForObject(sql, arrayOf(userId), BeanPropertyRowMapper(User::class.java));
        }catch(e: EmptyResultDataAccessException){
            null
        };
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
        val sql = "INSERT INTO users (roomId, userId, polygon, color, lastSeenAt) VALUES (?, ?, ?, ?, ?);"
        db.update(sql, user.roomId, user.userId, user.polygon, user.color, user.lastSeenAt)
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
        val sql = "UPDATE users SET roomId=?, userId=?, polygon=?, color=?, lastSeenAt=?) WHERE userId=?"
        db.update(sql, user.roomId, user.userId, user.polygon, user.color, user.lastSeenAt, user.userId)
    }

    @Throws(UserIdNotFoundException::class)
    fun removeUser(userId: String){
        if(!userIdExists(userId)){
            throw UserIdNotFoundException()
        }
        val sql = "DELETE FROM users WHERE userId=?;"
        db.update(sql, userId)
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Throws(RoomIdNotFoundException::class)
    open fun numUsersInRoom(roomId: String): Int {
        if(!roomIdExists(roomId)){
            throw RoomIdNotFoundException()
        }
        val sql = "SELECT COUNT(*) FROM users WHERE roomId=?;"
        return db.queryForObject(sql, arrayOf(roomId), Integer::class.java).toInt()
    }

    private fun roomIdExists(roomId: String): Boolean{
        val maybeRoom = roomRepo.getRoom(roomId);
        return maybeRoom != null;
    }

    private fun userIdExists(userId: String): Boolean{
        val sql = "SELECT COUNT(*) FROM users WHERE userId=?;"
        try {
            db.queryForObject(sql, arrayOf(userId), Integer::class.java)
        }catch (e: EmptyResultDataAccessException){
            return false
        }
        return true
    }
}
