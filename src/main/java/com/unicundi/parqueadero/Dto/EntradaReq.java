package com.unicundi.parqueadero.Dto;

import com.unicundi.parqueadero.Entity.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EntradaReq(@NotNull Categoria categoria, @NotBlank String qr) {}