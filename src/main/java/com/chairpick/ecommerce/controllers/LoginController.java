package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.input.LoginInput;
import com.chairpick.ecommerce.io.output.TokenResponseDTO;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import com.chairpick.ecommerce.services.LoginService;
import com.chairpick.ecommerce.services.TokenService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Controller
public class LoginController {

    private final LoginService loginService;
    private final TokenService tokenService;

    public LoginController(LoginService loginService, TokenService tokenService) {
        this.loginService = loginService;
        this.tokenService = tokenService;
    }

    @GetMapping("/login")
    public ModelAndView redirectToLogin() {
        return new ModelAndView("auth/login.html");
    }

    @PostMapping("/login")
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
}
