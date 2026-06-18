package Controller;

import Dao.CategoriaDaoImpl;
import Interface.ICategoria;
import Model.Categoria;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CategoriaServlet", urlPatterns = {"/CategoriaServlet"})
public class CategoriaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        ICategoria dao = new CategoriaDaoImpl();

        if ("listarJSON".equals(accion)) {
            List<Categoria> lista = dao.listar();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < lista.size(); i++) {
                Categoria c = lista.get(i);
                json.append(String.format(
                    "{\"id_categoria\":%d, \"nombre\":\"%s\", \"descripcion\":\"%s\"}",
                    c.getId_categoria(), c.getNombre(), c.getDescripcion()
                ));
                if (i < lista.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        ICategoria dao = new CategoriaDaoImpl();

        if ("registrar".equals(accion)) {
            Categoria c = new Categoria();
            c.setNombre(request.getParameter("nombre"));
            c.setDescripcion(request.getParameter("descripcion"));
            if (dao.registrar(c)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if ("actualizar".equals(accion)) {
            Categoria c = new Categoria();
            c.setId_categoria(Integer.parseInt(request.getParameter("id_categoria")));
            c.setNombre(request.getParameter("nombre"));
            c.setDescripcion(request.getParameter("descripcion"));
            if (dao.actualizar(c)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if ("eliminar".equals(accion)) {
            int id = Integer.parseInt(request.getParameter("id_categoria"));
            if (dao.eliminar(id)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}