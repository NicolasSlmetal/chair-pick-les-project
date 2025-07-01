package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.io.input.AlterPasswordInput;
import com.chairpick.ecommerce.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResetPasswordController {

    private final LoginService loginService;

    public ResetPasswordController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/reset-password")
    public ModelAndView redirectToResetPassword(@RequestParam("token") String token) {
        ModelAndView view = new ModelAndView("reset-password/index.html");
        String email = loginService.validateToken(token);
        view.addObject("email", email);
        return view;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody AlterPasswordInput input) {
        loginService.resetPassword(input);
        return ResponseEntity.ok("Password reset successfully.");
    }
}
