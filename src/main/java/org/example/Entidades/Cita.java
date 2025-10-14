// src/main/java/org/example/Entidades/Cita.java
package org.example.Entidades;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cita implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="paciente_id", nullable=false)
    private Paciente paciente;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="medico_id", nullable=false)
    private Medico medico;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="sala_id", nullable=false)
    private Sala sala;

    @Column(nullable=false)
    private LocalDateTime fechaHora;

    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal costo;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private EstadoCita estado;

    @Column(length=500)
    private String observaciones;

    public Cita(Paciente paciente, Medico medico, Sala sala, LocalDateTime fechaHora, BigDecimal costo){
        this.paciente = Objects.requireNonNull(paciente);
        this.medico = Objects.requireNonNull(medico);
        this.sala = Objects.requireNonNull(sala);
        this.fechaHora = Objects.requireNonNull(fechaHora);
        this.costo = Objects.requireNonNull(costo);
        this.estado = EstadoCita.PROGRAMADA;
        this.observaciones = "";
    }

    public String toCsvString(){
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                paciente.getDni(), medico.getDni(), sala.getNumero(),
                fechaHora, costo, estado.name(),
                observaciones.replaceAll(",", ";"));
    }

    public static Cita fromCsvString(String csv,
                                     Map<String, Paciente> pacientes,
                                     Map<String, Medico> medicos,
                                     Map<String, Sala> salas) throws CitaException {
        String[] v = csv.split(",");
        if (v.length != 7) throw new CitaException("Formato de CSV inválido para Cita: " + csv);
        Paciente p = pacientes.get(v[0]);
        Medico m = medicos.get(v[1]);
        Sala s = salas.get(v[2]);
        if (p==null) throw new CitaException("Paciente no encontrado: " + v[0]);
        if (m==null) throw new CitaException("Médico no encontrado: " + v[1]);
        if (s==null) throw new CitaException("Sala no encontrada: " + v[2]);

        Cita c = new Cita(p, m, s, LocalDateTime.parse(v[3]), new BigDecimal(v[4]));
        c.setEstado(EstadoCita.valueOf(v[5]));
        c.setObservaciones(v[6].replaceAll(";", ","));
        return c;
    }

    @Override public String toString(){
        return "Cita{paciente=" + (paciente!=null?paciente.getNombreCompleto():"N/A") +
                ", medico=" + (medico!=null?medico.getNombreCompleto():"N/A") +
                ", sala=" + (sala!=null?sala.getNumero():"N/A") +
                ", fechaHora=" + fechaHora + ", estado=" + estado + ", costo=" + costo + "}";
    }
}
