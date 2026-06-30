package com.example.uambite.dto.request;

import com.example.uambite.model.MetodoPago;
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
public class PagoRequest {

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    @NotNull(message = "El pedido es obligatorio")
    private UUID pedidoId;
}
