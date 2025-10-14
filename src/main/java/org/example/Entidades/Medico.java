// src/main/java/org/example/Entidades/Medico.java
package org.example.Entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="medico")
@SuperBuilder
@Getter
@NoArgsConstructor
public class Medico extends Persona implements Serializable {

    @Embedded
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=50)
    private EspecialidadMedica especialidad;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="departamento_id")
    private Departamento departamento;

    @OneToMany(mappedBy="medico", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default
    private List<Cita> citas = new ArrayList<>();

    public Medico(String nombre, String apellido, String dni, LocalDate fechaNacimiento,
                  TipoSangre tipoSangre, String numeroMatricula, EspecialidadMedica especialidad){
        super(nombre, apellido, dni, fechaNacimiento, tipoSangre);
        this.matricula = new Matricula(numeroMatricula);
        this.especialidad = especialidad;
        this.citas = new ArrayList<>();
    }

    public void setDepartamento(Departamento d){ this.departamento = d; }

    public void addCita(Cita c){
        if (c!=null && !citas.contains(c)){
            citas.add(c);
            c.setMedico(this);
        }
    }
    public List<Cita> getCitas(){ return Collections.unmodifiableList(new ArrayList<>(citas)); }

    @Override public String toString(){
        return "Medico{nombre='" + getNombre() + "', apellido='" + getApellido() +
                "', especialidad=" + (especialidad!=null?especialidad.name():"null") +
                ", matricula=" + (matricula!=null?matricula.getNumero():"null") + "}";
    }
}
