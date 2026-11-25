package cl.tienda.ms.ventas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.HexFormat;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        byte[] bytes = HexFormat.of().parseHex(secret);
        if (bytes.length != 32) {
            throw new IllegalArgumentException("JWT_SECRET debe contener 32 bytes hexadecimales");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public Claims validarToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key)
                .requireIssuer("ms-identidad").require("tipo", "access")
                .build().parseClaimsJws(token).getBody();
        if (claims.getExpiration() == null || claims.getSubject() == null
                || claims.get("correo", String.class) == null) {
            throw new MalformedJwtException("Token incompleto");
        }
        String rol = claims.get("rol", String.class);
        if (!"admin".equals(rol) && !"cliente".equals(rol)) {
            throw new MalformedJwtException("Rol inválido");
        }
        Long id = Long.valueOf(claims.getSubject());
        Object usuarioId = claims.get("usuarioId");
        if (id <= 0 || !(usuarioId instanceof Number numero) || numero.longValue() != id) {
            throw new MalformedJwtException("Identificador inválido");
        }
        return claims;
    }
}
