package com.example.uambite.dto.response;

import com.example.uambite.model.EstadoEntrega;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaResponse {
    private UUID id;
    private String ubicacion;
    private LocalDateTime fechaEntrega;
    private EstadoEntrega estado;
    private UUID pedidoId;
}
