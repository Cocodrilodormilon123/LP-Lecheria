
package Model;

public class DetallePedido {
    private int id_detalle;
    private Pedidos pedido;
    private Productos producto;
    private int cantidad;
    private double precio_unitario;
    private double subtotal;

    public DetallePedido() {}

    public int getId_detalle() { return id_detalle; }
    public void setId_detalle(int id_detalle) { this.id_detalle = id_detalle; }

    public Pedidos getPedido() { return pedido; }
    public void setPedido(Pedidos pedido) { this.pedido = pedido; }

    public Productos getProducto() { return producto; }
    public void setProducto(Productos producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecio_unitario() { return precio_unitario; }
    public void setPrecio_unitario(double precio_unitario) { this.precio_unitario = precio_unitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}