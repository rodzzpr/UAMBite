package com.example.uambite.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class DescuentoRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50)
    private String codigo;

    @NotNull(message = "El porcentaje es obligatorio")
    @DecimalMin(value = "0.01", message = "El porcentaje debe ser mayor a cero")
    @DecimalMax(value = "100.00", message = "El porcentaje no puede superar 100")
    @Digits(integer = 3, fraction = 2, message = "El porcentaje debe tener máximo 2 decimales")
    private BigDecimal porcentaje;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;

    @NotNull(message = "Debe indicar si el descuento está activo")
    private Boolean activo;

    private UUID localComidaId;
}
