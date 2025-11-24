package com.biblioteca.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Clase que representa un préstamo de libro en el sistema
 *
 * Esta clase gestiona la información de los préstamos realizados,
 * incluyendo las fechas, estado y relaciones con usuarios y libros.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class Prestamo {

    // Atributos privados
    private int id;
    private int idUsuario;
    private int idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;
    private String observaciones;

    // Atributos adicionales para visualización (no se guardan en BD)
    private String nombreUsuario;
    private String tituloLibro;

    /**
     * Enumeración que define los estados posibles de un préstamo
     */
    public enum EstadoPrestamo {
        PENDIENTE("Pendiente"),
        DEVUELTO("Devuelto"),
        RETRASADO("Retrasado"),
        RENOVADO("Renovado");

        private final String descripcion;

        EstadoPrestamo(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    /**
     * Constructor vacío
     */
    public Prestamo() {
        this.fechaPrestamo = LocalDate.now();
        this.estado = EstadoPrestamo.PENDIENTE;
    }

    /**
     * Constructor completo con todos los atributos
     *
     * @param id Identificador único del préstamo
     * @param idUsuario ID del usuario que realiza el préstamo
     * @param idLibro ID del libro prestado
     * @param fechaPrestamo Fecha en que se realizó el préstamo
     * @param fechaDevolucionEsperada Fecha esperada de devolución
     * @param fechaDevolucionReal Fecha real de devolución (null si no se ha devuelto)
     * @param estado Estado actual del préstamo
     * @param observaciones Observaciones adicionales
     */
    public Prestamo(int id, int idUsuario, int idLibro, LocalDate fechaPrestamo,
                    LocalDate fechaDevolucionEsperada, LocalDate fechaDevolucionReal,
                    EstadoPrestamo estado, String observaciones) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    /**
     * Constructor para crear un nuevo préstamo
     *
     * @param idUsuario ID del usuario
     * @param idLibro ID del libro
     * @param diasPrestamo Número de días del préstamo (por defecto 14)
     */
    public Prestamo(int idUsuario, int idLibro, int diasPrestamo) {
        this.idUsuario = idUsuario;
        this.idLibro = idLibro;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucionEsperada = LocalDate.now().plusDays(diasPrestamo);
        this.estado = EstadoPrestamo.PENDIENTE;
    }

    // ==================== GETTERS ====================

    /**
     * Obtiene el ID del préstamo
     * @return ID del préstamo
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el ID del usuario
     * @return ID del usuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Obtiene el ID del libro
     * @return ID del libro
     */
    public int getIdLibro() {
        return idLibro;
    }

    /**
     * Obtiene la fecha de préstamo
     * @return Fecha de préstamo
     */
    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    /**
     * Obtiene la fecha de devolución esperada
     * @return Fecha de devolución esperada
     */
    public LocalDate getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    /**
     * Obtiene la fecha de devolución real
     * @return Fecha de devolución real (null si no se ha devuelto)
     */
    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    /**
     * Obtiene el estado del préstamo
     * @return Estado del préstamo
     */
    public EstadoPrestamo getEstado() {
        return estado;
    }

    /**
     * Obtiene las observaciones del préstamo
     * @return Observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Obtiene el nombre del usuario (para visualización)
     * @return Nombre del usuario
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Obtiene el título del libro (para visualización)
     * @return Título del libro
     */
    public String getTituloLibro() {
        return tituloLibro;
    }

    // ==================== SETTERS ====================

    /**
     * Establece el ID del préstamo
     * @param id ID del préstamo
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Establece el ID del usuario
     * @param idUsuario ID del usuario
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Establece el ID del libro
     * @param idLibro ID del libro
     */
    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    /**
     * Establece la fecha de préstamo
     * @param fechaPrestamo Fecha de préstamo
     */
    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    /**
     * Establece la fecha de devolución esperada
     * @param fechaDevolucionEsperada Fecha de devolución esperada
     */
    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    /**
     * Establece la fecha de devolución real
     * @param fechaDevolucionReal Fecha de devolución real
     */
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    /**
     * Establece el estado del préstamo
     * @param estado Estado del préstamo
     */
    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    /**
     * Establece las observaciones del préstamo
     * @param observaciones Observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Establece el nombre del usuario (para visualización)
     * @param nombreUsuario Nombre del usuario
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Establece el título del libro (para visualización)
     * @param tituloLibro Título del libro
     */
    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Verifica si el préstamo está retrasado
     * @return true si está retrasado, false en caso contrario
     */
    public boolean estaRetrasado() {
        if (estado == EstadoPrestamo.DEVUELTO) {
            return false;
        }
        return LocalDate.now().isAfter(fechaDevolucionEsperada);
    }

    /**
     * Calcula los días de retraso del préstamo
     * @return Número de días de retraso (0 si no hay retraso)
     */
    public long getDiasRetraso() {
        if (!estaRetrasado()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(fechaDevolucionEsperada, LocalDate.now());
    }

    /**
     * Calcula los días restantes para la devolución
     * @return Número de días restantes (negativo si está retrasado)
     */
    public long getDiasRestantes() {
        if (estado == EstadoPrestamo.DEVUELTO) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaDevolucionEsperada);
    }

    /**
     * Registra la devolución del libro
     * Actualiza el estado y la fecha de devolución real
     */
    public void registrarDevolucion() {
        this.fechaDevolucionReal = LocalDate.now();
        this.estado = EstadoPrestamo.DEVUELTO;
    }

    /**
     * Renueva el préstamo por días adicionales
     * @param diasAdicionales Número de días adicionales
     */
    public void renovarPrestamo(int diasAdicionales) {
        this.fechaDevolucionEsperada = this.fechaDevolucionEsperada.plusDays(diasAdicionales);
        this.estado = EstadoPrestamo.RENOVADO;
    }

    /**
     * Actualiza el estado según las fechas
     * Debe llamarse periódicamente para mantener estados actualizados
     */
    public void actualizarEstado() {
        if (estado != EstadoPrestamo.DEVUELTO) {
            if (estaRetrasado()) {
                this.estado = EstadoPrestamo.RETRASADO;
            }
        }
    }

    /**
     * Genera una representación en String del objeto Prestamo
     * @return String con la información del préstamo
     */
    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", idLibro=" + idLibro +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucionEsperada=" + fechaDevolucionEsperada +
                ", fechaDevolucionReal=" + fechaDevolucionReal +
                ", estado=" + estado +
                ", diasRetraso=" + getDiasRetraso() +
                '}';
    }

    /**
     * Compara este préstamo con otro objeto
     * @param o Objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prestamo prestamo = (Prestamo) o;
        return id == prestamo.id;
    }

    /**
     * Genera el código hash del préstamo
     * @return Código hash basado en id
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}