package com.example.uambite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalComidaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 255, message = "La ubicación no puede exceder 255 caracteres")
    private String ubicacion;

    @Size(max = 100, message = "El horario no puede exceder 100 caracteres")
    private String horario;
}
