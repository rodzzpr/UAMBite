package com.example.uambite.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteExtraRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotNull(message = "El precio extra es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio extra debe ser mayor a cero")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precioExtra;
}
