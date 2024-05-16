package com.aonufrei.achat.repo

import com.aonufrei.achat.model.Party
import org.springframework.data.jpa.repository.JpaRepository

interface PartyRepository : JpaRepository<Party, Long> {
    fun findByUsername(username: String) : Party?
}
