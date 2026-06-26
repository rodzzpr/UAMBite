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
public class ProductoIngredienteExtraResponse {
    private UUID id;
    private UUID productoId;
    private String producto;
    private UUID ingredienteExtraId;
    private String ingredienteExtra;
}
