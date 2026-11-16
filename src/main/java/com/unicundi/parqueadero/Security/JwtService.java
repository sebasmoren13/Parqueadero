package com.unicundi.parqueadero.Security;

import com.unicundi.parqueadero.Entity.TipoQr;
import com.unicundi.parqueadero.Entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expHoras;
    private final long qrSeg;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.exp-horas}") long expHoras,
                      @Value("${app.qr.vigencia-seg}") long qrSeg) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expHoras = expHoras;
        this.qrSeg = qrSeg;
    }

    public String token(Usuario u) {
        return Jwts.builder()
                .subject(String.valueOf(u.getId()))
                .claim("rol", u.getRol().name())
                .expiration(new Date(System.currentTimeMillis() + expHoras * 3_600_000))
                .signWith(key)
                .compact();
    }

    public String qr(TipoQr tipo) {
        return Jwts.builder()
                .subject("QR")
                .claim("tipo", tipo.name())
                .id(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + qrSeg * 1000))
                .signWith(key)
                .compact();
    }

    public long qrSegundos() {
        return qrSeg;
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public void validarQr(String token, TipoQr esperado) {
        try {
            if (!esperado.name().equals(parse(token).get("tipo"))) throw new IllegalArgumentException();
        } catch (Exception e) {
            throw new IllegalArgumentException("QR inválido o vencido: debes escanearlo en la sede");
        }
    }
}