package com.aonufrei.achat.model

import jakarta.persistence.*

@Entity
data class Message(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @ManyToOne(fetch = FetchType.EAGER)
        val from: Party,
        val content: String,
)