package com.unicundi.parqueadero.Repository;

import com.unicundi.parqueadero.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoOrCedula(String correo, String cedula);
    boolean existsByCorreoOrCedula(String correo, String cedula);
    boolean existsByCorreo(String correo);
    List<Usuario> findByVisitanteTrueOrderByCreadoEnDesc();
}