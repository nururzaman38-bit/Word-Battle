package com.wordbattle.data.firebase

import com.google.firebase.database.*
import com.wordbattle.game.models.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class FirebaseManager {
    
    private val database = FirebaseDatabase.getInstance()
    
    // Rooms operations
    fun createRoom(
        hostPlayerId: String,
        hostPlayerName: String,
        totalSlots: Int,
        localSlotsCount: Int,
        onlineSlotsCount: Int
    ): Room {
        val roomId = generateRoomId()
        val passcode = generatePasscode()
        val gameStateId = UUID.randomUUID().toString()
        
        val slots = (0 until totalSlots).map { index ->
            RoomSlot(
                slotIndex = index,
                filledBy = if (index == 0) {
                    Player(
                        id = hostPlayerId,
                        name = hostPlayerName,
                        type = if (index < localSlotsCount) PlayerType.HUMAN_LOCAL else PlayerType.HUMAN_ONLINE
                    )
                } else null,
                isReady = false
            )
        }
        
        val room = Room(
            roomId = roomId,
            passcode = passcode,
            hostPlayerId = hostPlayerId,
            totalSlots = totalSlots,
            localSlotsCount = localSlotsCount,
            onlineSlotsCount = onlineSlotsCount,
            slots = slots,
            gameStateId = gameStateId
        )
        
        val roomRef = database.getReference("rooms/$roomId")
        roomRef.setValue(room).await()
        
        return room
    }
    
    fun joinRoom(
        roomId: String,
        passcode: String,
        playerId: String,
        playerName: String
    ): Result<Room> {
        return try {
            val roomRef = database.getReference("rooms/$roomId")
            val roomSnapshot = roomRef.get().await()
            
            if (!roomSnapshot.exists()) {
                Result.failure(Exception("Room not found"))
            } else {
                val room = roomSnapshot.getValue(Room::class.java)
                
                if (room == null) {
                    Result.failure(Exception("Invalid room data"))
                } else if (room.passcode != passcode) {
                    Result.failure(Exception("Wrong passcode"))
                } else if (room.gameStateId != null) {
                    Result.failure(Exception("Room has already started"))
                } else {
                    val openSlotIndex = room.slots.indexOfFirst { it.filledBy == null }
                    
                    if (openSlotIndex == -1) {
                        Result.failure(Exception("Room is full"))
                    } else {
                        val updatedSlots = room.slots.mapIndexed { index, slot ->
                            if (index == openSlotIndex) {
                                slot.copy(
                                    filledBy = Player(
                                        id = playerId,
                                        name = playerName,
                                        type = if (index < room.localSlotsCount) PlayerType.HUMAN_LOCAL 
                                               else PlayerType.HUMAN_ONLINE
                                    )
                                )
                            } else {
                                slot
                            }
                        }
                        
                        val updatedRoom = room.copy(slots = updatedSlots)
                        roomRef.setValue(updatedRoom).await()
                        
                        Result.success(updatedRoom)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun makePlayerReady(roomId: String, playerId: String): Room? {
        val roomRef = database.getReference("rooms/$roomId")
        val roomSnapshot = roomRef.get().await()
        val room = roomSnapshot.getValue(Room::class.java) ?: return null
        
        val updatedSlots = room.slots.map { slot ->
            if (slot.filledBy?.id == playerId) {
                slot.copy(isReady = true)
            } else {
                slot
            }
        }
        
        roomRef.child("slots").setValue(updatedSlots).await()
        return room.copy(slots = updatedSlots)
    }
    
    fun startGame(roomId: String, gameState: GameState): String {
        val roomRef = database.getReference("rooms/$roomId")
        roomRef.child("gameStateId").setValue(gameState.gameId).await()
        
        val gamesRef = database.getReference("games/${gameState.gameId}")
        gamesRef.setValue(gameState).await()
        
        return gameState.gameId
    }
    
    fun observeRoom(roomId: String, onUpdate: (Room) -> Unit): DatabaseListener {
        val roomRef = database.getReference("rooms/$roomId")
        
        val listener = roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(Room::class.java)
                room?.let { onUpdate(it) }
            }
            
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        
        return DatabaseListener(roomRef, listener)
    }
    
    fun observeGame(gameId: String, onUpdate: (GameState) -> Unit): DatabaseListener {
        val gameRef = database.getReference("games/$gameId")
        
        val listener = gameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameState = snapshot.getValue(GameState::class.java)
                gameState?.let { onUpdate(it) }
            }
            
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        
        return DatabaseListener(gameRef, listener)
    }
    
    private fun generateRoomId(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..4).map { chars.random() }.joinToString("")
    }
    
    private fun generatePasscode(): String {
        return (1000..9999).random().toString()
    }
}

data class DatabaseListener(
    val reference: DatabaseReference,
    val listener: ValueEventListener?
) {
    fun remove() {
        listener?.let { reference.removeEventListener(it) }
    }
}
