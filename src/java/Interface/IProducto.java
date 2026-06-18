package Interface;

import Model.Productos;
import java.util.List;

public interface IProducto {
    public List<Productos> listarCatalogo();
    public List<Productos> listarAdmin();
    public Productos buscarPorId(int id);
    public boolean registrar(Productos p);
    public boolean actualizar(Productos p);
    public boolean eliminar(int id);
}