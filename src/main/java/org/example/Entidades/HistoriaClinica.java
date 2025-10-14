// src/main/java/org/example/Entidades/HistoriaClinica.java
package org.example.Entidades;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoriaClinica implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="numero_historia", nullable=false, unique=true, length=50)
    private String numeroHistoria;

    @OneToOne
    @JoinColumn(name="paciente_id", nullable=false, unique=true)
    private Paciente paciente;

    @Column(nullable=false)
    private LocalDateTime fechaCreacion;

    @ElementCollection
    @CollectionTable(name="diagnosticos", joinColumns=@JoinColumn(name="historia_id"))
    @Column(name="diagnostico", nullable=false, length=200)
    private List<String> diagnosticos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="tratamientos", joinColumns=@JoinColumn(name="historia_id"))
    @Column(name="tratamiento", nullable=false, length=200)
    private List<String> tratamientos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="alergias", joinColumns=@JoinColumn(name="historia_id"))
    @Column(name="alergia", nullable=false, length=200)
    private List<String> alergias = new ArrayList<>();

    public HistoriaClinica(Paciente paciente){
        this.paciente = Objects.requireNonNull(paciente, "El paciente no puede ser nulo");
        this.fechaCreacion = LocalDateTime.now();
        this.numeroHistoria = generarNumeroHistoria();
    }

    private String generarNumeroHistoria(){
        return "HC-" + paciente.getDni() + "-" + fechaCreacion.getYear();
    }

    public void agregarDiagnostico(String d){ if (d!=null && !d.trim().isEmpty()) diagnosticos.add(d); }
    public void agregarTratamiento(String t){ if (t!=null && !t.trim().isEmpty()) tratamientos.add(t); }
    public void agregarAlergia(String a){ if (a!=null && !a.trim().isEmpty()) alergias.add(a); }

    public List<String> getDiagnosticos(){ return Collections.unmodifiableList(diagnosticos); }
    public List<String> getTratamientos(){ return Collections.unmodifiableList(tratamientos); }
    public List<String> getAlergias(){ return Collections.unmodifiableList(alergias); }

    @Override public String toString(){
        return "HistoriaClinica{numeroHistoria='" + numeroHistoria + "', paciente=" +
                (paciente!=null?paciente.getNombreCompleto():"N/A") + ", fechaCreacion=" + fechaCreacion + "}";
    }
}
