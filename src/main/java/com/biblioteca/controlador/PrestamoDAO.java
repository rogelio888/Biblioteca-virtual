package com.biblioteca.controlador;

import com.biblioteca.modelo.Prestamo;
import com.biblioteca.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar operaciones CRUD de Préstamos
 *
 * Esta clase maneja todas las operaciones relacionadas con préstamos
 * de libros, incluyendo control de fechas y estados.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class PrestamoDAO {

    private DatabaseConnection dbConnection;

    /**
     * Constructor que inicializa la conexión a la base de datos
     */
    public PrestamoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Registra un nuevo préstamo en la base de datos
     * El trigger automáticamente decrementará el stock del libro
     *
     * @param prestamo Objeto Prestamo a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_prestamo, " +
                "fecha_devolucion_esperada, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, prestamo.getIdUsuario());
            pstmt.setInt(2, prestamo.getIdLibro());
            pstmt.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            pstmt.setDate(4, Date.valueOf(prestamo.getFechaDevolucionEsperada()));
            pstmt.setString(5, prestamo.getEstado().name());
            pstmt.setString(6, prestamo.getObservaciones());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    prestamo.setId(rs.getInt(1));
                }
                System.out.println("✓ Préstamo registrado con ID: " + prestamo.getId());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al insertar préstamo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza los datos de un préstamo existente
     *
     * @param prestamo Objeto Prestamo con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizar(Prestamo prestamo) {
        String sql = "UPDATE prestamos SET id_usuario = ?, id_libro = ?, fecha_prestamo = ?, " +
                "fecha_devolucion_esperada = ?, fecha_devolucion_real = ?, estado = ?, " +
                "observaciones = ? WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prestamo.getIdUsuario());
            pstmt.setInt(2, prestamo.getIdLibro());
            pstmt.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            pstmt.setDate(4, Date.valueOf(prestamo.getFechaDevolucionEsperada()));

            if (prestamo.getFechaDevolucionReal() != null) {
                pstmt.setDate(5, Date.valueOf(prestamo.getFechaDevolucionReal()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setString(6, prestamo.getEstado().name());
            pstmt.setString(7, prestamo.getObservaciones());
            pstmt.setInt(8, prestamo.getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✓ Préstamo actualizado con ID: " + prestamo.getId());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar préstamo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Registra la devolución de un libro
     * El trigger automáticamente incrementará el stock del libro
     *
     * @param idPrestamo ID del préstamo a marcar como devuelto
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean registrarDevolucion(int idPrestamo) {
        String sql = "UPDATE prestamos SET fecha_devolucion_real = ?, estado = ? WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, Prestamo.EstadoPrestamo.DEVUELTO.name());
            pstmt.setInt(3, idPrestamo);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✓ Devolución registrada para préstamo ID: " + idPrestamo);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al registrar devolución: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un préstamo de la base de datos
     *
     * @param id ID del préstamo a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✓ Préstamo eliminado con ID: " + id);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al eliminar préstamo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca un préstamo por su ID
     *
     * @param id ID del préstamo a buscar
     * @return Objeto Prestamo si se encuentra, null en caso contrario
     */
    public Prestamo buscarPorId(int id) {
        String sql = "SELECT p.*, CONCAT(u.nombre, ' ', u.apellido) as nombre_usuario, l.titulo as titulo_libro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id " +
                "INNER JOIN libros l ON p.id_libro = l.id " +
                "WHERE p.id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearPrestamo(rs);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al buscar préstamo por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene todos los préstamos de la base de datos
     *
     * @return Lista de todos los préstamos
     */
    public List<Prestamo> obtenerTodos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(u.nombre, ' ', u.apellido) as nombre_usuario, l.titulo as titulo_libro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id " +
                "INNER JOIN libros l ON p.id_libro = l.id " +
                "ORDER BY p.fecha_prestamo DESC";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener préstamos: " + e.getMessage());
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Obtiene préstamos activos (pendientes, retrasados o renovados)
     *
     * @return Lista de préstamos activos
     */
    public List<Prestamo> obtenerPrestamosActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(u.nombre, ' ', u.apellido) as nombre_usuario, l.titulo as titulo_libro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id " +
                "INNER JOIN libros l ON p.id_libro = l.id " +
                "WHERE p.estado IN ('PENDIENTE', 'RETRASADO', 'RENOVADO') " +
                "ORDER BY p.fecha_devolucion_esperada ASC";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener préstamos activos: " + e.getMessage());
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Obtiene préstamos retrasados
     *
     * @return Lista de préstamos retrasados
     */
    public List<Prestamo> obtenerPrestamosRetrasados() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(u.nombre, ' ', u.apellido) as nombre_usuario, l.titulo as titulo_libro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id " +
                "INNER JOIN libros l ON p.id_libro = l.id " +
                "WHERE p.estado = 'RETRASADO' OR " +
                "(p.estado IN ('PENDIENTE', 'RENOVADO') AND p.fecha_devolucion_esperada < CURRENT_DATE) " +
                "ORDER BY p.fecha_devolucion_esperada ASC";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener préstamos retrasados: " + e.getMessage());
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Obtiene préstamos de un usuario específico
     *
     * @param idUsuario ID del usuario
     * @return Lista de préstamos del usuario
     */
    public List<Prestamo> obtenerPorUsuario(int idUsuario) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(u.nombre, ' ', u.apellido) as nombre_usuario, l.titulo as titulo_libro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id " +
                "INNER JOIN libros l ON p.id_libro = l.id " +
                "WHERE p.id_usuario = ? " +
                "ORDER BY p.fecha_prestamo DESC";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener préstamos por usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Obtiene préstamos de un libro específico
     *
     * @param idLibro ID del libro
     * @return Lista de préstamos del libro
     */
    public List<Prestamo> obtenerPorLibro(int idLibro) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(u.nombre, ' ', u.apellido) as nombre_usuario, l.titulo as titulo_libro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id " +
                "INNER JOIN libros l ON p.id_libro = l.id " +
                "WHERE p.id_libro = ? " +
                "ORDER BY p.fecha_prestamo DESC";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idLibro);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener préstamos por libro: " + e.getMessage());
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Actualiza estados de préstamos retrasados
     * Ejecuta el procedimiento almacenado sp_actualizar_prestamos_retrasados
     *
     * @return Número de préstamos actualizados
     */
    public int actualizarPrestamosRetrasados() {
        String sql = "{CALL sp_actualizar_prestamos_retrasados()}";

        try (Connection conn = dbConnection.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                int actualizados = rs.getInt("prestamos_actualizados");
                System.out.println("✓ Préstamos retrasados actualizados: " + actualizados);
                return actualizados;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar préstamos retrasados: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Verifica si un usuario tiene préstamos pendientes
     *
     * @param idUsuario ID del usuario
     * @return true si tiene préstamos pendientes, false en caso contrario
     */
    public boolean tienePrestamosActivos(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? " +
                "AND estado IN ('PENDIENTE', 'RETRASADO', 'RENOVADO')";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al verificar préstamos activos: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cuenta préstamos por estado
     *
     * @param estado Estado a contar
     * @return Cantidad de préstamos en ese estado
     */
    public int contarPorEstado(Prestamo.EstadoPrestamo estado) {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE estado = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado.name());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al contar préstamos: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Mapea un ResultSet a un objeto Prestamo
     *
     * @param rs ResultSet con los datos del préstamo
     * @return Objeto Prestamo mapeado
     * @throws SQLException Si hay error al leer los datos
     */
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_usuario"),
                rs.getInt("id_libro"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_devolucion_esperada").toLocalDate(),
                rs.getDate("fecha_devolucion_real") != null ?
                        rs.getDate("fecha_devolucion_real").toLocalDate() : null,
                Prestamo.EstadoPrestamo.valueOf(rs.getString("estado")),
                rs.getString("observaciones")
        );

        // Agregar datos de usuario y libro para visualización
        prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
        prestamo.setTituloLibro(rs.getString("titulo_libro"));

        return prestamo;
    }
}