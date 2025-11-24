package com.biblioteca.modelo;

import java.util.Objects;

/**
 * Clase que representa un libro en el sistema de biblioteca
 *
 * Esta clase encapsula toda la información relacionada con un libro,
 * incluyendo sus datos bibliográficos y de inventario.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class Libro {

    // Atributos privados
    private int id;
    private String titulo;
    private String autor;
    private String categoria;
    private int stock;
    private int anioPublicacion;
    private String isbn;
    private String editorial;

    /**
     * Constructor vacío
     * Utilizado para crear instancias sin inicializar atributos
     */
    public Libro() {
    }

    /**
     * Constructor completo con todos los atributos
     *
     * @param id Identificador único del libro
     * @param titulo Título del libro
     * @param autor Autor del libro
     * @param categoria Categoría o género del libro
     * @param stock Cantidad disponible en inventario
     * @param anioPublicacion Año de publicación
     * @param isbn Código ISBN del libro
     * @param editorial Editorial que publicó el libro
     */
    public Libro(int id, String titulo, String autor, String categoria,
                 int stock, int anioPublicacion, String isbn, String editorial) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.stock = stock;
        this.anioPublicacion = anioPublicacion;
        this.isbn = isbn;
        this.editorial = editorial;
    }

    /**
     * Constructor sin ID (para inserción en base de datos)
     * El ID se genera automáticamente en la base de datos
     *
     * @param titulo Título del libro
     * @param autor Autor del libro
     * @param categoria Categoría o género del libro
     * @param stock Cantidad disponible en inventario
     * @param anioPublicacion Año de publicación
     * @param isbn Código ISBN del libro
     * @param editorial Editorial que publicó el libro
     */
    public Libro(String titulo, String autor, String categoria,
                 int stock, int anioPublicacion, String isbn, String editorial) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.stock = stock;
        this.anioPublicacion = anioPublicacion;
        this.isbn = isbn;
        this.editorial = editorial;
    }

    // ==================== GETTERS ====================

    /**
     * Obtiene el ID del libro
     * @return ID del libro
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el título del libro
     * @return Título del libro
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Obtiene el autor del libro
     * @return Autor del libro
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Obtiene la categoría del libro
     * @return Categoría del libro
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Obtiene el stock disponible del libro
     * @return Cantidad en stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * Obtiene el año de publicación del libro
     * @return Año de publicación
     */
    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    /**
     * Obtiene el código ISBN del libro
     * @return ISBN del libro
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Obtiene la editorial del libro
     * @return Editorial del libro
     */
    public String getEditorial() {
        return editorial;
    }

    // ==================== SETTERS ====================

    /**
     * Establece el ID del libro
     * @param id ID del libro
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Establece el título del libro
     * @param titulo Título del libro
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Establece el autor del libro
     * @param autor Autor del libro
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * Establece la categoría del libro
     * @param categoria Categoría del libro
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Establece el stock del libro
     * @param stock Cantidad en stock
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Establece el año de publicación
     * @param anioPublicacion Año de publicación
     */
    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    /**
     * Establece el ISBN del libro
     * @param isbn Código ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Establece la editorial del libro
     * @param editorial Editorial del libro
     */
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Verifica si el libro está disponible para préstamo
     * @return true si hay stock disponible, false en caso contrario
     */
    public boolean estaDisponible() {
        return this.stock > 0;
    }

    /**
     * Incrementa el stock del libro en una unidad
     * Usado cuando se devuelve un libro
     */
    public void incrementarStock() {
        this.stock++;
    }

    /**
     * Decrementa el stock del libro en una unidad
     * Usado cuando se presta un libro
     * @return true si se pudo decrementar, false si no hay stock
     */
    public boolean decrementarStock() {
        if (this.stock > 0) {
            this.stock--;
            return true;
        }
        return false;
    }

    /**
     * Genera una representación en String del objeto Libro
     * @return String con la información del libro
     */
    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", stock=" + stock +
                ", anioPublicacion=" + anioPublicacion +
                ", isbn='" + isbn + '\'' +
                ", editorial='" + editorial + '\'' +
                '}';
    }

    /**
     * Compara este libro con otro objeto
     * @param o Objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return id == libro.id && Objects.equals(isbn, libro.isbn);
    }

    /**
     * Genera el código hash del libro
     * @return Código hash basado en id e isbn
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, isbn);
    }
}