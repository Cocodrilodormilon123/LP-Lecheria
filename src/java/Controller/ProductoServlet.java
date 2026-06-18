package Controller;

import Dao.ProductoDaoImpl;
import Interface.IProducto;
import Model.Categoria;
import Model.Productos;
import java.io.File;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5)
public class ProductoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        IProducto dao = new ProductoDaoImpl();

        if ("listarJSON".equals(accion) || "listarAdminJSON".equals(accion)) {
            List<Productos> lista = ("listarAdminJSON".equals(accion)) ? dao.listarAdmin() : dao.listarCatalogo();
            StringBuilder json = new StringBuilder("[");
            
            for (int i = 0; i < lista.size(); i++) {
                Productos p = lista.get(i);
                
                json.append(String.format(
                    "{\"id_producto\":%d, \"id_categoria\":%d, \"categoria\":\"%s\", \"nombre\":\"%s\", \"descripcion\":\"%s\", \"precio\":%.2f, \"stock\":%d, \"imagen\":\"%s\"}",
                    p.getId_producto(), 
                    p.getCategoria().getId_categoria(), 
                    p.getCategoria().getNombre(), 
                    p.getNombre(), 
                    p.getDescripcion(), 
                    p.getPrecio(), 
                    p.getStock(), 
                    p.getImagen()
                ));
                
                if (i < lista.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            
            String jsonFinal = json.toString().replace(String.format("%.2f", 1.0).substring(1, 2), ".");
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonFinal);
            
        } else {
            List<Productos> listaCatalogo = dao.listarCatalogo();
            request.setAttribute("productos", listaCatalogo);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        IProducto dao = new ProductoDaoImpl();

        if ("registrar".equals(accion) || "actualizar".equals(accion)) {
            Productos p = new Productos();
            Categoria c = new Categoria();
            
            c.setId_categoria(Integer.parseInt(request.getParameter("id_categoria")));
            p.setCategoria(c);
            p.setNombre(request.getParameter("nombre"));
            p.setDescripcion(request.getParameter("descripcion"));
            p.setPrecio(Double.parseDouble(request.getParameter("precio")));
            p.setStock(Integer.parseInt(request.getParameter("stock")));
            
            Part filePart = request.getPart("imagen");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = filePart.getSubmittedFileName();
                
                String pathTomcat = getServletContext().getRealPath("/assets/img/productos/");
                
                File folderTomcat = new File(pathTomcat);
                
                filePart.write(pathTomcat + File.separator + fileName);
                
                try {
                    String pathWeb = folderTomcat.getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath() 
                                     + File.separator + "web" + File.separator + "assets" + File.separator + "img" + File.separator + "productos";
                    
                    File folderWeb = new File(pathWeb);
                    if (!folderWeb.exists()) {
                        folderWeb.mkdirs();
                    }
                    
                    java.nio.file.Files.copy(
                        new File(pathTomcat + File.separator + fileName).toPath(),
                        new File(pathWeb + File.separator + fileName).toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (Exception e) {
                    System.out.println("Nota de sincronizacion: Guardado solo en entorno de ejecucion.");
                }

                p.setImagen(fileName);
            } else {
                if ("actualizar".equals(accion)) {
                    int id = Integer.parseInt(request.getParameter("id_producto"));
                    Productos pAntiguo = dao.buscarPorId(id);
                    p.setImagen(pAntiguo.getImagen());
                }
            }

            if ("registrar".equals(accion)) {
                if (dao.registrar(p)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else if ("actualizar".equals(accion)) {
                p.setId_producto(Integer.parseInt(request.getParameter("id_producto")));
                if (dao.actualizar(p)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
            
        } else if ("eliminar".equals(accion)) {
            int id = Integer.parseInt(request.getParameter("id_producto"));
            if (dao.eliminar(id)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}