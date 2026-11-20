package com.unicundi.parqueadero.Repository;

import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.Registro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistroRepository extends JpaRepository<Registro, Long> {
    Optional<Registro> findByUsuarioIdAndActivoTrue(Long usuarioId);
    List<Registro> findByUsuarioIdOrderByEntradaDesc(Long usuarioId);
    List<Registro> findByActivoTrueOrderByEntradaDesc();

    @Query("select r from Registro r where r.entrada >= :ini and r.entrada < :fin order by r.entrada desc")
    List<Registro> delDia(@Param("ini") LocalDateTime ini, @Param("fin") LocalDateTime fin);

    @Query("select r from Registro r where r.entrada >= :ini and r.entrada < :fin "
            + "and r.cupo.categoria = :cat order by r.entrada desc")
    List<Registro> delDiaPorCategoria(@Param("ini") LocalDateTime ini, @Param("fin") LocalDateTime fin,
                                      @Param("cat") Categoria cat);

    @Query("select r.cupo.categoria, count(r) from Registro r "
            + "where r.entrada >= :ini and r.entrada < :fin group by r.cupo.categoria")
    List<Object[]> contarPorCategoria(@Param("ini") LocalDateTime ini, @Param("fin") LocalDateTime fin);

    @Query("select r from Registro r join r.usuario u "
            + "where lower(coalesce(r.placa, '')) like :p "
            + "or lower(u.nombre) like :p "
            + "or lower(u.correo) like :p "
            + "or coalesce(u.cedula, '') like :p "
            + "order by r.entrada desc")
    List<Registro> buscar(@Param("p") String patron, Pageable pageable);
}