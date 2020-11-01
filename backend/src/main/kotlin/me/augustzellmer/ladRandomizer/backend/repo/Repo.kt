package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.Shape
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

open class Repo(@Autowired val db: JdbcTemplate){

    @Transactional
    @Throws(DuplicateRoomIdException::class, DuplicateUserIdException::class)
    open fun addUser(roomId: String, userId: String){
        if(roomIdExists(roomId)){
            throw DuplicateRoomIdException()
        }
        if(userIdExists(userId)){
            throw DuplicateUserIdException()
        }
        val sql = "INSERT INTO users (roomId, userId, polygon, color) VALUES (?, ?, NULL, NULL);"
        db.update(sql, roomId, userId)
    }

    @Transactional
    @Throws(UserIdNotFoundException::class)
    open fun removeUser(userId: String){
        if(!userIdExists(userId)){
            throw UserIdNotFoundException()
        }
        val sql = "DELETE FROM users WHERE userId=?;"
        db.update(sql, userId)
    }

    fun numUsersInRoom(roomId: String): Int {
        val sql = "SELECT COUNT(*) FROM users WHERE roomId=?;"
        return db.queryForObject(sql, arrayOf(roomId), Integer::class.java).toInt()
    }

    @Transactional
    @Throws(UserIdNotFoundException::class)
    open fun setShapeOnUser(userId: String, shape: Shape){
        if(!userIdExists(userId)){
            throw UserIdNotFoundException()
        }
        val sql = "UPDATE users SET polygon=?, color=? WHERE userId=?;"
        db.update(sql, shape.polygon, shape.color, userId);
    }

    @Transactional
    @Throws(UserIdNotFoundException::class)
    open fun getShape(userId: String): Shape?{
        if(!userIdExists(userId)){
            throw UserIdNotFoundException()
        }
        val sql = "SELECT * FROM users WHERE userId=?;"
        val row = db.queryForObject(sql, arrayOf(userId), BeanPropertyRowMapper(Row::class.java))!!;
        if(row.polygon==null && row.color==null){
            return null;
        }
        return Shape(row.polygon!!, row.color!!);
    }

    private fun roomIdExists(roomId: String): Boolean{
        val sql = "SELECT COUNT(*) FROM users WHERE roomId=?;"
        try {
            db.queryForObject(sql, arrayOf(roomId), Integer::class.java)
        }catch(e: EmptyResultDataAccessException){
            return false
        }
        return true
    }

    private fun userIdExists(userId: String): Boolean{
        val sql = "SELECT COUNT(*) FROM users WHERE userId=?;"
        try {
            db.queryForObject(sql, arrayOf(userId), Integer::class.java)
        }catch(e: EmptyResultDataAccessException){
            return false
        }
        return true
    }
}

open class DuplicateIdException() : IllegalArgumentException()
class DuplicateRoomIdException() : DuplicateIdException()
class DuplicateUserIdException() : DuplicateIdException()
open class IdNotFoundException() : IllegalArgumentException()
class UserIdNotFoundException(): IdNotFoundException()
