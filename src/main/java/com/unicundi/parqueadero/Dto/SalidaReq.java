package com.unicundi.parqueadero.Dto;

import jakarta.validation.constraints.NotBlank;

public record SalidaReq(@NotBlank String qr) {}