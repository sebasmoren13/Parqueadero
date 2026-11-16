package com.unicundi.parqueadero.Repository;

import com.unicundi.parqueadero.Entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findTop20ByOrderByFechaDesc();
}