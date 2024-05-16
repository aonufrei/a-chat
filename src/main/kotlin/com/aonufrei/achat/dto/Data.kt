package com.aonufrei.achat.dto

data class ChatMessage(val from: String, val content: String)
data class MessageInDto(val content: String)
data class PartyInDto(val username: String, val password: String)
data class PartyOutDto(val id: Long, val name: String)

data class RegisterInDto(val username: String, val password: String, val repeatPassword: String)