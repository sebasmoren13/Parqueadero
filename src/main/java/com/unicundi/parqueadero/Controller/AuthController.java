package com.unicundi.parqueadero.Controller;

import com.unicundi.parqueadero.Dto.LoginReq;
import com.unicundi.parqueadero.Dto.RegistroReq;
import com.unicundi.parqueadero.Entity.Rol;
import com.unicundi.parqueadero.Entity.Usuario;
import com.unicundi.parqueadero.Repository.UsuarioRepository;
import com.unicundi.parqueadero.Security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository repo;
    private final PasswordEncoder enc;
    private final JwtService jwt;

    @Value("${app.dominio-correo}")
    private String dominio;

    @PostMapping("/registro")
    public Map<String, Object> registro(@Valid @RequestBody RegistroReq r) {
        if (!r.correo().toLowerCase().endsWith("@" + dominio))
            throw new IllegalArgumentException("Usa tu correo institucional (@" + dominio + ")");
        if (repo.existsByCorreoOrCedula(r.correo().toLowerCase(), r.cedula()))
            throw new IllegalStateException("Correo o cédula ya registrados");

        Usuario u = new Usuario();
        u.setNombre(r.nombre());
        u.setCorreo(r.correo().toLowerCase());
        u.setCedula(r.cedula());
        u.setPassword(enc.encode(r.password()));
        u.setRol(Rol.ESTUDIANTE);
        repo.save(u);
        return Map.of("token", jwt.token(u), "rol", u.getRol());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginReq r) {
        Usuario u = repo.findByCorreoOrCedula(r.identificador().toLowerCase(), r.identificador())
                .filter(x -> enc.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        return Map.of("token", jwt.token(u), "rol", u.getRol());
    }
}