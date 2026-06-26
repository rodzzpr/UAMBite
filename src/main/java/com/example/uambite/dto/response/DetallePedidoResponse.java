package com.example.uambite.dto.response;

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
public class DetallePedidoResponse {
    private UUID id;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private String producto;
    private UUID productoId;
    private List<DetallePedidoIngredienteExtraResponse> ingredientesExtra;
}
