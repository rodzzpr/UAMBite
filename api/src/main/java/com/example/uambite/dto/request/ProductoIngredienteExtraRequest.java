package com.example.uambite.dto.request;

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
public class ProductoIngredienteExtraRequest {

    @NotNull(message = "El producto es obligatorio")
    private UUID productoId;

    @NotNull(message = "El ingrediente extra es obligatorio")
    private UUID ingredienteExtraId;
}
