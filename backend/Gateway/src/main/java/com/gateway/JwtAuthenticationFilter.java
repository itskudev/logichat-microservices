package com.gateway;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private final SecretKey signingKey;
    private final boolean enabled;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret,
                                   @Value("${gateway.security.enabled:false}") boolean enabled) {
        this.enabled = enabled;
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                "jwt.secret debe tener al menos 32 bytes para HS256"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Paso a paso: sin Security el Gateway deja pasar todo.
        // TODO(security): activar con GATEWAY_SECURITY_ENABLED=true y exigir Bearer.
        if (!enabled) {
            return chain.filter(exchange);
        }
        String path = exchange.getRequest().getURI().getPath();
        // El endpoint que emite el token debe poder invocarse sin un token previo.
        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange, "Falta el encabezado Authorization: Bearer <token>");
        }

        String token = authorization.substring(7).trim();
        if (token.isEmpty()) {
            return unauthorized(exchange, "Token vacío");
        }

        try {
            // Verifica firma, estructura y expiración (exp) del JWT.
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
            // El encabezado Authorization original se conserva al reenviar la petición.
            return chain.filter(exchange);
        } catch (JwtException | IllegalArgumentException ex) {
            return unauthorized(exchange, "Token inválido o expirado");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"error\":\"unauthorized\",\"message\":\"" + message + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
