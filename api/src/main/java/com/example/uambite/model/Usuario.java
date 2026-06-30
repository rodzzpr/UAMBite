package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String carnet;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(length = 150)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean passwordTemporal = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Rol rol;

    @JsonManagedReference(value = "usuario-pedido")
    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;
}
