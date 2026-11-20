package com.unicundi.parqueadero.Dto;

import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.Cupo;
import com.unicundi.parqueadero.Entity.Registro;

import java.time.LocalDateTime;

public record RegistroRes(Long id, Categoria categoria, String cupo, LocalDateTime entrada,
                          LocalDateTime salida, String mensaje) {

    public static String etiqueta(Cupo c) {
        return c.getCategoria().name().substring(0, 1) + "-" + String.format("%02d", c.getNumero());
    }

    public static RegistroRes de(Registro r) {
        Cupo c = r.getCupo();
        String et = etiqueta(c);
        String msg = r.getSalida() != null
                ? "Salida registrada. Se liberó el cupo " + et
                : "Ingreso registrado. Dirígete al cupo " + et;
        return new RegistroRes(r.getId(), c.getCategoria(), et, r.getEntrada(), r.getSalida(), msg);
    }
}