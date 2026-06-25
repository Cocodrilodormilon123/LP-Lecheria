package Controller;

import Dao.UsuarioDaoImpl;
import Model.Persona;
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
                out.print(dao.listarClientesAdmin());
            } else if ("historial_cliente".equals(accion)) {
                int idPersona = Integer.parseInt(request.getParameter("id_persona"));
                out.print(dao.obtenerHistorialCliente(idPersona));
            } else if ("obtener_perfil".equals(accion)) {
                int idPersona = Integer.parseInt(request.getParameter("id_persona"));
                Persona p = dao.obtenerPerfil(idPersona);
                if(p != null) {
                    String json = String.format(
                        "{\"nombre\":\"%s\", \"apellido\":\"%s\", \"dni\":\"%s\", \"email\":\"%s\", \"telefono\":\"%s\", \"direccion\":\"%s\"}",
                        p.getNombre(),
                        p.getApellido() != null ? p.getApellido() : "",
                        p.getDni() != null ? p.getDni() : "",
                        p.getEmail() != null ? p.getEmail() : "",
                        p.getTelefono() != null ? p.getTelefono() : "",
                        p.getDireccion() != null ? p.getDireccion() : ""
                    );
                    out.print(json);
                } else {
                    out.print("{}");
                }
            } else {
                out.print("[]");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        String accion = request.getParameter("action");
        UsuarioDaoImpl dao = new UsuarioDaoImpl();

        if ("actualizar_perfil".equals(accion)) {
            Persona p = new Persona();
            p.setId_persona(Integer.parseInt(request.getParameter("id_persona")));
            p.setTelefono(request.getParameter("telefono"));
            p.setDireccion(request.getParameter("direccion"));
            
            boolean exito = dao.actualizarPerfil(p);
            
            try (PrintWriter out = response.getWriter()) {
                if (exito) out.print("{\"status\":\"success\"}");
                else out.print("{\"status\":\"error\"}");
            }
        }
    }
}