package com.unicundi.parqueadero.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroVisitanteReq(@NotBlank String nombre,
                                   @Email @NotBlank String correo,
                                   @NotBlank @Size(max = 500) String motivo,
                                   @Size(min = 6) String password) {}