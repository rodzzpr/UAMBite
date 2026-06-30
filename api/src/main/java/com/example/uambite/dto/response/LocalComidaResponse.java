package com.example.uambite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalComidaResponse {
    private UUID id;
    private String nombre;
    private String ubicacion;
    private String horario;
    private UUID duenoId;
    private Boolean tieneImagen;
}
