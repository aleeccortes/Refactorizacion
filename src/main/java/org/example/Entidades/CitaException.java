// src/main/java/org/example/Entidades/CitaException.java
package org.example.Entidades;

public class CitaException extends Exception {
    public CitaException(String msg){ super(msg); }
    public CitaException(String msg, Throwable cause){ super(msg, cause); }
}
