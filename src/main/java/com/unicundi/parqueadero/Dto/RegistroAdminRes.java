package com.unicundi.parqueadero.Dto;

import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.Registro;
import com.unicundi.parqueadero.Entity.Usuario;

import java.time.LocalDateTime;

public record RegistroAdminRes(Long id, String usuario, String correo, String cedula, boolean visitante,
                               String motivo, Categoria categoria, String cupo, String placa,
                               String descripcion, String novedades, LocalDateTime entrada,
                               LocalDateTime salida, boolean activo, boolean tieneFoto) {

    public static RegistroAdminRes de(Registro r) {
        Usuario u = r.getUsuario();
        return new RegistroAdminRes(r.getId(), u.getNombre(), u.getCorreo(), u.getCedula(),
                Boolean.TRUE.equals(u.getVisitante()), u.getMotivo(),
                r.getCupo().getCategoria(), RegistroRes.etiqueta(r.getCupo()),
                r.getPlaca(), r.getDescripcion(), r.getNovedades(),
                r.getEntrada(), r.getSalida(), r.isActivo(), Boolean.TRUE.equals(r.getTieneFoto()));
    }
}