// src/main/java/org/example/Entidades/CitaService.java
package org.example.Entidades;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

interface CitaService {
    Cita programarCita(Paciente paciente, Medico medico, Sala sala,
                       LocalDateTime fechaHora, java.math.BigDecimal costo) throws CitaException;
    List<Cita> getCitasPorPaciente(Paciente paciente);
    List<Cita> getCitasPorMedico(Medico medico);
    List<Cita> getCitasPorSala(Sala sala);
    void guardarCitas(String filename) throws IOException;
    void cargarCitas(String filename,
                     Map<String, Paciente> pacientes,
                     Map<String, Medico> medicos,
                     Map<String, Sala> salas)
            throws IOException, ClassNotFoundException, CitaException;
}
