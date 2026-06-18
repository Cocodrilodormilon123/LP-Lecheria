package Dao;

import Interface.ICategoria;
import Model.Categoria;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDaoImpl implements ICategoria {

    private Connection cn;

    @Override
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "SELECT * FROM categorias";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId_categoria(rs.getInt("id_categoria"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("descripcion"));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {}
        }
        return lista;
    }

    @Override
    public Categoria buscarPorId(int id) {
        Categoria c = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "SELECT * FROM categorias WHERE id_categoria = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                c = new Categoria();
                c.setId_categoria(rs.getInt("id_categoria"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("descripcion"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {}
        }
        return c;
    }

    @Override
    public boolean registrar(Categoria c) {
        boolean exito = false;
        PreparedStatement st = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "INSERT INTO categorias(nombre, descripcion) VALUES (?, ?)";
            st = cn.prepareStatement(query);
            st.setString(1, c.getNombre());
            st.setString(2, c.getDescripcion());
            if (st.executeUpdate() > 0) exito = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (st != null) st.close(); } catch (SQLException e) {}
        }
        return exito;
    }

    @Override
    public boolean actualizar(Categoria c) {
        boolean exito = false;
        PreparedStatement st = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "UPDATE categorias SET nombre=?, descripcion=? WHERE id_categoria=?";
            st = cn.prepareStatement(query);
            st.setString(1, c.getNombre());
            st.setString(2, c.getDescripcion());
            st.setInt(3, c.getId_categoria());
            if (st.executeUpdate() > 0) exito = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (st != null) st.close(); } catch (SQLException e) {}
        }
        return exito;
    }

    @Override
    public boolean eliminar(int id) {
        boolean exito = false;
        PreparedStatement st = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "DELETE FROM categorias WHERE id_categoria=?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            if (st.executeUpdate() > 0) exito = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (st != null) st.close(); } catch (SQLException e) {}
        }
        return exito;
    }
}