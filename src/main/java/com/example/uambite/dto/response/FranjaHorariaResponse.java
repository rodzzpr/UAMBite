package com.example.uambite.dto.response;

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
public class FranjaHorariaResponse {
    private UUID id;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer capacidadMaxima;
    private Integer pedidosActuales;
    private Boolean disponible;
    private UUID localComidaId;
    private String localComida;
}
