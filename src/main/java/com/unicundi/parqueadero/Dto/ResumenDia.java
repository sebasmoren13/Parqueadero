package com.unicundi.parqueadero.Dto;

import java.time.LocalDate;

public record ResumenDia(LocalDate fecha, long bicicletas, long motos, long vehiculos, long total) {}