package org.example.Entidades;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@ToString(onlyExplicitlyIncluded = true)
public class HistoriaClinica implements Serializable {

    @ToString.Include
    private final String numeroHistoria;

    // 🔥 En lugar de imprimir el paciente completo, mostramos solo el nombre
    @ToString.Include(name = "paciente")
    private final String nombrePaciente;

    @ToString.Include
    private final LocalDateTime fechaCreacion;

    private final List<String> diagnosticos = new ArrayList<>();
    private final List<String> tratamientos = new ArrayList<>();
    private final List<String> alergias = new ArrayList<>();

    private final transient Paciente paciente; // evitamos imprimir en toString()

    public HistoriaClinica(Paciente paciente) {
        this.paciente = Objects.requireNonNull(paciente, "El paciente no puede ser nulo");
        this.nombrePaciente = paciente.getNombreCompleto(); // Guardamos solo el nombre
        this.fechaCreacion = LocalDateTime.now();
        this.numeroHistoria = generarNumeroHistoria();
    }

    private String generarNumeroHistoria() {
        return "HC-" + paciente.getDni() + "-" + fechaCreacion.getYear();
    }

    public void agregarDiagnostico(String diagnostico) {
        if (diagnostico != null && !diagnostico.trim().isEmpty()) {
            diagnosticos.add(diagnostico);
        }
    }

    public void agregarTratamiento(String tratamiento) {
        if (tratamiento != null && !tratamiento.trim().isEmpty()) {
            tratamientos.add(tratamiento);
        }
    }

    public void agregarAlergia(String alergia) {
        if (alergia != null && !alergia.trim().isEmpty()) {
            alergias.add(alergia);
        }
    }

    public List<String> getDiagnosticos() {
        return Collections.unmodifiableList(diagnosticos);
    }

    public List<String> getTratamientos() {
        return Collections.unmodifiableList(tratamientos);
    }

    public List<String> getAlergias() {
        return Collections.unmodifiableList(alergias);
    }
}
