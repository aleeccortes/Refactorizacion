// src/main/java/org/example/Main.java
package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.Entidades.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HospitalJPA");
        try {
            withEm(emf, em -> {
                limpiar(em);
                try {
                    cargarDatosMinimosRubrica(em);
                } catch (CitaException e) {
                    throw new RuntimeException(e);
                }
                consultasRubrica(em);
            });
        } finally {
            emf.close();
        }
    }

    private static void cargarDatosMinimosRubrica(EntityManager em) throws CitaException {
        em.getTransaction().begin();

        Hospital hosp = Hospital.builder()
                .nombre("Hospital Central")
                .direccion("Av. Siempreviva 742")
                .telefono("011-5555-5555")
                .build();
        em.persist(hosp);

        Departamento cardio = Departamento.builder().nombre("Cardiología").especialidad(EspecialidadMedica.CARDIOLOGIA).build();
        Departamento pedia  = Departamento.builder().nombre("Pediatría").especialidad(EspecialidadMedica.PEDIATRIA).build();
        Departamento trau   = Departamento.builder().nombre("Traumatología").especialidad(EspecialidadMedica.TRAUMATOLOGIA).build();
        hosp.agregarDepartamento(cardio); em.persist(cardio);
        hosp.agregarDepartamento(pedia);  em.persist(pedia);
        hosp.agregarDepartamento(trau);   em.persist(trau);

        Sala s1 = cardio.crearSala("CARD-101","Consultorio");
        Sala s2 = pedia .crearSala("PED-201","Consultorio");
        Sala s3 = trau  .crearSala("TRAU-301","Quirófano");
        em.persist(s1); em.persist(s2); em.persist(s3);

        Medico m1 = Medico.builder()
                .nombre("Ana").apellido("García").dni("12345678")
                .fechaNacimiento(LocalDate.of(1980,3,10)).tipoSangre(TipoSangre.O_POSITIVO)
                .matricula(new Matricula("MP-12345"))
                .especialidad(EspecialidadMedica.CARDIOLOGIA)
                .build();
        cardio.agregarMedico(m1); em.persist(m1);

        Medico m2 = Medico.builder()
                .nombre("Bruno").apellido("Paz").dni("23456789")
                .fechaNacimiento(LocalDate.of(1985,6,20)).tipoSangre(TipoSangre.A_NEGATIVO)
                .matricula(new Matricula("MP-23456"))
                .especialidad(EspecialidadMedica.PEDIATRIA)
                .build();
        pedia.agregarMedico(m2); em.persist(m2);

        Medico m3 = Medico.builder()
                .nombre("Carla").apellido("Lopez").dni("34567890")
                .fechaNacimiento(LocalDate.of(1978,11,5)).tipoSangre(TipoSangre.B_POSITIVO)
                .matricula(new Matricula("MP-34567"))
                .especialidad(EspecialidadMedica.TRAUMATOLOGIA)
                .build();
        trau.agregarMedico(m3); em.persist(m3);

        Paciente p1 = Paciente.builder()
                .nombre("Mario").apellido("Tomasso").dni("87654321")
                .fechaNacimiento(LocalDate.of(1990,11,20)).tipoSangre(TipoSangre.A_POSITIVO)
                .telefono("011-1111-1111").direccion("Calle Falsa 123")
                .build();
        hosp.agregarPaciente(p1); em.persist(p1);
        p1.getHistoriaClinica().agregarDiagnostico("HTA leve");

        Paciente p2 = Paciente.builder()
                .nombre("Lucía").apellido("Pérez").dni("76543210")
                .fechaNacimiento(LocalDate.of(1995,3,14)).tipoSangre(TipoSangre.O_NEGATIVO)
                .telefono("011-2222-2222").direccion("Av. Mitre 456")
                .build();
        hosp.agregarPaciente(p2); em.persist(p2);
        p2.getHistoriaClinica().agregarDiagnostico("Asma leve");

        Paciente p3 = Paciente.builder()
                .nombre("Jorge").apellido("Giménez").dni("65432109")
                .fechaNacimiento(LocalDate.of(1982,7,2)).tipoSangre(TipoSangre.B_POSITIVO)
                .telefono("011-3333-3333").direccion("San Martín 789")
                .build();
        hosp.agregarPaciente(p3); em.persist(p3);
        p3.getHistoriaClinica().agregarDiagnostico("Dolor lumbar crónico");

        CitaManager cm = new CitaManager();
        Cita c1 = cm.programarCita(p1, m1, s1, LocalDateTime.now().plusDays(2).withHour(9).withMinute(0),  new BigDecimal("60000.00"));
        Cita c2 = cm.programarCita(p2, m2, s2, LocalDateTime.now().plusDays(3).withHour(10).withMinute(30), new BigDecimal("45000.00"));
        Cita c3 = cm.programarCita(p3, m3, s3, LocalDateTime.now().plusDays(4).withHour(14).withMinute(0),  new BigDecimal("80000.00"));
        em.persist(c1); em.persist(c2); em.persist(c3);

        em.getTransaction().commit();
        System.out.println("✔ Datos mínimos de rúbrica cargados.");
    }

    private static void consultasRubrica(EntityManager em){
        System.out.println("\n--- CONSULTAS JPQL ---");

        System.out.println("\nHOSPITALES:");
        em.createQuery("SELECT h FROM Hospital h", Hospital.class)
                .getResultList()
                .forEach(h -> System.out.println("- " + h.getNombre() + " (" + h.getDireccion() + ")"));

        System.out.println("\nDEPARTAMENTOS:");
        em.createQuery("SELECT d FROM Departamento d", Departamento.class)
                .getResultList()
                .forEach(d -> System.out.println("- " + d.getNombre() + " / " + d.getEspecialidad()));

        System.out.println("\nMÉDICOS CARDIÓLOGOS:");
        TypedQuery<Medico> qCard = em.createQuery(
                "SELECT m FROM Medico m WHERE m.especialidad = :esp", Medico.class);
        qCard.setParameter("esp", EspecialidadMedica.CARDIOLOGIA);
        qCard.getResultList().forEach(m ->
                System.out.println("- " + m.getNombreCompleto() + " / Mat.: " + m.getMatricula().getNumero()));

        System.out.println("\nPACIENTES:");
        em.createQuery("SELECT p FROM Paciente p", Paciente.class)
                .getResultList()
                .forEach(p -> System.out.println("- " + p.getNombreCompleto() +
                        " / Hospital: " + (p.getHospital()!=null?p.getHospital().getNombre():"-")));

        System.out.println("\nCITAS (ORDER BY fecha):");
        em.createQuery("SELECT c FROM Cita c ORDER BY c.fechaHora", Cita.class)
                .getResultList()
                .forEach(c -> System.out.println("- " + c));

        em.getTransaction().begin();
        Cita primera = em.createQuery("SELECT c FROM Cita c ORDER BY c.id", Cita.class)
                .setMaxResults(1).getSingleResult();
        primera.setEstado(EstadoCita.COMPLETADA);
        em.merge(primera);
        em.getTransaction().commit();
        System.out.println("\n✔ Update: Cita id="+primera.getId()+" -> COMPLETADA");

        Long totalCitas = em.createQuery("SELECT COUNT(c) FROM Cita c", Long.class).getSingleResult();
        Long completadas = em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :e", Long.class)
                .setParameter("e", EstadoCita.COMPLETADA).getSingleResult();
        Long totalPac = em.createQuery("SELECT COUNT(p) FROM Paciente p", Long.class).getSingleResult();
        Long totalSal = em.createQuery("SELECT COUNT(s) FROM Sala s", Long.class).getSingleResult();

        System.out.println("\n--- ESTADÍSTICAS ---");
        System.out.println("Total de citas: " + totalCitas);
        System.out.println("Citas COMPLETADAS: " + completadas);
        System.out.println("Total de pacientes: " + totalPac);
        System.out.println("Total de salas: " + totalSal);
    }

    private static void withEm(EntityManagerFactory emf, java.util.function.Consumer<EntityManager> work){
        EntityManager em = emf.createEntityManager();
        try { work.accept(em); } finally { em.close(); }
    }
    private static void limpiar(EntityManager em){
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Cita").executeUpdate();
        em.createQuery("DELETE FROM HistoriaClinica").executeUpdate();
        em.createQuery("DELETE FROM Medico").executeUpdate();
        em.createQuery("DELETE FROM Paciente").executeUpdate();
        em.createQuery("DELETE FROM Sala").executeUpdate();
        em.createQuery("DELETE FROM Departamento").executeUpdate();
        em.createQuery("DELETE FROM Hospital").executeUpdate();
        em.getTransaction().commit();
    }
}
