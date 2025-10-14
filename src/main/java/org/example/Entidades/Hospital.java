// src/main/java/org/example/Entidades/Hospital.java
package org.example.Entidades;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="Hospital")
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class Hospital implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=200) private String nombre;
    @Column(nullable=false, length=300) private String direccion;
    @Column(nullable=false, length=20)  private String telefono;

    @OneToMany(mappedBy="hospital", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default
    private List<Departamento> departamentos = new ArrayList<>();

    @OneToMany(mappedBy="hospital", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default
    private List<Paciente> pacientes = new ArrayList<>();

    public void agregarDepartamento(Departamento d){
        if (d==null || departamentos.contains(d)) return;
        departamentos.add(d);
        d.setHospital(this);
    }
    public void agregarPaciente(Paciente p){
        if (p==null || pacientes.contains(p)) return;
        pacientes.add(p);
        p.setHospital(this);
    }

    public List<Departamento> getDepartamentos(){ return Collections.unmodifiableList(departamentos); }
    public List<Paciente> getPacientes(){ return Collections.unmodifiableList(pacientes); }

    List<Departamento> getInternalDepartamentos(){ return departamentos; }
    List<Paciente> getInternalPacientes(){ return pacientes; }

    private String validarString(String v, String msg){
        Objects.requireNonNull(v, msg);
        if (v.trim().isEmpty()) throw new IllegalArgumentException(msg);
        return v;
    }
}
