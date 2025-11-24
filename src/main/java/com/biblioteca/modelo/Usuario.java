package com.biblioteca.modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase que representa un usuario del sistema de biblioteca
 *
 * Esta clase encapsula la información de los usuarios, incluyendo
 * sus datos personales, rol en el sistema y credenciales de acceso.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class Usuario {

    // Atributos privados
    private int id;
    private String nombre;
    private String apellido;
    private TipoUsuario tipo;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaRegistro;
    private String username;
    private String password;
    private boolean activo;

    /**
     * Enumeración que define los tipos de usuario del sistema
     */
    public enum TipoUsuario {
        ADMINISTRADOR("Administrador"),
        BIBLIOTECARIO("Bibliotecario"),
        LECTOR("Lector");

        private final String descripcion;

        TipoUsuario(String descripcion) {
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
    public Usuario() {
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
    }

    /**
     * Constructor completo con todos los atributos
     *
     * @param id Identificador único del usuario
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param tipo Tipo de usuario (rol)
     * @param email Correo electrónico
     * @param telefono Número de teléfono
     * @param direccion Dirección física
     * @param fechaRegistro Fecha de registro en el sistema
     * @param username Nombre de usuario para login
     * @param password Contraseña (debe estar encriptada)
     * @param activo Estado del usuario en el sistema
     */
    public Usuario(int id, String nombre, String apellido, TipoUsuario tipo,
                   String email, String telefono, String direccion,
                   LocalDate fechaRegistro, String username, String password, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipo = tipo;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
        this.username = username;
        this.password = password;
        this.activo = activo;
    }

    /**
     * Constructor sin ID (para inserción en base de datos)
     *
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param tipo Tipo de usuario (rol)
     * @param email Correo electrónico
     * @param telefono Número de teléfono
     * @param direccion Dirección física
     * @param username Nombre de usuario para login
     * @param password Contraseña
     */
    public Usuario(String nombre, String apellido, TipoUsuario tipo,
                   String email, String telefono, String direccion,
                   String username, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipo = tipo;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = LocalDate.now();
        this.username = username;
        this.password = password;
        this.activo = true;
    }

    // ==================== GETTERS ====================

    /**
     * Obtiene el ID del usuario
     * @return ID del usuario
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el nombre del usuario
     * @return Nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el apellido del usuario
     * @return Apellido del usuario
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Obtiene el tipo/rol del usuario
     * @return Tipo de usuario
     */
    public TipoUsuario getTipo() {
        return tipo;
    }

    /**
     * Obtiene el email del usuario
     * @return Email del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obtiene el teléfono del usuario
     * @return Teléfono del usuario
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Obtiene la dirección del usuario
     * @return Dirección del usuario
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Obtiene la fecha de registro del usuario
     * @return Fecha de registro
     */
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Obtiene el username del usuario
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtiene la contraseña del usuario
     * @return Password (encriptada)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Verifica si el usuario está activo
     * @return true si está activo, false en caso contrario
     */
    public boolean isActivo() {
        return activo;
    }

    // ==================== SETTERS ====================

    /**
     * Establece el ID del usuario
     * @param id ID del usuario
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Establece el nombre del usuario
     * @param nombre Nombre del usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Establece el apellido del usuario
     * @param apellido Apellido del usuario
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Establece el tipo/rol del usuario
     * @param tipo Tipo de usuario
     */
    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    /**
     * Establece el email del usuario
     * @param email Email del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Establece el teléfono del usuario
     * @param telefono Teléfono del usuario
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Establece la dirección del usuario
     * @param direccion Dirección del usuario
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Establece la fecha de registro
     * @param fechaRegistro Fecha de registro
     */
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Establece el username del usuario
     * @param username Username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Establece la contraseña del usuario
     * @param password Password (debe encriptarse antes)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Establece el estado activo del usuario
     * @param activo Estado del usuario
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Obtiene el nombre completo del usuario
     * @return Nombre completo (nombre + apellido)
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Verifica si el usuario tiene permisos de administrador
     * @return true si es administrador, false en caso contrario
     */
    public boolean esAdministrador() {
        return this.tipo == TipoUsuario.ADMINISTRADOR;
    }

    /**
     * Verifica si el usuario tiene permisos de bibliotecario
     * @return true si es bibliotecario o administrador, false en caso contrario
     */
    public boolean esBibliotecario() {
        return this.tipo == TipoUsuario.BIBLIOTECARIO || this.tipo == TipoUsuario.ADMINISTRADOR;
    }

    /**
     * Verifica si el usuario es un lector
     * @return true si es lector, false en caso contrario
     */
    public boolean esLector() {
        return this.tipo == TipoUsuario.LECTOR;
    }

    /**
     * Genera una representación en String del objeto Usuario
     * @return String con la información del usuario (sin password)
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tipo=" + tipo +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", username='" + username + '\'' +
                ", activo=" + activo +
                '}';
    }

    /**
     * Compara este usuario con otro objeto
     * @param o Objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id && Objects.equals(username, usuario.username);
    }

    /**
     * Genera el código hash del usuario
     * @return Código hash basado en id y username
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}