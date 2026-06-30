package com.example.uambite.dto.request;

import com.example.uambite.model.EstadoEntrega;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoEntregaRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoEntrega estado;
}
