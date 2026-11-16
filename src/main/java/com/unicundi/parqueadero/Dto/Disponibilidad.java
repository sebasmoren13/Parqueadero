package com.unicundi.parqueadero.Dto;

import com.unicundi.parqueadero.Entity.Categoria;

public record Disponibilidad(Categoria categoria, long total, long disponibles, long ocupados) {}