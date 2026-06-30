package com.example.uambite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Boolean permitePersonalizacion;
    private String localComida;
    private List<UUID> ingredientesExtraIds;
    private Boolean tieneImagen;
}
