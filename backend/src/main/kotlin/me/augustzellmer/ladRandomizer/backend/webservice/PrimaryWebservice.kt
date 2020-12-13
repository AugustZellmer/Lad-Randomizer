package me.augustzellmer.ladRandomizer.backend.webservice

import me.augustzellmer.ladRandomizer.backend.objects.Room
import me.augustzellmer.ladRandomizer.backend.objects.RoomPerspective
import me.augustzellmer.ladRandomizer.backend.objects.User
import me.augustzellmer.ladRandomizer.backend.objects.UserIdNotFoundException
import me.augustzellmer.ladRandomizer.backend.service.PrimaryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController()
@RequestMapping("/api")
class PrimaryWebservice(@Autowired val service: PrimaryService){

    @PostMapping("/room")
    fun createRoom(): Room {
        return service.createRoom()
    }

    @PostMapping("/room/{roomId}/user")
    fun addUserToRoom(@PathVariable roomId: String): User {
        return service.addUserToRoom(roomId)
    }

    @PostMapping("/room/{roomId}/randomize")
    fun distributeRandomShapes(@PathVariable roomId: String){
        service.distributeRandomShapes(roomId)
    }

    @PostMapping("/room/{roomId}/user/{userId}/heartbeat")
    fun heartBeat(@PathVariable roomId: String, @PathVariable userId: String, @RequestParam lastUpdatedAt: Instant): RoomPerspective? {
        if(!service.doesRoomContainUser(roomId, userId)){
            /*
            Technically, we don't need the roomId variable. However, the REST API is more confusing without it so we ask
            for it anyway. Yet, because we ask for it anyway, we'd better validate it.
             */
            throw UserIdNotFoundException()
        }
        return service.heartbeat(userId, lastUpdatedAt);
    }

    /*
    What is the correct HTTP Method to use here? Who knows!
     */
    @PostMapping("/deleteStaleData")
    fun deleteStaleData(): Unit{
        service.deleteStaleData();
    }
}
