package com.unicundi.parqueadero.Repository;

import com.unicundi.parqueadero.Entity.Registro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistroRepository extends JpaRepository<Registro, Long> {
    Optional<Registro> findByUsuarioIdAndActivoTrue(Long usuarioId);
    List<Registro> findByUsuarioIdOrderByEntradaDesc(Long usuarioId);
}