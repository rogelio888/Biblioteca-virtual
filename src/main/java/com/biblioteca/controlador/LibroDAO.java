package com.biblioteca.controlador;

import com.biblioteca.modelo.Libro;
import com.biblioteca.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar operaciones CRUD de Libros
 *
 * Esta clase implementa el patrón DAO (Data Access Object) para
 * separar la lógica de acceso a datos de la lógica de negocio.
 * Utiliza PreparedStatement para prevenir inyección SQL.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class LibroDAO {

    private DatabaseConnection dbConnection;

    /**
     * Constructor que inicializa la conexión a la base de datos
     */
    public LibroDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Inserta un nuevo libro en la base de datos
     *
     * @param libro Objeto Libro a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, categoria, stock, anio_publicacion, isbn, editorial) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setString(3, libro.getCategoria());
            pstmt.setInt(4, libro.getStock());
            pstmt.setInt(5, libro.getAnioPublicacion());
            pstmt.setString(6, libro.getIsbn());
            pstmt.setString(7, libro.getEditorial());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    libro.setId(rs.getInt(1));
                }
                System.out.println("✓ Libro insertado: " + libro.getTitulo());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al insertar libro: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza los datos de un libro existente
     *
     * @param libro Objeto Libro con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, categoria = ?, stock = ?, " +
                "anio_publicacion = ?, isbn = ?, editorial = ? WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setString(3, libro.getCategoria());
            pstmt.setInt(4, libro.getStock());
            pstmt.setInt(5, libro.getAnioPublicacion());
            pstmt.setString(6, libro.getIsbn());
            pstmt.setString(7, libro.getEditorial());
            pstmt.setInt(8, libro.getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✓ Libro actualizado: " + libro.getTitulo());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar libro: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un libro de la base de datos
     *
     * @param id ID del libro a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✓ Libro eliminado con ID: " + id);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al eliminar libro: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca un libro por su ID
     *
     * @param id ID del libro a buscar
     * @return Objeto Libro si se encuentra, null en caso contrario
     */
    public Libro buscarPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearLibro(rs);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al buscar libro por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene todos los libros de la base de datos
     *
     * @return Lista de todos los libros
     */
    public List<Libro> obtenerTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY titulo";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener libros: " + e.getMessage());
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Busca libros por título (búsqueda parcial)
     *
     * @param titulo Título o parte del título a buscar
     * @return Lista de libros que coinciden
     */
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ? ORDER BY titulo";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + titulo + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al buscar por título: " + e.getMessage());
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Busca libros por autor (búsqueda parcial)
     *
     * @param autor Autor o parte del nombre a buscar
     * @return Lista de libros que coinciden
     */
    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE autor LIKE ? ORDER BY titulo";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + autor + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al buscar por autor: " + e.getMessage());
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Busca libros por categoría
     *
     * @param categoria Categoría a buscar
     * @return Lista de libros de la categoría
     */
    public List<Libro> buscarPorCategoria(String categoria) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE categoria = ? ORDER BY titulo";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al buscar por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Obtiene libros con stock bajo (2 o menos ejemplares)
     *
     * @return Lista de libros con stock bajo
     */
    public List<Libro> obtenerLibrosStockBajo() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE stock <= 2 ORDER BY stock, titulo";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener libros con stock bajo: " + e.getMessage());
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Obtiene todas las categorías únicas de libros
     *
     * @return Lista de categorías
     */
    public List<String> obtenerCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT categoria FROM libros ORDER BY categoria";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categorias.add(rs.getString("categoria"));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener categorías: " + e.getMessage());
            e.printStackTrace();
        }
        return categorias;
    }

    /**
     * Verifica si existe un ISBN en la base de datos
     *
     * @param isbn ISBN a verificar
     * @param idExcluir ID del libro a excluir de la verificación (para updates)
     * @return true si el ISBN ya existe, false en caso contrario
     */
    public boolean existeISBN(String isbn, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM libros WHERE isbn = ? AND id != ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            pstmt.setInt(2, idExcluir);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al verificar ISBN: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene la cantidad total de libros
     *
     * @return Número total de libros
     */
    public int contarLibros() {
        String sql = "SELECT COUNT(*) FROM libros";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al contar libros: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Mapea un ResultSet a un objeto Libro
     *
     * @param rs ResultSet con los datos del libro
     * @return Objeto Libro mapeado
     * @throws SQLException Si hay error al leer los datos
     */
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("categoria"),
                rs.getInt("stock"),
                rs.getInt("anio_publicacion"),
                rs.getString("isbn"),
                rs.getString("editorial")
        );
    }
}