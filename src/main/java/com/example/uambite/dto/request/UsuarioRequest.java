package com.example.uambite.dto.request;

import jakarta.validation.constraints.Email;
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
public class UsuarioRequest {

    @NotBlank(message = "El carnet es obligatorio")
    @Size(min = 4, max = 20, message = "El carnet debe tener entre 4 y 20 caracteres")
    private String carnet;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 150)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100)
    private String password;
}
