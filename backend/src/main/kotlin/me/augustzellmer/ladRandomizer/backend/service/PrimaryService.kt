package me.augustzellmer.ladRandomizer.backend.service

import me.augustzellmer.ladRandomizer.backend.objects.*
import me.augustzellmer.ladRandomizer.backend.repo.RoomRepo
import me.augustzellmer.ladRandomizer.backend.repo.UserRepo
import org.apache.commons.lang3.RandomStringUtils
import org.apache.juli.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import kotlin.random.Random

open class PrimaryService(@Autowired val roomRepo: RoomRepo, @Autowired val userRepo: UserRepo) {

    val logger = LogFactory.getLog(PrimaryService::class.java);

    @Throws(DuplicateRoomIdException::class)
    fun createRoom(): Room {
        logger.trace("Creating room.");
        var createdWithoutIdCollision: Boolean
        var room: Room
        do {
            createdWithoutIdCollision = true
            val roomId = RandomStringUtils.randomAlphanumeric(7)
            room = Room(roomId, Instant.now());
            try {
                roomRepo.addRoom(room)
            }catch (e: DuplicateRoomIdException){
                logger.debug(String.format("Room Id Collision: %s", roomId))
                createdWithoutIdCollision = false
            }
        } while(!createdWithoutIdCollision)
        return room
    }

    @Throws(RoomIdNotFoundException::class)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    open fun addUserToRoom(roomId: String): User{
        logger.trace("Adding user to room.");
        var createdWithoutIdCollision: Boolean
        var user: User
        do{
            createdWithoutIdCollision = true
            val userId = RandomStringUtils.randomAlphanumeric(10)
            user = User(roomId, userId, null, null, Instant.now())
            try{
                userRepo.addUser(user)
            }catch (e: DuplicateUserIdException){
                logger.debug(String.format("User Id Collision: %s", userId))
                createdWithoutIdCollision = false
            }
        } while(!createdWithoutIdCollision)
        updateRoomLastMutatedAt(roomId)
        return user
    }

    @Throws(UserIdNotFoundException::class, OrphanedUserException::class)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    open fun heartbeat(userId: String, lastCheckedInAt: Instant): RoomPerspective?{
        val roomOfUser = roomRepo.getRoomContainingUser(userId) ?: throw OrphanedUserException()
        val user = userRepo.getUser(userId) ?: throw UserIdNotFoundException()
        updateUserLastSeenAt(userId)
        cleanRoomOfStaleData(roomOfUser.roomId)
        if(userNeedsAnUpdate(roomOfUser, lastCheckedInAt)){
            return createRoomPerspective(roomOfUser, user)
        }
        return null
    }

    @Throws(RoomIdNotFoundException::class)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    open fun distributeRandomShapes(roomId: String){
        val users = userRepo.getUsersInRoom(roomId);
        val shapes = mutableListOf<Shape>();
        for(user in users){
            var createdWithoutIdCollision: Boolean
            var shape: Shape;
            do{
                createdWithoutIdCollision=true
                val polygon = Polygon.values()[Random.nextInt(Polygon.values().size)]
                val color = Color.values()[Random.nextInt(Color.values().size)]
                shape = Shape(polygon, color)
                if(shapes.contains(shape)){
                    createdWithoutIdCollision=false
                }
            }while(!createdWithoutIdCollision)
            user.polygon=shape.polygon
            user.color=shape.color
            userRepo.updateUser(user)
            updateRoomLastMutatedAt(roomId)
        }
    }

    @Throws(RoomIdNotFoundException::class)
    private fun updateRoomLastMutatedAt(roomId: String){
        val room = roomRepo.getRoom(roomId) ?: throw RoomIdNotFoundException()
        room.lastMutatedAt = Instant.now()
        roomRepo.updateRoom(room)
    }

    @Throws(UserIdNotFoundException::class)
    private fun updateUserLastSeenAt(userId: String){
        val user = userRepo.getUser(userId) ?: throw UserIdNotFoundException()
        user.lastSeenAt = Instant.now()
        userRepo.updateUser(user)
    }

    @Throws(RoomIdNotFoundException::class)
    private fun cleanRoomOfStaleData(roomId: String){
        val users = userRepo.getUsersInRoom(roomId)
        for(user in users){
            if(user.lastSeenAt.isBefore(Instant.now().minus(3, SECONDS))){
                userRepo.removeUser(user.userId)
            }
        }
        val currentUsers = userRepo.getUsersInRoom(roomId)
        if(currentUsers.isEmpty()){
            roomRepo.removeRoom(roomId);
        }
    }

    private fun userNeedsAnUpdate(roomOfUser: Room, lastCheckedInAt: Instant): Boolean {
        return roomOfUser.lastMutatedAt.isAfter(lastCheckedInAt)
    }

    private fun createRoomPerspective(roomOfUser: Room, user: User): RoomPerspective? {
        
    }
}
