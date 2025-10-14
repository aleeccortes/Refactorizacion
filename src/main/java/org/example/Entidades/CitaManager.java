// src/main/java/org/example/Entidades/CitaManager.java
package org.example.Entidades;

import lombok.Getter;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CitaManager implements CitaService {

    private final List<Cita> citas = new ArrayList<>();
    private final Map<Paciente, List<Cita>> citasPorPaciente = new ConcurrentHashMap<>();
    private final Map<Medico, List<Cita>> citasPorMedico = new ConcurrentHashMap<>();
    private final Map<Sala, List<Cita>> citasPorSala = new ConcurrentHashMap<>();

    @Override
    public Cita programarCita(Paciente paciente, Medico medico, Sala sala,
                              LocalDateTime fechaHora, BigDecimal costo) throws CitaException {
        validarCita(fechaHora, costo);
        if (!esMedicoDisponible(medico, fechaHora)) throw new CitaException("Médico no disponible.");
        if (!esSalaDisponible(sala, fechaHora)) throw new CitaException("Sala no disponible.");
        if (!medico.getEspecialidad().equals(sala.getDepartamento().getEspecialidad()))
            throw new CitaException("Especialidad del médico no coincide con el departamento de la sala.");

        Cita c = new Cita(paciente, medico, sala, fechaHora, costo);
        citas.add(c);
        citasPorPaciente.computeIfAbsent(paciente,k->new ArrayList<>()).add(c);
        citasPorMedico.computeIfAbsent(medico,k->new ArrayList<>()).add(c);
        citasPorSala.computeIfAbsent(sala,k->new ArrayList<>()).add(c);
        paciente.addCita(c); medico.addCita(c); sala.addCita(c);
        return c;
    }

    private void validarCita(LocalDateTime fh, BigDecimal costo) throws CitaException {
        if (fh.isBefore(LocalDateTime.now())) throw new CitaException("No se puede programar en el pasado.");
        if (costo.compareTo(BigDecimal.ZERO) <= 0) throw new CitaException("Costo debe ser > 0.");
    }
    private boolean esMedicoDisponible(Medico m, LocalDateTime fh){
        List<Cita> l = citasPorMedico.get(m); if (l==null) return true;
        for (Cita c: l) if (Math.abs(c.getFechaHora().compareTo(fh)) < 2) return false;
        return true;
    }
    private boolean esSalaDisponible(Sala s, LocalDateTime fh){
        List<Cita> l = citasPorSala.get(s); if (l==null) return true;
        for (Cita c: l) if (Math.abs(c.getFechaHora().compareTo(fh)) < 2) return false;
        return true;
    }

    @Override public List<Cita> getCitasPorPaciente(Paciente p){ return citasPorPaciente.getOrDefault(p, List.of()); }
    @Override public List<Cita> getCitasPorMedico(Medico m){ return citasPorMedico.getOrDefault(m, List.of()); }
    @Override public List<Cita> getCitasPorSala(Sala s){ return citasPorSala.getOrDefault(s, List.of()); }

    @Override public void guardarCitas(String f) throws IOException {
        try(PrintWriter w = new PrintWriter(new FileWriter(f))){
            for (Cita c : citas) w.println(c.toCsvString());
        }
    }
    @Override public void cargarCitas(String f, Map<String, Paciente> pacientes,
                                      Map<String, Medico> medicos, Map<String, Sala> salas)
            throws IOException, ClassNotFoundException, CitaException {
        citas.clear(); citasPorPaciente.clear(); citasPorMedico.clear(); citasPorSala.clear();
        try(BufferedReader r = new BufferedReader(new FileReader(f))){
            String line; while((line=r.readLine())!=null){
                Cita c = Cita.fromCsvString(line, pacientes, medicos, salas);
                citas.add(c);
                citasPorPaciente.computeIfAbsent(c.getPaciente(),k->new ArrayList<>()).add(c);
                citasPorMedico.computeIfAbsent(c.getMedico(),k->new ArrayList<>()).add(c);
                citasPorSala.computeIfAbsent(c.getSala(),k->new ArrayList<>()).add(c);
            }
        }
    }
}
