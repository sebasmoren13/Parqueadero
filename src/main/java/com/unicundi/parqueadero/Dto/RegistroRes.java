package com.unicundi.parqueadero.Dto;

import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.Cupo;
import com.unicundi.parqueadero.Entity.Registro;

import java.time.LocalDateTime;

public record RegistroRes(Long id, Categoria categoria, String cupo, LocalDateTime entrada, LocalDateTime salida) {
    public static RegistroRes de(Registro r) {
        Cupo c = r.getCupo();
        String etiqueta = c.getCategoria().name().substring(0, 1) + "-" + String.format("%02d", c.getNumero());
        return new RegistroRes(r.getId(), c.getCategoria(), etiqueta, r.getEntrada(), r.getSalida());
    }
}