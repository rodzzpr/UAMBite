package com.example.uambite.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "descuento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Descuento extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false)
    private Double porcentaje;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private Boolean activo;

    @JsonBackReference(value = "local-descuento")
    @ManyToOne
    @JoinColumn(name = "local_comida_id")
    private LocalComida localComida;
}
