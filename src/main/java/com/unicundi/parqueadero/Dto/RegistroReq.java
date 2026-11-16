package com.unicundi.parqueadero.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroReq(
        @NotBlank String nombre,
        @Email @NotBlank String correo,
        @NotBlank String cedula,
        @Size(min = 6) String password) {}