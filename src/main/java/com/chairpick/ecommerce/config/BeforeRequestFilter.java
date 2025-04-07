package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.exceptions.AuthenticationException;
import com.chairpick.ecommerce.model.enums.UserType;
import com.chairpick.ecommerce.repositories.CustomerRepository;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import com.chairpick.ecommerce.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class BeforeRequestFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public BeforeRequestFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (request.getRequestURI().equals("/logout")) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        if (token != null) {
            verifyProvidedToken(request, response, token);

        }
        if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void verifyProvidedToken(HttpServletRequest request, HttpServletResponse response, String token) {
        try {
            AuthenticatedUser authenticatedUser = tokenService.decodeToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser.getUsername(), null, authenticatedUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authenticatedUser.getCustomer() != null) {
                Long customerId = authenticatedUser.getCustomer().getId();
                verifyCustomerId(request, response, customerId);
                request.setAttribute("customerId", customerId);
            }

        } catch (AuthenticationException e) {
            ResponseCookie cookie = ResponseCookie.from("token", null)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .maxAge(0)
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());

        } catch (IOException exception) {
            exception.fillInStackTrace();
        }
    }

    private void verifyCustomerId(HttpServletRequest request, HttpServletResponse response, Long customerId) throws IOException {
        String requestPath = request.getServletPath();
        if (requestPath.startsWith("/customers/") && !requestPath.startsWith("/customers/" + customerId + "/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/404");
        }
    }
}
