package Controller;

import Dao.ProductoDaoImpl;
import Interface.IProducto;
import Model.Carrito;
import Model.Productos;
import Util.ConexionSingleton;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CarritoServlet", urlPatterns = {"/CarritoServlet"})
public class CarritoServlet extends HttpServlet {

    // MÉTODO GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        String accion = request.getParameter("accion");
        
        if ("validarCupon".equals(accion)) {
            String codigo = request.getParameter("codigo");
            int descuento = 0;
            boolean valido = false;
            
            String sql = "SELECT descuento_porcentaje FROM cupones WHERE codigo = ? AND activo = true AND stock > 0";
            
            try (Connection con = ConexionSingleton.getConnection();
     PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        descuento = rs.getInt("descuento_porcentaje");
                        valido = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            String jsonResponse = "{\"valido\":" + valido + ", \"descuento\":" + descuento + "}";
            
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    // MÉTODO POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int idProducto = Integer.parseInt(request.getParameter("idProducto"));
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));

        IProducto dao = new ProductoDaoImpl();
        Productos p = dao.buscarPorId(idProducto);

        if (p != null) {
            double subtotal = p.getPrecio() * cantidad;
            Carrito item = new Carrito(p, cantidad, subtotal);

            HttpSession session = request.getSession();
            List<Carrito> listaCarrito = (List<Carrito>) session.getAttribute("carrito");

            if (listaCarrito == null) {
                listaCarrito = new ArrayList<>();
            }

            listaCarrito.add(item);
            session.setAttribute("carrito", listaCarrito);
        }

        response.sendRedirect("ProductoServlet");
    }
}