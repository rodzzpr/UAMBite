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
@Table(name = "local_comida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalComida extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String ubicacion;

    @Column(length = 100)
    private String horario;

    @JsonManagedReference(value = "local-producto")
    @OneToMany(mappedBy = "localComida")
    private List<Producto> productos;

    @JsonManagedReference(value = "local-descuento")
    @OneToMany(mappedBy = "localComida")
    private List<Descuento> descuentos;

    @JsonManagedReference(value = "local-franja")
    @OneToMany(mappedBy = "localComida")
    private List<FranjaHoraria> franjas;
}
