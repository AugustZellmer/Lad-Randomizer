package me.augustzellmer.ladRandomizer.backend.objects

import java.lang.IllegalStateException

open class DuplicateIdException() : IllegalArgumentException()
class DuplicateRoomIdException() : DuplicateIdException()
class DuplicateUserIdException() : DuplicateIdException()
open class IdNotFoundException() : IllegalArgumentException()
class RoomIdNotFoundException(): IdNotFoundException()
class UserIdNotFoundException(): IdNotFoundException()
class OrphanedUserException: IllegalStateException();
