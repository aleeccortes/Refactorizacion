🏥 **Sistema de Gestión Hospitalaria**

Proyecto en **Java 21** que simula la gestión integral de un hospital: médicos, pacientes, departamentos, salas y citas médicas.  
El código fue **refactorizado** para incorporar **Lombok**, reduciendo la necesidad de escribir manualmente métodos *getters*, *setters* y *toString()*.

---

### 📋 **Funcionalidades principales**

- Registro de hospitales, departamentos, médicos y pacientes.  
- Generación automática de historia clínica para cada paciente.  
- Programación de citas médicas validando:
  - Disponibilidad del médico.  
  - Disponibilidad de la sala.  
  - Coincidencia de especialidad con el departamento.  
- Guardado y carga de citas en formato **CSV**.  
- Persistencia de entidades mediante **JPA** y **base de datos H2** embebida.

---

### 🛠️ **Tecnologías utilizadas**

- ☕ **Java 21**  
- 🧩 **JPA (Jakarta Persistence API)**  
- 🗃️ **Base de datos H2 (modo embebido)**  
- 🪶 **Lombok**  
- 🧱 **Gradle**  
- 💻 **IntelliJ IDEA**

---

### 💾 **Configuración de persistencia**

El proyecto utiliza un archivo `persistence.xml` configurado para conectar con la base de datos **H2** local:  
- Creación automática de tablas mediante Hibernate.  
- Soporte para transacciones con `EntityManagerFactory`.  
- Datos almacenados temporalmente (modo archivo o memoria).

---

### 📥 **Clonar y ejecutar el proyecto**

1. Abrí la terminal de IntelliJ IDEA en el directorio deseado.  
2. Ejecutá el siguiente comando para clonar el repositorio:
   ```bash
   git clone https://github.com/aleeccortes/Refactorizacion.git

