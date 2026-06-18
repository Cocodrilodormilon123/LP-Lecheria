package Dao;

import Interface.IPedido;
import Model.*;
import Util.ConexionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDaoImpl implements IPedido {

    private Connection cn;

    @Override
    public boolean registrarCompra(Pedidos pedido, List<DetallePedido> detalles) {
        return registrarCompraConPago(pedido, detalles, "TIENDA", null);
    }

    public boolean registrarCompraConPago(Pedidos pedido, List<DetallePedido> detalles, String metodoPago, String comprobanteImg) {
        boolean exito = false;
        PreparedStatement stPedido = null;
        PreparedStatement stDetalle = null;
        PreparedStatement stStock = null;
        PreparedStatement stPago = null;
        ResultSet rs = null;

        try {
            cn = ConexionSingleton.getConnection();
            cn.setAutoCommit(false); 

            String queryPedido = "INSERT INTO pedidos(id_persona, tipo_entrega, direccion_envio, referencia_envio, telefono_contacto, destinatario_nombre, total, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stPedido = cn.prepareStatement(queryPedido, Statement.RETURN_GENERATED_KEYS);
            stPedido.setInt(1, pedido.getPersona().getId_persona());
            stPedido.setString(2, pedido.getTipo_entrega());
            stPedido.setString(3, pedido.getDireccion_envio());
            stPedido.setString(4, pedido.getReferencia_envio());
            stPedido.setString(5, pedido.getTelefono_contacto());
            stPedido.setString(6, pedido.getDestinatario_nombre());
            stPedido.setDouble(7, pedido.getTotal());
            stPedido.setString(8, pedido.getEstado().name());
            stPedido.executeUpdate();

            rs = stPedido.getGeneratedKeys();
            int id_pedido_generado = 0;
            if (rs.next()) {
                id_pedido_generado = rs.getInt(1);
            }

            String queryDetalle = "INSERT INTO detalles_pedido(id_pedido, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            stDetalle = cn.prepareStatement(queryDetalle);

            String queryStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
            stStock = cn.prepareStatement(queryStock);

            for (DetallePedido dp : detalles) {
                stDetalle.setInt(1, id_pedido_generado);
                stDetalle.setInt(2, dp.getProducto().getId_producto());
                stDetalle.setInt(3, dp.getCantidad());
                stDetalle.setDouble(4, dp.getPrecio_unitario());
                stDetalle.setDouble(5, dp.getSubtotal());
                stDetalle.executeUpdate();

                stStock.setInt(1, dp.getCantidad());
                stStock.setInt(2, dp.getProducto().getId_producto());
                stStock.executeUpdate();
            }

            String queryPago = "INSERT INTO pagos(id_pedido, metodo_pago, monto, comprobante_img) VALUES (?, ?, ?, ?)";
            stPago = cn.prepareStatement(queryPago);
            stPago.setInt(1, id_pedido_generado);
            stPago.setString(2, metodoPago);
            stPago.setDouble(3, pedido.getTotal());
            stPago.setString(4, comprobanteImg);
            stPago.executeUpdate();

            cn.commit(); 
            exito = true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (cn != null) {
                    cn.rollback(); 
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (stPedido != null) stPedido.close();
                if (stDetalle != null) stDetalle.close();
                if (stStock != null) stStock.close();
                if (stPago != null) stPago.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exito;
    }

    @Override
    public List<Pedidos> listarMisPedidos(int id_persona) {
        List<Pedidos> lista = new ArrayList<>();
        String sql = "SELECT p.id_pedido, p.fecha_pedido, p.tipo_entrega, p.direccion_envio, p.telefono_contacto, p.destinatario_nombre, p.total, p.estado, pa.metodo_pago, pa.comprobante_img FROM pedidos p LEFT JOIN pagos pa ON p.id_pedido = pa.id_pedido WHERE p.id_persona = ? ORDER BY p.id_pedido DESC";
        
        try {
            cn = ConexionSingleton.getConnection();
            PreparedStatement st = cn.prepareStatement(sql);
            st.setInt(1, id_persona);
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                Pedidos ped = new Pedidos();
                ped.setId_pedido(rs.getInt("id_pedido"));
                ped.setFecha_pedido(rs.getTimestamp("fecha_pedido"));
                ped.setTipo_entrega(rs.getString("tipo_entrega"));
                ped.setDireccion_envio(rs.getString("direccion_envio"));
                ped.setTelefono_contacto(rs.getString("telefono_contacto"));
                ped.setDestinatario_nombre(rs.getString("destinatario_nombre"));
                ped.setTotal(rs.getDouble("total"));
                ped.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                
                Pagos pago = new Pagos();
                pago.setMetodo_pago(rs.getString("metodo_pago"));
                pago.setComprobante_img(rs.getString("comprobante_img"));
                ped.setPago(pago);
                
                List<DetallePedido> detalles = new ArrayList<>();
                String sqlDet = "SELECT dp.cantidad, dp.precio_unitario, dp.subtotal, pr.nombre FROM detalles_pedido dp INNER JOIN productos pr ON dp.id_producto = pr.id_producto WHERE dp.id_pedido = ?";
                PreparedStatement stDet = cn.prepareStatement(sqlDet);
                stDet.setInt(1, ped.getId_pedido());
                ResultSet rsDet = stDet.executeQuery();
                
                while(rsDet.next()){
                    DetallePedido det = new DetallePedido();
                    det.setCantidad(rsDet.getInt("cantidad"));
                    det.setPrecio_unitario(rsDet.getDouble("precio_unitario"));
                    det.setSubtotal(rsDet.getDouble("subtotal"));
                    
                    Productos pr = new Productos();
                    pr.setNombre(rsDet.getString("nombre"));
                    det.setProducto(pr);
                    
                    detalles.add(det);
                }
                rsDet.close();
                stDet.close();
                
                ped.setDetalles(detalles);
                lista.add(ped);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Pedidos> listarTodosLosPedidos() {
        List<Pedidos> lista = new ArrayList<>();
        String sql = "SELECT p.id_pedido, p.fecha_pedido, p.tipo_entrega, p.direccion_envio, p.telefono_contacto, p.destinatario_nombre, p.total, p.estado, per.nombre, per.apellido, pa.metodo_pago, pa.comprobante_img FROM pedidos p INNER JOIN persona per ON p.id_persona = per.id_persona LEFT JOIN pagos pa ON p.id_pedido = pa.id_pedido ORDER BY p.id_pedido DESC";
        
        try {
            cn = ConexionSingleton.getConnection();
            PreparedStatement st = cn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                Pedidos ped = new Pedidos();
                ped.setId_pedido(rs.getInt("id_pedido"));
                ped.setFecha_pedido(rs.getTimestamp("fecha_pedido"));
                ped.setTipo_entrega(rs.getString("tipo_entrega"));
                ped.setDireccion_envio(rs.getString("direccion_envio"));
                ped.setTelefono_contacto(rs.getString("telefono_contacto"));
                ped.setDestinatario_nombre(rs.getString("destinatario_nombre"));
                ped.setTotal(rs.getDouble("total"));
                ped.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                
                Persona per = new Persona();
                per.setNombre(rs.getString("nombre"));
                per.setApellido(rs.getString("apellido"));
                ped.setPersona(per);
                
                Pagos pago = new Pagos();
                pago.setMetodo_pago(rs.getString("metodo_pago"));
                pago.setComprobante_img(rs.getString("comprobante_img"));
                ped.setPago(pago);
                
                List<DetallePedido> detalles = new ArrayList<>();
                String sqlDet = "SELECT dp.cantidad, dp.precio_unitario, dp.subtotal, pr.nombre FROM detalles_pedido dp INNER JOIN productos pr ON dp.id_producto = pr.id_producto WHERE dp.id_pedido = ?";
                PreparedStatement stDet = cn.prepareStatement(sqlDet);
                stDet.setInt(1, ped.getId_pedido());
                ResultSet rsDet = stDet.executeQuery();
                
                while(rsDet.next()){
                    DetallePedido det = new DetallePedido();
                    det.setCantidad(rsDet.getInt("cantidad"));
                    det.setPrecio_unitario(rsDet.getDouble("precio_unitario"));
                    det.setSubtotal(rsDet.getDouble("subtotal"));
                    
                    Productos pr = new Productos();
                    pr.setNombre(rsDet.getString("nombre"));
                    det.setProducto(pr);
                    
                    detalles.add(det);
                }
                rsDet.close();
                stDet.close();
                
                ped.setDetalles(detalles);
                lista.add(ped);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean cambiarEstadoPedido(int id_pedido, String nuevoEstado) {
        boolean exito = false;
        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";
        try {
            cn = ConexionSingleton.getConnection();
            PreparedStatement st = cn.prepareStatement(sql);
            st.setString(1, nuevoEstado);
            st.setInt(2, id_pedido);
            exito = st.executeUpdate() > 0;
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exito;
    }
}