package com.unicundi.parqueadero.Dto;

import jakarta.validation.constraints.NotBlank;

public record LoginReq(@NotBlank String identificador, @NotBlank String password) {}