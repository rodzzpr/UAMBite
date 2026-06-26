package com.example.uambite.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "El carnet es obligatorio")
    @Size(min = 4, max = 20, message = "El carnet debe tener entre 4 y 20 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "El carnet solo puede contener letras y números")
    private String carnet;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;

    @Pattern(regexp = "^(CLIENTE|ADMIN|LOCAL)$",
            message = "El rol debe ser CLIENTE, ADMIN o LOCAL")
    private String rol;
}
