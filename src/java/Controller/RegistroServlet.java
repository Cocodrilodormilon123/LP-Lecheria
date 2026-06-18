package Controller;

import Dao.PersonaDaoImpl;
import Interface.IPersona;
import Model.Persona;
import Model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Persona p = new Persona();
        p.setNombre(request.getParameter("nombre"));
        p.setApellido(request.getParameter("apellido"));
        p.setDni(request.getParameter("dni"));
        p.setEmail(request.getParameter("correo"));
        p.setDireccion(request.getParameter("direccion"));
        p.setTelefono(request.getParameter("telefono"));

        Usuario u = new Usuario();
        u.setPassword(request.getParameter("contrasena"));

        IPersona dao = new PersonaDaoImpl();
        int r = dao.insert(p, u);

        if (r > 0) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else if (r == -2) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}