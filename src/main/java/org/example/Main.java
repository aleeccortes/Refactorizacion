package org.example.Servicio;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.Entidades.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hospital-persistence-unit");

        try {
            //guardarDatos(emf);
            consultarDatos(emf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            emf.close();
        }
    }

    private static void guardarDatos(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Hospital hospital = Hospital.builder()
                    .nombre("Hospital Central")
                    .direccion("Av. Libertador 1234")
                    .telefono("011-4567-8901")
                    .build();

            Departamento cardio = new Departamento("Cardiología", EspecialidadMedica.CARDIOLOGIA);
            hospital.agregarDepartamento(cardio);

            Sala consultorio = cardio.crearSala("CARD-101", "Consultorio");

            Medico medico = new Medico(
                    "Carlos",
                    "González",
                    "12345678",
                    LocalDate.of(1975, 5, 15),
                    TipoSangre.A_POSITIVO,
                    "MP-12345",
                    EspecialidadMedica.CARDIOLOGIA
            );
            cardio.agregarMedico(medico);


            Paciente paciente = Paciente.builder()
                    .nombre("María")
                    .apellido("López")
                    .dni("11111111")
                    .fechaNacimiento(LocalDate.of(1985, 12, 5))
                    .tipoSangre(TipoSangre.A_POSITIVO)
                    .telefono("011-1111-1111")
                    .direccion("Calle Falsa 123")
                    .build();
            hospital.agregarPaciente(paciente);

            HistoriaClinica historia = paciente.getHistoriaClinica();
            historia.agregarDiagnostico("Hipertensión arterial esencial (I10)");
            historia.agregarTratamiento("Enalapril 10mg diario");
            historia.agregarAlergia("Penicilina");

            CitaManager manager = new CitaManager();
            Cita cita = manager.programarCita(
                    paciente,
                    medico,
                    consultorio,
                    LocalDateTime.now().plusDays(1).withHour(10),
                    new BigDecimal("15000.00")
            );

            paciente.addCita(cita);
            medico.addCita(cita);
            consultorio.addCita(cita);
            em.persist(hospital);
            em.getTransaction().commit();

            System.out.println("Datos guardados correctamente en la base de datos H2.");

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }

    private static void consultarDatos(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n--- CONSULTAS JPQL ---");

            System.out.println("\nHOSPITALES:");
            em.createQuery("SELECT h FROM Hospital h", Hospital.class)
                    .getResultList()
                    .forEach(h -> System.out.println("- " + h.getNombre() + " (" + h.getDireccion() + ")"));

            System.out.println("\nDEPARTAMENTOS POR HOSPITAL:");
            em.createQuery("SELECT d FROM Departamento d", Departamento.class)
                    .getResultList()
                    .forEach(d -> System.out.println("- " + d.getNombre() + " / Hospital: " + d.getHospital().getNombre()));

            System.out.println("\nMÉDICOS REGISTRADOS:");
            em.createQuery("SELECT m FROM Medico m", Medico.class)
                    .getResultList()
                    .forEach(m -> System.out.println("- " + m.getNombreCompleto() + " (" + m.getEspecialidad() + ") Matrícula: " + m.getMatricula().getNumero()));

            System.out.println("\nPACIENTES:");
            em.createQuery("SELECT p FROM Paciente p", Paciente.class)
                    .getResultList()
                    .forEach(p -> System.out.println("- " + p.getNombreCompleto() + " / Hospital: " + p.getHospital().getNombre()));

            System.out.println("\nHISTORIAS CLÍNICAS:");
            em.createQuery("SELECT h FROM HistoriaClinica h", HistoriaClinica.class)
                    .getResultList()
                    .forEach(h -> {
                        System.out.println("- " + h.getNumeroHistoria() + " de " + h.getPaciente().getNombreCompleto());
                        System.out.println("  Diagnósticos: " + h.getDiagnosticos());
                        System.out.println("  Tratamientos: " + h.getTratamientos());
                        System.out.println("  Alergias: " + h.getAlergias());
                    });

            System.out.println("\nCITAS PROGRAMADAS:");
            em.createQuery("SELECT c FROM Cita c", Cita.class)
                    .getResultList()
                    .forEach(c -> System.out.println(
                            "- Paciente: " + c.getPaciente().getNombreCompleto() +
                                    " / Médico: " + c.getMedico().getNombreCompleto() +
                                    " / Sala: " + c.getSala().getNumero() +
                                    " / Fecha: " + c.getFechaHora() +
                                    " / Costo: $" + c.getCosto() +
                                    " / Estado: " + c.getEstado()
                    ));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
