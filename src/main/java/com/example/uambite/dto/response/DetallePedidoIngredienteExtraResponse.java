package com.example.uambite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoIngredienteExtraResponse {
    private UUID id;
    private UUID ingredienteExtraId;
    private String nombre;
    private Double precioAdicional;
}
