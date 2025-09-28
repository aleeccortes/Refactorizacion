package org.example.Entidades;


import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Paciente extends Persona implements Serializable {

    // 🔥 IMPORTANTE: ya NO usamos @ToString.Include para historiaClinica
    // para evitar ciclo infinito con HistoriaClinica.toString()
    private final HistoriaClinica historiaClinica;

    @ToString.Include
    private final String telefono;

    @ToString.Include
    private final String direccion;

    private Hospital hospital;
    private final List<Cita> citas = new ArrayList<>();

    public Paciente(String nombre, String apellido, String dni, LocalDate fechaNacimiento,
                    TipoSangre tipoSangre, String telefono, String direccion) {
        super(nombre, apellido, dni, fechaNacimiento, tipoSangre);
        this.telefono = validarString(telefono, "El teléfono no puede ser nulo ni vacío");
        this.direccion = validarString(direccion, "La dirección no puede ser nula ni vacía");
        this.historiaClinica = new HistoriaClinica(this);
    }

    public void setHospital(Hospital hospital) {
        if (this.hospital != hospital) {
            if (this.hospital != null) {
                this.hospital.getInternalPacientes().remove(this);
            }
            this.hospital = hospital;
            if (hospital != null) {
                hospital.getInternalPacientes().add(this);
            }
        }
    }

    public void addCita(Cita cita) {
        this.citas.add(cita);
    }

    public List<Cita> getCitas() {
        return Collections.unmodifiableList(new ArrayList<>(citas));
    }

    private String validarString(String valor, String mensajeError) {
        Objects.requireNonNull(valor, mensajeError);
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor;
    }
}
