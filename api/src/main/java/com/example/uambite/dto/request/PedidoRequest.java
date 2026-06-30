package com.example.uambite.dto.request;

import com.example.uambite.model.TipoEntrega;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequest {

    @NotNull(message = "El tipo de entrega es obligatorio")
    private TipoEntrega tipoEntrega;

    @NotNull(message = "El usuario es obligatorio")
    private UUID usuarioId;

    private UUID franjaHorariaId;

    private UUID descuentoId;
}
