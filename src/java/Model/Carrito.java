
package Model;

public class Carrito {
    private Productos producto;
    private int cantidad;
    private double subtotal;

    public Carrito() {}

    public Carrito(Productos producto, int cantidad, double subtotal) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }
    
    public Productos getProducto() { return producto; }
    public void setProducto(Productos producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

}