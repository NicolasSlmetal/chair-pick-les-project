package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.input.LoginInput;
import com.chairpick.ecommerce.io.input.NewPasswordInput;
import com.chairpick.ecommerce.io.output.TokenResponseDTO;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import com.chairpick.ecommerce.services.LoginService;
import com.chairpick.ecommerce.services.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;
    private final TokenService tokenService;

    public LoginController(LoginService loginService, TokenService tokenService) {
        this.loginService = loginService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ModelAndView redirectToLogin() {
        return new ModelAndView("auth/login.html");
    }

    @PostMapping
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginInput loginInput) {
        AuthenticatedUser authenticatedUser = loginService.authenticate(loginInput);
        TokenResponseDTO token = tokenService.generateToken(authenticatedUser);
        ResponseCookie cookie = ResponseCookie.from("token", token.token())
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofSeconds(Instant.now().until(token.expiration(), ChronoUnit.SECONDS)))
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(token);

    }

    @PostMapping("/request-reset-password")
    public ResponseEntity<?> requestResetPassword(@RequestBody String email) {
        loginService.sendRequestPassword(email);

        return ResponseEntity.ok().build();
    }

}
