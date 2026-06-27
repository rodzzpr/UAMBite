package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean permitePersonalizacion;

    @Version
    private Long version;

    @JsonBackReference(value = "local-producto")
    @ManyToOne
    @JoinColumn(name = "local_id")
    private LocalComida localComida;

    @JsonManagedReference(value = "producto-detalle")
    @OneToMany(mappedBy = "producto")
    private List<DetallePedido> detalles;

    @JsonManagedReference(value = "producto-ingrediente-extra")
    @OneToMany(mappedBy = "producto")
    private List<ProductoIngredienteExtra> ingredientesExtras;
}
