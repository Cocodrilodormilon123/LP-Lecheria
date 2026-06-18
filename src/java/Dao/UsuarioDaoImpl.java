package Dao;

import Interface.IUsuario;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionSingleton;
import java.sql.*;

public class UsuarioDaoImpl implements IUsuario {

    private Connection cn;

    @Override
    public Usuario validate(String user, String passw) {
        Usuario usuarioLogueado = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            cn = ConexionSingleton.getConnection();
            Usuario temp = new Usuario();
            String hashedPassword = temp.HashPassword(passw);

            String query = "SELECT u.id_usuario, u.usuario, u.rol, p.id_persona, p.nombre, p.apellido, p.dni, p.email " +
                           "FROM usuarios u INNER JOIN persona p ON u.id_persona = p.id_persona " +
                           "WHERE u.usuario = ? AND u.password = ?";

            st = cn.prepareStatement(query);
            st.setString(1, user);
            st.setString(2, hashedPassword);

            rs = st.executeQuery();

            if (rs.next()) {
                usuarioLogueado = new Usuario();
                usuarioLogueado.setId_usuario(rs.getInt("id_usuario"));
                usuarioLogueado.setUsuario(rs.getString("usuario"));
                usuarioLogueado.setRol(Rol.valueOf(rs.getString("rol")));

                Persona personaInfo = new Persona();
                personaInfo.setId_persona(rs.getInt("id_persona"));
                personaInfo.setNombre(rs.getString("nombre"));
                personaInfo.setApellido(rs.getString("apellido"));
                personaInfo.setDni(rs.getString("dni"));
                personaInfo.setEmail(rs.getString("email"));

                usuarioLogueado.setPersona(personaInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
            }
        }
        return usuarioLogueado;
    }

    @Override
    public String listarClientesAdmin() {
        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT p.id_persona, p.dni, p.nombre, p.apellido, p.email, p.telefono, " +
                     "COUNT(ped.id_pedido) as cantidad_pedidos, COALESCE(SUM(ped.total), 0) as total_consumido " +
                     "FROM persona p INNER JOIN usuarios u ON p.id_persona = u.id_persona " +
                     "LEFT JOIN pedidos ped ON p.id_persona = ped.id_persona " +
                     "WHERE u.rol = 'CLIENTE' " +
                     "GROUP BY p.id_persona, p.dni, p.nombre, p.apellido, p.email, p.telefono " +
                     "ORDER BY p.id_persona DESC";
        try {
            cn = ConexionSingleton.getConnection();
            PreparedStatement st = cn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            boolean first = true;
            
            while(rs.next()){
                if(!first) json.append(",");
                json.append("{")
                    .append("\"id_persona\":").append(rs.getInt("id_persona")).append(",")
                    .append("\"dni\":\"").append(rs.getString("dni")).append("\",")
                    .append("\"nombre\":\"").append(rs.getString("nombre")).append("\",")
                    .append("\"apellido\":\"").append(rs.getString("apellido")).append("\",")
                    .append("\"email\":\"").append(rs.getString("email")).append("\",")
                    .append("\"telefono\":\"").append(rs.getString("telefono") != null ? rs.getString("telefono") : "N/A").append("\",")
                    .append("\"cantidad_pedidos\":").append(rs.getInt("cantidad_pedidos")).append(",")
                    .append("\"total_consumido\":").append(rs.getDouble("total_consumido"))
                    .append("}");
                first = false;
            }
            rs.close(); st.close();
        } catch(Exception e) { e.printStackTrace(); }
        json.append("]");
        return json.toString();
    }

    @Override
    public String obtenerHistorialCliente(int idPersona) {
        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT id_pedido, fecha_pedido, tipo_entrega, total, estado FROM pedidos WHERE id_persona = ? ORDER BY id_pedido DESC";
        try {
            cn = ConexionSingleton.getConnection();
            PreparedStatement st = cn.prepareStatement(sql);
            st.setInt(1, idPersona);
            ResultSet rs = st.executeQuery();
            boolean first = true;
            
            while(rs.next()){
                if(!first) json.append(",");
                json.append("{")
                    .append("\"id_pedido\":").append(rs.getInt("id_pedido")).append(",")
                    .append("\"fecha\":\"").append(rs.getTimestamp("fecha_pedido").toString()).append("\",")
                    .append("\"tipo_entrega\":\"").append(rs.getString("tipo_entrega")).append("\",")
                    .append("\"total\":").append(rs.getDouble("total")).append(",")
                    .append("\"estado\":\"").append(rs.getString("estado").replace("_", " "))
                    .append("\"}");
                first = false;
            }
            rs.close(); st.close();
        } catch(Exception e) { e.printStackTrace(); }
        json.append("]");
        return json.toString();
    }
}