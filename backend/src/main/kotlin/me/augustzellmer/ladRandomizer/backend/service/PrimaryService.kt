package me.augustzellmer.ladRandomizer.backend.service

import me.augustzellmer.ladRandomizer.backend.objects.DuplicateRoomIdException
import me.augustzellmer.ladRandomizer.backend.objects.DuplicateUserIdException
import me.augustzellmer.ladRandomizer.backend.objects.RoomIdNotFoundException
import me.augustzellmer.ladRandomizer.backend.repo.UserRepo
import org.apache.juli.logging.LogFactory
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired

class PrimaryService(@Autowired val repo: UserRepo) {

    val logger = LogFactory.getLog(PrimaryService::class.java);

    fun createRoom(): UserInRoom{

        logger.trace("Creating room.");

        var createdWithoutIdCollision: Boolean
        var roomId: String
        var userId: String

        do {
            createdWithoutIdCollision = true
            roomId = RandomStringUtils.randomAlphanumeric(7)
            userId = RandomStringUtils.randomAlphanumeric(10)
            try {
                repo.addUser(roomId, userId)
            }catch(e: DuplicateRoomIdException){
                logger.debug(String.format("Room Id Collision: %s", roomId))
                createdWithoutIdCollision = false
            }catch(e: DuplicateUserIdException){
                logger.debug(String.format("User Id Collision: %s", userId))
                createdWithoutIdCollision = false
            }
        } while(!createdWithoutIdCollision)

        return UserInRoom(roomId, userId)
    }

    @Throws(RoomIdNotFoundException::class)
    fun addUserToRoom(roomId: String): String{

        logger.trace("Creating user.")

        var createdWithoutIdCollision: Boolean
        var userId: String;

        do {
            createdWithoutIdCollision = true
            userId = RandomStringUtils.randomAlphanumeric(10);
            try {
                repo.addUser(roomId, userId)
            }catch(e: DuplicateUserIdException){
                logger.debug(String.format("User Id Collision: %s", userId))
                createdWithoutIdCollision = false
            }
        } while(!createdWithoutIdCollision)

        return userId
    }
}
