
package Model;

import java.sql.Timestamp;

public class Pagos {
    private int id_pago;
    private int id_pedido;
    private String metodo_pago;
    private double monto;
    private String comprobante_img;
    private Timestamp fecha_pago;

    public Pagos() {}

    public int getId_pago() { return id_pago; }
    public void setId_pago(int id_pago) { this.id_pago = id_pago; }

    public int getId_pedido() { return id_pedido; }
    public void setId_pedido(int id_pedido) { this.id_pedido = id_pedido; }

    public String getMetodo_pago() { return metodo_pago; }
    public void setMetodo_pago(String metodo_pago) { this.metodo_pago = metodo_pago; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getComprobante_img() { return comprobante_img; }
    public void setComprobante_img(String comprobante_img) { this.comprobante_img = comprobante_img; }

    public Timestamp getFecha_pago() { return fecha_pago; }
    public void setFecha_pago(Timestamp fecha_pago) { this.fecha_pago = fecha_pago; }
}
