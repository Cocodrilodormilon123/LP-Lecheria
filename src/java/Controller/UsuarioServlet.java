package Controller;

import Dao.UsuarioDaoImpl;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        String accion = request.getParameter("action");
        UsuarioDaoImpl dao = new UsuarioDaoImpl();
        
        try (PrintWriter out = response.getWriter()) {
            if ("listar_usuarios_panel".equals(accion)) {
                // Llama al método que trae todos los clientes
                out.print(dao.listarClientesAdmin());
            } else if ("historial_cliente".equals(accion)) {
                // Llama al método que trae el historial de un solo cliente
                int idPersona = Integer.parseInt(request.getParameter("id_persona"));
                out.print(dao.obtenerHistorialCliente(idPersona));
            } else {
                out.print("[]");
            }
        }
    }
}