package com.example.uambite.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public class DescuentoResponse {

    private UUID id;

    private String codigo;

    private Double porcentaje;

    private Boolean activo;

    private LocalDate fechaVencimiento;

    // getters y setters
}