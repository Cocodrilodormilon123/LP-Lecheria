package Dao;

import Interface.IPersona;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import java.util.List;
import java.sql.*;
import Util.ConexionSingleton;

public class PersonaDaoImpl implements IPersona {

    private Connection cn;

    @Override
    public List<Persona> lista() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int insert(Persona p, Usuario u) {
        PreparedStatement st = null;
        ResultSet rs = null;
        int id_persona = 0;
        int r = 0;

        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false);

            String query1 = "INSERT INTO persona(nombre, apellido, dni, email, direccion, telefono) VALUES (?, ?, ?, ?, ?, ?)";
            st = cn.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, p.getNombre());
            st.setString(2, p.getApellido());
            st.setString(3, p.getDni());
            st.setString(4, p.getEmail());
            st.setString(5, p.getDireccion());
            st.setString(6, p.getTelefono());

            r = st.executeUpdate();

            if (r != 0) {
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    id_persona = rs.getInt(1);
                }

                if (id_persona > 0) {
                    u.setRol(Rol.CLIENTE);
                    String hashedPassword = u.HashPassword(u.getPassword());

                    String query2 = "INSERT INTO usuarios(usuario, password, rol, id_persona) VALUES (?, ?, ?, ?)";
                    st = cn.prepareStatement(query2);
                    st.setString(1, p.getEmail());
                    st.setString(2, hashedPassword);
                    st.setString(3, u.getRol().name());
                    st.setInt(4, id_persona);

                    r = st.executeUpdate();
                    cn.commit();
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                r = -2;
            } else {
                r = 0;
            }
            try {
                if (cn != null) cn.rollback();
            } catch (SQLException ex) {}
        } catch (Exception e) {
            r = 0;
            try {
                if (cn != null) cn.rollback();
            } catch (SQLException ex) {}
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {}
        }
        return r;
    }

    @Override
    public boolean update(Persona p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Persona SearchById(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(int id) {
        throw new UnsupportedOperationException();
    }
}