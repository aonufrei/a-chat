package com.aonufrei.achat.controller

import com.aonufrei.achat.dto.RegisterInDto
import com.aonufrei.achat.exception.UserRegistrationException
import com.aonufrei.achat.service.AuthService
import com.aonufrei.achat.service.MessageService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.socket.messaging.SessionConnectEvent


@Controller
class ChatController(val authService: AuthService, val msgService: MessageService) {

    @GetMapping("/")
    fun home(request: HttpServletRequest): String{
        if (isValidToken(request)) {
            return "redirect:/chat"
        }
        return "redirect:/login"
    }

    @GetMapping("/login")
    fun loginPage(@RequestParam("failed", required = false) failed: Boolean?, model: Model): String {
        model.addAttribute("failed", failed ?: false)
        return "login"
    }

    @PostMapping("/login")
    fun loginForm(@RequestParam("username") username: String,
                  @RequestParam("password") password: String,
                  request: HttpServletRequest,
                  attr: RedirectAttributes): String {
        val (token, success) = authService.createToken(username, password)
        if (success) {
            request.session.setAttribute("token", token)
            return "redirect:/chat"
        }
        attr.addAttribute("failed", true)
        return "redirect:/login"
    }

    @GetMapping("/register")
    fun registerPage(@RequestParam("errorMessage", required = false) errorMessage: String?, model: Model): String {
        model.addAttribute("errorMessage", errorMessage ?: "")
        return "register"
    }

    @PostMapping("/register")
    fun registerPost(@RequestParam("username") username: String,
                     @RequestParam("password") password: String,
                     @RequestParam("repeatPassword") repeatPassword: String,
                     request: HttpServletRequest,
                     attr: RedirectAttributes): String {
        if (isValidToken(request)) return "redirect:/register"
        try {
            authService.register(RegisterInDto(username, password, repeatPassword))
        } catch (e: UserRegistrationException) {
            attr.addAttribute("errorMessage", e.message)
            return "redirect:/register"
        }
        return "redirect:/login"
    }


    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.getSession(false)?.invalidate()
        return "redirect:/login"
    }

    @GetMapping("/chat")
    fun chatPage(request: HttpServletRequest, data: Model): String {
        if (!isValidToken(request)) return "redirect:/login"
        val token = request.session.getAttribute("token") as String
        data.addAttribute("token", token)
        data.addAttribute("messages", msgService.getLastChatMessages(10))
        data.addAttribute("currentUsername", authService.parseToken(token)?.first ?: "")
        return "chat"
    }


    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectEvent) {
        msgService.updateChat()
    }

    fun isValidToken(request: HttpServletRequest): Boolean {
        val sessionTokenAttribute = request.getSession(false)?.getAttribute("token") ?: return false
        val token = sessionTokenAttribute as String
        return authService.parseToken(token) != null
    }
}