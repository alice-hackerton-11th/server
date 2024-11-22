package com.elice.gameservice.application.room.service

import com.elice.common.exception.NotFoundException
import com.elice.common.response.BaseResponseStatus
import com.elice.gameservice.domain.common.value.RoomMemberEnteredState
import com.elice.gameservice.domain.room.model.Room
import com.elice.gameservice.domain.room.store.RoomStore
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomStore: RoomStore
) {
    @Transactional
    fun createRoom(subject: String, memberLimit: Int, explanationSecond: Long, memberId: Long) {
        val room = Room(subject = subject, memberLimit = memberLimit, explanationSecond = explanationSecond)
        room.createRoomMember(memberId)
        roomStore.saveRoom(room)
    }

    @Transactional
    fun enterRoom(roomId: Long, memberId: Long) {
        roomStore.findRoomMember(roomId, memberId)?.apply {
            this.roomMemberEnteredState = RoomMemberEnteredState.ENTERED
        } ?: run {
            val room = roomStore.findRoomById(roomId) ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_ROOM)
            room.createRoomMember(memberId)
        }
    }

    @Transactional
    fun exitRoom(roomId: Long, memberId: Long) {
        val roomMember = roomStore.findRoomMember(roomId, memberId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_ROOM_USER)
        roomMember.roomMemberEnteredState = RoomMemberEnteredState.NOT_ENTERED
    }
}