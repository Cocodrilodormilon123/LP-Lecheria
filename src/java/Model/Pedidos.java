package Model;
import java.sql.Timestamp;
import java.util.List;

public class Pedidos {
    private int id_pedido;
    private Persona persona;
    private String tipo_entrega;
    private String direccion_envio;
    private String referencia_envio;
    private String telefono_contacto;
    private String destinatario_nombre;
    private double total;
    private EstadoPedido estado;
    private Timestamp fecha_pedido;
    private List<DetallePedido> detalles; 
    private Pagos pago;

    public Pedidos() {}

    public String getTipo_entrega() { return tipo_entrega; }
    public void setTipo_entrega(String tipo_entrega) { this.tipo_entrega = tipo_entrega; }

    public String getDireccion_envio() { return direccion_envio; }
    public void setDireccion_envio(String direccion_envio) { this.direccion_envio = direccion_envio; }

    public String getReferencia_envio() { return referencia_envio; }
    public void setReferencia_envio(String referencia_envio) { this.referencia_envio = referencia_envio; }

    public String getTelefono_contacto() { return telefono_contacto; }
    public void setTelefono_contacto(String telefono_contacto) { this.telefono_contacto = telefono_contacto; }

    public String getDestinatario_nombre() { return destinatario_nombre; }
    public void setDestinatario_nombre(String destinatario_nombre) { this.destinatario_nombre = destinatario_nombre; }

    public int getId_pedido() { return id_pedido; }
    public void setId_pedido(int id_pedido) { this.id_pedido = id_pedido; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public Timestamp getFecha_pedido() { return fecha_pedido; }
    public void setFecha_pedido(Timestamp fecha_pedido) { this.fecha_pedido = fecha_pedido; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    public Pagos getPago() { return pago; }
    public void setPago(Pagos pago) { this.pago = pago; }
}