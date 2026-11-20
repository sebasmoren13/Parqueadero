package com.unicundi.parqueadero.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(unique = true)
    private String cedula;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private Boolean visitante;

    @Column(length = 500)
    private String motivo;

    private LocalDateTime creadoEn;
}