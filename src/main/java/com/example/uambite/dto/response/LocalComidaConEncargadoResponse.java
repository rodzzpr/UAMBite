package com.example.uambite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalComidaConEncargadoResponse {
    private LocalComidaResponse local;
    private UsuarioResponse encargado;
}
