package com.example.uambite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescuentoResponse {
    private UUID id;
    private String codigo;
    private BigDecimal porcentaje;
    private LocalDate fechaVencimiento;
    private Boolean activo;
    private String localComida;
}
