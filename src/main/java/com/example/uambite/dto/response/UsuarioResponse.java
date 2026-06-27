package com.example.uambite.dto.response;

import com.example.uambite.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
    private UUID id;
    private String carnet;
    private String nombre;
    private String apellido;
    private String correo;
    private Rol rol;
}
