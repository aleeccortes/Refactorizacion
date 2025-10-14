// src/main/java/org/example/Entidades/Departamento.java
package org.example.Entidades;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="departamentos")
@Getter @Builder @AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Departamento implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=50)
    private EspecialidadMedica especialidad;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy="departamento", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default
    private List<Medico> medicos = new ArrayList<>();

    @OneToMany(mappedBy="departamento", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default
    private List<Sala> salas = new ArrayList<>();

    public Departamento(String nombre, EspecialidadMedica esp){
        this.nombre = validarString(nombre, "El nombre del departamento no puede ser nulo ni vacío");
        this.especialidad = Objects.requireNonNull(esp, "La especialidad no puede ser nula");
    }

    public void setHospital(Hospital h){
        if (this.hospital==h) return;
        if (this.hospital!=null) this.hospital.getInternalDepartamentos().remove(this);
        this.hospital = h;
        if (h!=null) h.getInternalDepartamentos().add(this);
    }

    public void agregarMedico(Medico m){
        if (m==null || medicos.contains(m)) return;
        medicos.add(m);
        m.setDepartamento(this);
    }

    public Sala crearSala(String numero, String tipo){
        Sala s = new Sala(numero, tipo, this);
        salas.add(s);
        return s;
    }

    public List<Medico> getMedicos(){ return Collections.unmodifiableList(medicos); }
    public List<Sala> getSalas(){ return Collections.unmodifiableList(salas); }

    private String validarString(String v, String msg){
        Objects.requireNonNull(v, msg);
        if (v.trim().isEmpty()) throw new IllegalArgumentException(msg);
        return v;
    }
}
