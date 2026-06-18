package Interface;

import Model.DetallePedido;
import Model.Pedidos;
import java.util.List;

public interface IPedido {
    public boolean registrarCompra(Pedidos pedido, List<DetallePedido> detalles);
    public List<Pedidos> listarMisPedidos(int id_persona);
    public List<Pedidos> listarTodosLosPedidos();
    public boolean cambiarEstadoPedido(int id_pedido, String nuevoEstado);
}