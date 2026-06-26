package com.example.uambite.dto.response;

import com.example.uambite.model.EstadoPedido;
import com.example.uambite.model.TipoEntrega;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponse {
    private UUID id;
    private EstadoPedido estado;
    private Double total;
    private Double subtotal;
    private Double descuentoAplicado;
    private TipoEntrega tipoEntrega;
    private UUID usuarioId;
    private String usuario;
    private UUID franjaHorariaId;
    private UUID descuentoId;
    private List<DetallePedidoResponse> detalles;
    private PagoResponse pago;
    private EntregaResponse entrega;
    private LocalDateTime createdAt;
}
