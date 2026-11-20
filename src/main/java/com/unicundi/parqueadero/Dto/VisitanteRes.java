package com.unicundi.parqueadero.Dto;

import java.time.LocalDateTime;

public record VisitanteRes(Long id, String nombre, String correo, String motivo, LocalDateTime creadoEn) {}