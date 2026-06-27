package com.example.uambite.dto.response;

import com.example.uambite.model.EstadoPago;
import com.example.uambite.model.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponse {
    private UUID id;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private EstadoPago estado;
    private UUID pedidoId;
}
