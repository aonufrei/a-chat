package com.aonufrei.achat.service

import com.aonufrei.achat.dto.ChatMessage
import com.aonufrei.achat.dto.MessageInDto
import com.aonufrei.achat.model.Message
import com.aonufrei.achat.model.Party
import com.aonufrei.achat.repo.MessageRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class MessageService(
    val msgRepo: MessageRepository,
    val msgTemplate: SimpMessagingTemplate,
    val objectMapper: ObjectMapper
) {
    companion object {
        const val LAST_MESSAGE_COUNT = 15
    }

    val executor: ExecutorService = Executors.newSingleThreadExecutor();

    val log: Logger = LoggerFactory.getLogger("MessageService.class")

    fun addMessage(from: Party, msg: MessageInDto) {
        if (msg.content.isBlank()) {
            throw RuntimeException("Message content is required")
        }
        log.info("Add message request $msg")
        msgRepo.save(Message(from = from, content = msg.content))
        updateChat()
    }

    fun updateChat() {
        executor.submit {
            val lastMessages = getLastChatMessages(LAST_MESSAGE_COUNT)
            msgTemplate.convertAndSend("/topic/public", objectMapper.writeValueAsString(lastMessages))
        }
    }

    fun getLastMessages(amount: Int): List<Message> {
        return msgRepo.getLastMessages(amount).reversed()
    }

    fun getLastChatMessages(amount: Int): List<ChatMessage> {
        return getLastMessages(amount).map { ChatMessage(it.from.username, it.content) }
    }

}