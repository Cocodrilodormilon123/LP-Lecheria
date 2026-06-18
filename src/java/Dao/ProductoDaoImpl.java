package Dao;

import Interface.IProducto;
import Model.Categoria;
import Model.Productos;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDaoImpl implements IProducto {

    private Connection cn;

    @Override
    public List<Productos> listarCatalogo() {
        List<Productos> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "SELECT p.*, c.nombre AS nombre_cat, c.descripcion AS desc_cat " +
                           "FROM productos p INNER JOIN categorias c ON p.id_categoria = c.id_categoria " +
                           "WHERE p.stock > 0";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId_categoria(rs.getInt("id_categoria"));
                c.setNombre(rs.getString("nombre_cat"));
                c.setDescripcion(rs.getString("desc_cat"));

                Productos p = new Productos();
                p.setId_producto(rs.getInt("id_producto"));
                p.setCategoria(c);
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setImagen(rs.getString("imagen"));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (st != null) st.close(); } catch (SQLException e) {}
        }
        return lista;
    }

    @Override
    public List<Productos> listarAdmin() {
        List<Productos> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "SELECT p.*, c.nombre AS nombre_cat, c.descripcion AS desc_cat " +
                           "FROM productos p INNER JOIN categorias c ON p.id_categoria = c.id_categoria " +
                           "ORDER BY p.id_producto DESC";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId_categoria(rs.getInt("id_categoria"));
                c.setNombre(rs.getString("nombre_cat"));
                c.setDescripcion(rs.getString("desc_cat"));

                Productos p = new Productos();
                p.setId_producto(rs.getInt("id_producto"));
                p.setCategoria(c);
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setImagen(rs.getString("imagen"));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (st != null) st.close(); } catch (SQLException e) {}
        }
        return lista;
    }

    @Override
    public Productos buscarPorId(int id) {
        Productos p = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "SELECT p.*, c.nombre AS nombre_cat, c.descripcion AS desc_cat " +
                           "FROM productos p INNER JOIN categorias c ON p.id_categoria = c.id_categoria " +
                           "WHERE p.id_producto = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                Categoria c = new Categoria();
                c.setId_categoria(rs.getInt("id_categoria"));
                c.setNombre(rs.getString("nombre_cat"));
                c.setDescripcion(rs.getString("desc_cat"));

                p = new Productos();
                p.setId_producto(rs.getInt("id_producto"));
                p.setCategoria(c);
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setImagen(rs.getString("imagen"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (st != null) st.close(); } catch (SQLException e) {}
        }
        return p;
    }

    @Override
    public boolean registrar(Productos p) {
        boolean exito = false;
        PreparedStatement st = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "INSERT INTO productos(id_categoria, nombre, descripcion, precio, stock, imagen) VALUES (?, ?, ?, ?, ?, ?)";
            st = cn.prepareStatement(query);
            st.setInt(1, p.getCategoria().getId_categoria());
            st.setString(2, p.getNombre());
            st.setString(3, p.getDescripcion());
            st.setDouble(4, p.getPrecio());
            st.setInt(5, p.getStock());
            st.setString(6, p.getImagen());
            if (st.executeUpdate() > 0) exito = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (st != null) st.close(); } catch (SQLException e) {}
        }
        return exito;
    }

    @Override
    public boolean actualizar(Productos p) {
        boolean exito = false;
        PreparedStatement st = null;
        try {
            cn = ConexionSingleton.getConnection();
            String query = "UPDATE productos SET id_categoria=?, nombre=?, descripcion=?, precio=?, stock=?, imagen=? WHERE id_producto=?";
            st = cn.prepareStatement(query);
            st.setInt(1, p.getCategoria().getId_categoria());
            st.setString(2, p.getNombre());
            st.setString(3, p.getDescripcion());
            st.setDouble(4, p.getPrecio());
            st.setInt(5, p.getStock());
            st.setString(6, p.getImagen());
            st.setInt(7, p.getId_producto());
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
            String query = "DELETE FROM productos WHERE id_producto=?";
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