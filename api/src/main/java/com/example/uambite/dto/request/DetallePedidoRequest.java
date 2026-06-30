package com.example.uambite.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoRequest {

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Max(value = 100, message = "La cantidad máxima por detalle es 100")
    private Integer cantidad;

    @NotNull(message = "El pedido es obligatorio")
    private UUID pedidoId;

    @NotNull(message = "El producto es obligatorio")
    private UUID productoId;

    private List<UUID> ingredientesExtraIds;
}
