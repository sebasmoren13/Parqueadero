package com.unicundi.parqueadero.Repository;

import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.Cupo;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CupoRepository extends JpaRepository<Cupo, Long> {
    List<Cupo> findByCategoriaOrderByNumero(Categoria categoria);
    long countByCategoria(Categoria categoria);
    long countByCategoriaAndOcupadoFalse(Categoria categoria);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cupo c where c.categoria = :cat and c.ocupado = false order by c.numero")
    List<Cupo> libres(@Param("cat") Categoria cat, Pageable pageable);
}