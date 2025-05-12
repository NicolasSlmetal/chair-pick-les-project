package com.chairpick.ecommerce.config.ws;

import jakarta.websocket.server.HandshakeRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

public class WebSocketHandshake extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String email;
        Principal principal = request.getPrincipal();
        if (principal == null) {
            return null;
        }

        email = principal.getName();

        if (email != null) {
            return () -> email;
        }
        return super.determineUser(request, wsHandler, attributes);
    }
}
