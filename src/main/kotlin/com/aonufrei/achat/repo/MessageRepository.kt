package com.aonufrei.achat.repo

import com.aonufrei.achat.model.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MessageRepository : JpaRepository<Message, Long> {
    @Query("from Message as msg order by msg.id desc limit :amount")
    fun getLastMessages(amount: Int) : List<Message>
}