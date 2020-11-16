package me.augustzellmer.ladRandomizer.backend.objects

open class DuplicateIdException() : IllegalArgumentException()
class DuplicateRoomIdException() : DuplicateIdException()
class DuplicateUserIdException() : DuplicateIdException()
open class IdNotFoundException() : IllegalArgumentException()
class RoomIdNotFoundException(): IdNotFoundException()
class UserIdNotFoundException(): IdNotFoundException()
class OrphanedUserException: IllegalStateException();
class HalfShapeException: IllegalStateException();
