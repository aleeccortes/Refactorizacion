// src/main/java/org/example/Entidades/Matricula.java
package org.example.Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matricula implements Serializable {
    @Column(name="matricula_numero", nullable=false, unique=true, length=15)
    private String numero;

    public Matricula(String numero) { this.numero = validarMatricula(numero); }

    private String validarMatricula(String n){
        Objects.requireNonNull(n, "El número de matrícula no puede ser nulo");
        if (!n.matches("MP-\\d{4,6}"))
            throw new IllegalArgumentException("Formato inválido. Debe ser como MP-12345");
        return n;
    }
}
