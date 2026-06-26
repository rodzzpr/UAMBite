package com.example.uambite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EntregaRequest {

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 255)
    private String ubicacion;

    @NotNull(message = "El pedido es obligatorio")
    private UUID pedidoId;
}
