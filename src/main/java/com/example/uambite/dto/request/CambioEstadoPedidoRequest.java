package com.example.uambite.dto.request;

import com.example.uambite.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoPedidoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoPedido estado;
}
