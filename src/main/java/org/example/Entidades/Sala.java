// src/main/java/org/example/Entidades/Sala.java
package org.example.Entidades;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sala implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=20, unique=true)
    private String numero;

    @Column(nullable=false, length=50)
    private String tipo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="departamento_id", nullable=false)
    private Departamento departamento;

    @OneToMany(mappedBy="sala", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default
    private List<Cita> citas = new ArrayList<>();

    public Sala(String numero, String tipo, Departamento departamento){
        this.numero = validarString(numero, "El número de sala no puede ser nulo ni vacío");
        this.tipo = validarString(tipo, "El tipo de sala no puede ser nulo ni vacío");
        this.departamento = Objects.requireNonNull(departamento, "El departamento no puede ser nulo");
    }

    public void addCita(Cita c){
        if (c!=null && !citas.contains(c)){
            citas.add(c);
            c.setSala(this);
        }
    }
    public List<Cita> getCitas(){ return Collections.unmodifiableList(citas); }

    private String validarString(String v, String msg){
        Objects.requireNonNull(v, msg);
        if (v.trim().isEmpty()) throw new IllegalArgumentException(msg);
        return v;
    }
}
