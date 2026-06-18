package Controller;

import Dao.PedidoDaoImpl;
import Model.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5)
public class PedidoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PedidoDaoImpl dao = new PedidoDaoImpl();
        List<Pedidos> lista = dao.listarTodosLosPedidos();
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Pedidos p = lista.get(i);
            json.append("{")
                .append("\"id_pedido\":").append(p.getId_pedido()).append(",")
                .append("\"cliente\":\"").append(p.getPersona().getNombre()).append(" ").append(p.getPersona().getApellido()).append("\",")
                .append("\"fecha\":\"").append(p.getFecha_pedido().toString()).append("\",")
                .append("\"entrega\":\"").append(p.getTipo_entrega() != null ? p.getTipo_entrega() : "").append("\",")
                .append("\"direccion\":\"").append(p.getDireccion_envio() != null ? p.getDireccion_envio() : "").append("\",")
                .append("\"telefono\":\"").append(p.getTelefono_contacto() != null ? p.getTelefono_contacto() : "").append("\",")
                .append("\"total\":").append(p.getTotal()).append(",")
                .append("\"estado\":\"").append(p.getEstado().name()).append("\",")
                .append("\"metodo_pago\":\"").append(p.getPago() != null && p.getPago().getMetodo_pago() != null ? p.getPago().getMetodo_pago() : "N/A").append("\",")
                .append("\"comprobante\":\"").append(p.getPago() != null && p.getPago().getComprobante_img() != null ? p.getPago().getComprobante_img() : "").append("\",")
                .append("\"detalles\":[");
                
            for (int j = 0; j < p.getDetalles().size(); j++) {
                DetallePedido d = p.getDetalles().get(j);
                json.append("{")
                    .append("\"producto\":\"").append(d.getProducto().getNombre()).append("\",")
                    .append("\"cantidad\":").append(d.getCantidad()).append(",")
                    .append("\"precio\":").append(d.getPrecio_unitario()).append(",")
                    .append("\"subtotal\":").append(d.getSubtotal())
                    .append("}");
                if(j < p.getDetalles().size() - 1) json.append(",");
            }
            json.append("]}");
            if(i < lista.size() - 1) json.append(",");
        }
        json.append("]");
        
        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        String accion = request.getParameter("action");
        if (accion != null && accion.equals("cambiar_estado")) {
            int idPedido = Integer.parseInt(request.getParameter("id_pedido"));
            String nuevoEstado = request.getParameter("nuevo_estado");
            
            PedidoDaoImpl dao = new PedidoDaoImpl();
            boolean exito = dao.cambiarEstadoPedido(idPedido, nuevoEstado);
            
            try (PrintWriter out = response.getWriter()) {
                if (exito) out.print("{\"status\":\"success\"}");
                else out.print("{\"status\":\"error\"}");
            }
            return;
        }
        
        String jsonTexto = "";
        String comprobanteImg = null;
        String contentType = request.getContentType();

        try {
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                Part filePart = request.getPart("comprobante");
                if (filePart != null && filePart.getSize() > 0) {
                    comprobanteImg = System.currentTimeMillis() + "_" + Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String uploadPath = getServletContext().getRealPath("/assets/img/comprobantes/");
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    filePart.write(uploadPath + File.separator + comprobanteImg);
                }
                jsonTexto = request.getParameter("items");
            } else {
                StringBuilder sb = new StringBuilder();
                String linea;
                try (BufferedReader reader = request.getReader()) {
                    while ((linea = reader.readLine()) != null) sb.append(linea);
                }
                jsonTexto = sb.toString();
            }

            Pedidos pedido = new Pedidos();
            String idUsuario = extraerTextoJson(jsonTexto, "id_usuario");
            if (idUsuario.isEmpty() || "null".equals(idUsuario) || "NaN".equals(idUsuario)) {
                throw new IllegalArgumentException("ID de usuario no valido.");
            }
            
            Persona persona = new Persona();
            persona.setId_persona(Integer.parseInt(idUsuario));
            pedido.setPersona(persona);

            pedido.setTipo_entrega(extraerTextoJson(jsonTexto, "tipo_entrega"));
            pedido.setDireccion_envio(extraerTextoJson(jsonTexto, "direccion"));
            pedido.setReferencia_envio(extraerTextoJson(jsonTexto, "referencia"));
            pedido.setTelefono_contacto(extraerTextoJson(jsonTexto, "telefono"));
            pedido.setDestinatario_nombre(extraerTextoJson(jsonTexto, "destinatario_nombre"));
            pedido.setTotal(Double.parseDouble(extraerTextoJson(jsonTexto, "total")));
            pedido.setEstado(EstadoPedido.valueOf(extraerTextoJson(jsonTexto, "estado")));

            String metodoPago = extraerTextoJson(jsonTexto, "metodo_pago");
            if (comprobanteImg == null && "YAPE".equals(metodoPago)) comprobanteImg = "voucher_yape_temp.png";

            List<DetallePedido> detalles = new ArrayList<>();
            int inicioArreglo = jsonTexto.indexOf("[");
            int finArreglo = jsonTexto.lastIndexOf("]");
            
            if (inicioArreglo != -1 && finArreglo != -1) {
                String seccionItems = jsonTexto.substring(inicioArreglo + 1, finArreglo);
                String[] objetosItems = seccionItems.split("\\}(,)?\\s*\\{");
                for (String itemBloque : objetosItems) {
                    String bloqueLimpio = itemBloque.replace("{", "").replace("}", "").trim();
                    if (bloqueLimpio.isEmpty()) continue;
                    
                    DetallePedido detalle = new DetallePedido();
                    Productos producto = new Productos();
                    producto.setId_producto(Integer.parseInt(extraerTextoJson(bloqueLimpio, "id")));
                    detalle.setProducto(producto);

                    int cant = Integer.parseInt(extraerTextoJson(bloqueLimpio, "cantidad"));
                    double prec = Double.parseDouble(extraerTextoJson(bloqueLimpio, "precio"));

                    detalle.setCantidad(cant);
                    detalle.setPrecio_unitario(prec);
                    detalle.setSubtotal(prec * cant);
                    detalles.add(detalle);
                }
            }

            PedidoDaoImpl dao = new PedidoDaoImpl();
            boolean rpta = dao.registrarCompraConPago(pedido, detalles, metodoPago, comprobanteImg);

            try (PrintWriter out = response.getWriter()) {
                if (rpta) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"status\":\"success\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"status\":\"error\"}");
                }
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"status\":\"error_parse\"}");
                out.flush();
            }
        }
    }

    private String extraerTextoJson(String json, String clave) {
        String patron = "\"" + clave + "\"";
        int posClave = json.indexOf(patron);
        if (posClave == -1) return "";
        int posDosPuntos = json.indexOf(":", posClave);
        if (posDosPuntos == -1) return "";
        int inicio = posDosPuntos + 1;
        while (inicio < json.length() && (Character.isWhitespace(json.charAt(inicio)))) inicio++;
        
        if (json.charAt(inicio) == '"') {
            inicio++;
            int fin = json.indexOf("\"", inicio);
            return json.substring(inicio, fin).trim();
        } else {
            int fin = inicio;
            while (fin < json.length() && json.charAt(fin) != ',' && json.charAt(fin) != '}' && json.charAt(fin) != ']') fin++;
            return json.substring(inicio, fin).trim();
        }
    }
}