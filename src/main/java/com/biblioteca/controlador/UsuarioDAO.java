package com.biblioteca.controlador;

import com.biblioteca.modelo.Usuario;
import com.biblioteca.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar operaciones CRUD de Usuarios
 *
 * Esta clase maneja todas las operaciones de base de datos relacionadas
 * con usuarios, incluyendo autenticación y gestión de roles.
 *
 * @author Biblioteca Inteligente Team
 * @version 1.0
 * @since 2025
 */
public class UsuarioDAO {

    private DatabaseConnection dbConnection;

    /**
     * Constructor que inicializa la conexión a la base de datos
     */
    public UsuarioDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Inserta un nuevo usuario en la base de datos
     *
     * @param usuario Objeto Usuario a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellido, tipo, email, telefono, direccion, " +
                "fecha_registro, username, password, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getTipo().name());
            pstmt.setString(4, usuario.getEmail());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setString(6, usuario.getDireccion());
            pstmt.setDate(7, Date.valueOf(usuario.getFechaRegistro()));
            pstmt.setString(8, usuario.getUsername());
            pstmt.setString(9, usuario.getPassword()); // En producción debe estar encriptada
            pstmt.setBoolean(10, usuario.isActivo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
                System.out.println("✓ Usuario insertado: " + usuario.getUsername());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza los datos de un usuario existente
     *
     * @param usuario Objeto Usuario con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, tipo = ?, email = ?, " +
                "telefono = ?, direccion = ?, username = ?, password = ?, activo = ? WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getTipo().name());
            pstmt.setString(4, usuario.getEmail());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setString(6, usuario.getDireccion());
            pstmt.setString(7, usuario.getUsername());
            pstmt.setString(8, usuario.getPassword());
            pstmt.setBoolean(9, usuario.isActivo());
            pstmt.setInt(10, usuario.getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✓ Usuario actualizado: " + usuario.getUsername());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un usuario de la base de datos
     *
     * @param id ID del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✓ Usuario eliminado con ID: " + id);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca un usuario por su ID
     *
     * @param id ID del usuario a buscar
     * @return Objeto Usuario si se encuentra, null en caso contrario
     */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al buscar usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios de la base de datos
     *
     * @return Lista de todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre, apellido";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Autentica un usuario por username y password
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Usuario autenticado o null si las credenciales son inválidas
     */
    public Usuario autenticar(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ? AND activo = TRUE";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // En producción comparar con hash
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("✓ Usuario autenticado: " + username);
                return mapearUsuario(rs);
            } else {
                System.out.println("✗ Credenciales inválidas para: " + username);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca usuarios por nombre o apellido (búsqueda parcial)
     *
     * @param texto Texto a buscar en nombre o apellido
     * @return Lista de usuarios que coinciden
     */
    public List<Usuario> buscarPorNombre(String texto) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE nombre LIKE ? OR apellido LIKE ? ORDER BY nombre, apellido";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String patron = "%" + texto + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al buscar por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Obtiene usuarios por tipo/rol
     *
     * @param tipo Tipo de usuario a buscar
     * @return Lista de usuarios del tipo especificado
     */
    public List<Usuario> obtenerPorTipo(Usuario.TipoUsuario tipo) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE tipo = ? ORDER BY nombre, apellido";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipo.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al obtener usuarios por tipo: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Verifica si existe un username en la base de datos
     *
     * @param username Username a verificar
     * @param idExcluir ID del usuario a excluir de la verificación
     * @return true si el username ya existe, false en caso contrario
     */
    public boolean existeUsername(String username, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ? AND id != ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, idExcluir);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al verificar username: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si existe un email en la base de datos
     *
     * @param email Email a verificar
     * @param idExcluir ID del usuario a excluir de la verificación
     * @return true si el email ya existe, false en caso contrario
     */
    public boolean existeEmail(String email, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ? AND id != ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setInt(2, idExcluir);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al verificar email: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cambia el estado activo de un usuario
     *
     * @param id ID del usuario
     * @param activo Nuevo estado
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean cambiarEstado(int id, boolean activo) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";

        try (Connection conn = dbConnection.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, activo);
            pstmt.setInt(2, id);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✓ Estado de usuario actualizado");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al cambiar estado: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene la cantidad total de usuarios activos
     *
     * @return Número total de usuarios activos
     */
    public int contarUsuariosActivos() {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE activo = TRUE";

        try (Connection conn = dbConnection.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("✗ Error al contar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Mapea un ResultSet a un objeto Usuario
     *
     * @param rs ResultSet con los datos del usuario
     * @return Objeto Usuario mapeado
     * @throws SQLException Si hay error al leer los datos
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                Usuario.TipoUsuario.valueOf(rs.getString("tipo")),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getString("direccion"),
                rs.getDate("fecha_registro").toLocalDate(),
                rs.getString("username"),
                rs.getString("password"),
                rs.getBoolean("activo")
        );
    }
}