package com.example.uambite.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrioridadRequest {

    @NotNull(message = "La prioridad es obligatoria")
    @Min(value = 0, message = "La prioridad debe ser >= 0")
    private Integer prioridad;
}
