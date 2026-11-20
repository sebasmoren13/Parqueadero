package com.unicundi.parqueadero.Controller;

import com.unicundi.parqueadero.Dto.LoginReq;
import com.unicundi.parqueadero.Dto.RegistroReq;
import com.unicundi.parqueadero.Dto.RegistroVisitanteReq;
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

import java.time.LocalDateTime;
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

    private Map<String, Object> respuesta(Usuario u) {
        return Map.of("token", jwt.token(u), "rol", u.getRol(),
                "visitante", Boolean.TRUE.equals(u.getVisitante()));
    }

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
        u.setVisitante(false);
        u.setCreadoEn(LocalDateTime.now());
        repo.save(u);
        return respuesta(u);
    }

    @PostMapping("/registro-visitante")
    public Map<String, Object> registroVisitante(@Valid @RequestBody RegistroVisitanteReq r) {
        String correo = r.correo().toLowerCase();
        if (repo.existsByCorreo(correo))
            throw new IllegalStateException("Ese correo ya está registrado");

        Usuario u = new Usuario();
        u.setNombre(r.nombre());
        u.setCorreo(correo);
        u.setMotivo(r.motivo().trim());
        u.setPassword(enc.encode(r.password()));
        u.setRol(Rol.ESTUDIANTE);
        u.setVisitante(true);
        u.setCreadoEn(LocalDateTime.now());
        repo.save(u);
        return respuesta(u);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginReq r) {
        Usuario u = repo.findByCorreoOrCedula(r.identificador().toLowerCase(), r.identificador())
                .filter(x -> enc.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        return respuesta(u);
    }
}