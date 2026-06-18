
package Model;

public class Productos {
    private int id_producto;
    private Categoria categoria;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String imagen;

    public Productos() {}

    public Productos(int id_producto, Categoria categoria, String nombre, String descripcion, double precio, int stock, String imagen) {
        this.id_producto = id_producto;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagen = imagen;
    }

    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}