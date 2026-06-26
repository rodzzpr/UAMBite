package com.example.uambite.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Positive(message = "El porcentaje debe ser mayor a cero")
    private Double porcentaje;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;

    @NotNull(message = "Debe indicar si el descuento está activo")
    private Boolean activo;

    private UUID localComidaId;
}
