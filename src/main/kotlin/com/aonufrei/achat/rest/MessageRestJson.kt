package com.aonufrei.achat.rest

import com.aonufrei.achat.dto.ChatMessage
import com.aonufrei.achat.dto.MessageInDto
import com.aonufrei.achat.model.Message
import com.aonufrei.achat.repo.MessageRepository
import com.aonufrei.achat.service.AuthService
import com.aonufrei.achat.service.MessageService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/messages")
class MessageRestJson(val msgService: MessageService,
                      val authService: AuthService) {

    @GetMapping
    fun getChatMessages(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): ResponseEntity<List<ChatMessage>> {
        authService.partyFromToken(token) ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        return ResponseEntity(msgService.getLastChatMessages(MessageService.LAST_MESSAGE_COUNT), HttpStatus.OK);
    }

    @PostMapping
    fun sendMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
                    @RequestBody message: MessageInDto
    ): ResponseEntity<Void> {
        val partyFromToken = authService.partyFromToken(token) ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        msgService.addMessage(partyFromToken, message)
        return ResponseEntity(HttpStatus.OK)
    }

}