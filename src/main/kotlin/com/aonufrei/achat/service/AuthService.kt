package com.aonufrei.achat.service

import com.aonufrei.achat.dto.RegisterInDto
import com.aonufrei.achat.exception.UserRegistrationException
import com.aonufrei.achat.model.Party
import com.aonufrei.achat.repo.PartyRepository
import org.springframework.stereotype.Service

@Service
class AuthService(val partyRepo: PartyRepository) {

    fun partyFromToken(token: String) : Party? {
        val (username, password) = parseToken(token) ?: return null
        val user = partyRepo.findByUsername(username) ?: return null
        if (user.password != password) {
            return null
        }
        return user
    }

    fun validateRegistration(data: RegisterInDto) {
        if (data.username.isBlank()) throw UserRegistrationException("Username is required")
        if (data.password.isBlank()) throw UserRegistrationException("Password is required")
        if (data.repeatPassword.isBlank()) throw UserRegistrationException("Repeat password is required")

        val minUsernameLength = 3
        val minPasswordLength = 8
        if (data.username.length < minUsernameLength)
            throw UserRegistrationException("Username cannot be shorter that $minUsernameLength symbols")
        if (data.password.length < minPasswordLength)
            throw UserRegistrationException("Password cannot be shorter that $minPasswordLength symbols")

        if (data.password != data.repeatPassword) throw UserRegistrationException("Passwords are not equal")
        if (partyRepo.findByUsername(data.username) != null)
            throw UserRegistrationException("Provided username is already in use")
    }

    fun register(data: RegisterInDto) {
        validateRegistration(data)
        partyRepo.save(Party(username = data.username, password = data.password))
    }

    fun parseToken(token: String): Pair<String, String>? {
        val parts = token.split(':')
        if (parts.size != 2) return null
        return Pair(parts[0], parts[1])
    }

    fun createToken(username: String, password: String): Pair<String, Boolean> {
        val party = partyRepo.findByUsername(username) ?: return failedLoginResp()
        if (party.password != password) return failedLoginResp()
        return Pair("$username:$password", true)
    }

    fun failedLoginResp() = Pair("", false)
}