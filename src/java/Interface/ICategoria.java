package Interface;

import Model.Categoria;
import java.util.List;

public interface ICategoria {
    public List<Categoria> listar();
    public Categoria buscarPorId(int id);
    public boolean registrar(Categoria c);
    public boolean actualizar(Categoria c);
    public boolean eliminar(int id);
}