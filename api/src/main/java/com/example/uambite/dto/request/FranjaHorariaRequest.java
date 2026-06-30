package com.example.uambite.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranjaHorariaRequest {

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a cero")
    @Min(value = 1, message = "La capacidad mínima es 1")
    private Integer capacidadMaxima;

    private Integer pedidosActuales;

    private Boolean disponible;

    @NotNull(message = "El local de comida es obligatorio")
    private UUID localComidaId;
}
