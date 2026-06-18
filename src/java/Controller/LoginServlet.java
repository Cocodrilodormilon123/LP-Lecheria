package Controller;

import Dao.UsuarioDaoImpl;
import Interface.IUsuario;
import Model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("correo");
        String pass = request.getParameter("contrasena");

        IUsuario dao = new UsuarioDaoImpl();
        Usuario usuarioLogueado = dao.validate(user, pass);

        if (usuarioLogueado != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuarioActivo", usuarioLogueado);
            session.setAttribute("usuario_nombre", usuarioLogueado.getPersona().getNombre());
            session.setAttribute("usuario_rol", usuarioLogueado.getRol().name());

            String jsonRespuesta = "{\"nombre\":\"" + usuarioLogueado.getPersona().getNombre() + 
                                   "\", \"rol\":\"" + usuarioLogueado.getRol().name() + 
                                   "\", \"id_persona\":" + usuarioLogueado.getPersona().getId_persona() + "}";

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonRespuesta);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}