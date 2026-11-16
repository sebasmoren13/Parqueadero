package com.unicundi.parqueadero.Config;

import com.unicundi.parqueadero.Entity.Rol;
import com.unicundi.parqueadero.Entity.Usuario;
import com.unicundi.parqueadero.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Semilla implements CommandLineRunner {
    private final UsuarioRepository repo;
    private final PasswordEncoder enc;

    @Value("${app.admin.correo}")
    private String correo;

    @Value("${app.admin.password}")
    private String pass;

    @Override
    public void run(String... args) {
        if (repo.existsByCorreoOrCedula(correo, "0")) return;
        Usuario u = new Usuario();
        u.setNombre("Administrador");
        u.setCorreo(correo);
        u.setCedula("0");
        u.setPassword(enc.encode(pass));
        u.setRol(Rol.ADMIN);
        repo.save(u);
    }
}