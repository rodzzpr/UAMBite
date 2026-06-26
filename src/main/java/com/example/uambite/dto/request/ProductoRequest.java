package com.example.uambite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser mayor a cero")
    private Integer stock;

    @NotNull(message = "Debe indicar si el producto permite personalización")
    private Boolean permitePersonalizacion;

    @NotNull(message = "El local de comida es obligatorio")
    private UUID localComidaId;
}
