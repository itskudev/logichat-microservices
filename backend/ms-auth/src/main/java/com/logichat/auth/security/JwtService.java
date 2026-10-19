package com.logichat.auth.security;

import com.logichat.auth.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Genera el token JWT. A diferencia del ejemplo base, incluye claim "roles"
    // para que los micros protegidos resuelvan hasRole()/hasAnyRole().
    public String generateToken(Usuario user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("roles", List.of(user.getRol().getNombre()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + properties.getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }
}
