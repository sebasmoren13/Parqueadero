package com.unicundi.parqueadero.Dto;

import jakarta.validation.constraints.Size;

public record DetalleReq(@Size(max = 20) String placa,
                         @Size(max = 255) String descripcion,
                         @Size(max = 1000) String novedades) {}