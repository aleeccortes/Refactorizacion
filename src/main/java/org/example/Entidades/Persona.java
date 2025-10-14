// src/main/java/org/example/Entidades/Persona.java
package org.example.Entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@MappedSuperclass
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public abstract class Persona implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(name="nombre", nullable=false, length=50)
    protected String nombre;

    @ToString.Include
    @Column(name="apellido", nullable=false, length=50)
    protected String apellido;

    @ToString.Include
    @Column(name="dni", nullable=false, unique=true, length=50)
    protected String dni;

    @ToString.Include
    @Column(name="FechaNacimiento", nullable=false)
    protected LocalDate fechaNacimiento;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name="TipoSangre", nullable=false, length=20)
    protected TipoSangre tipoSangre;

    protected Persona(String nombre, String apellido, String dni,
                      LocalDate fechaNacimiento, TipoSangre tipoSangre) {
        this.nombre = validarString(nombre, "El nombre no puede ser nulo ni vacío");
        this.apellido = validarString(apellido, "El apellido no puede ser nulo ni vacío");
        this.dni = validarDni(dni);
        this.fechaNacimiento = Objects.requireNonNull(fechaNacimiento, "La fecha no puede ser nula");
        this.tipoSangre = Objects.requireNonNull(tipoSangre, "El tipo de sangre no puede ser nulo");
    }

    public String getNombreCompleto() { return nombre + " " + apellido; }

    private String validarString(String v, String msg){
        Objects.requireNonNull(v, msg);
        if (v.trim().isEmpty()) throw new IllegalArgumentException(msg);
        return v;
    }
    protected String validarDni(String dni){
        Objects.requireNonNull(dni, "El DNI no puede ser nulo");
        if (!dni.matches("\\d{7,8}")) throw new IllegalArgumentException("El DNI debe tener 7 u 8 dígitos");
        return dni;
    }
}
