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
@Table(name = "ingrediente_extra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteExtra extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "precio_extra", nullable = false)
    private Double precioExtra;

    @JsonManagedReference(value = "ingrediente-producto")
    @OneToMany(mappedBy = "ingredienteExtra")
    private List<ProductoIngredienteExtra> productos;
}
