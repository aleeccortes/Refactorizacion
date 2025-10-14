// src/main/java/org/example/Entidades/Paciente.java
package org.example.Entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name="paciente")
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
public class Paciente extends Persona implements Serializable {

    @OneToOne(mappedBy="paciente", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private HistoriaClinica historiaClinica;

    @Column(name="telefono", nullable=false, length=50)
    private String telefono;

    @Column(name="direccion", nullable=false, length=100)
    private String direccion;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy="paciente", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Cita> citas = new ArrayList<>();

    // builder ctor
    protected Paciente(String nombre, String apellido, String dni, LocalDate fechaNacimiento,
                       TipoSangre tipoSangre, String telefono, String direccion,
                       Hospital hospital, List<Cita> citas, HistoriaClinica historiaClinica){
        super(nombre, apellido, dni, fechaNacimiento, tipoSangre);
        this.telefono = validarString(telefono, "El teléfono no puede ser nulo ni vacío");
        this.direccion = validarString(direccion, "La dirección no puede ser nula ni vacía");
        this.hospital = hospital;
        this.citas = (citas!=null) ? citas : new ArrayList<>();
        this.historiaClinica = (historiaClinica!=null) ? historiaClinica : new HistoriaClinica(this);
    }

    public void setHospital(Hospital h){
        if (this.hospital == h) return;
        if (this.hospital != null) this.hospital.getInternalPacientes().remove(this);
        this.hospital = h;
        if (h != null) h.getInternalPacientes().add(this);
    }

    public void addCita(Cita c){
        if (c!=null && !this.citas.contains(c)){
            this.citas.add(c);
            c.setPaciente(this);
        }
    }
    public List<Cita> getCitas(){ return Collections.unmodifiableList(new ArrayList<>(citas)); }

    private String validarString(String v, String msg){
        Objects.requireNonNull(v, msg);
        if (v.trim().isEmpty()) throw new IllegalArgumentException(msg);
        return v;
    }

    @Override public String toString(){
        return "Paciente{nombre='" + getNombre() + "', apellido='" + getApellido() +
                "', dni='" + getDni() + "', telefono='" + telefono + "', tipoSangre=" + getTipoSangre() + "}";
    }
}
