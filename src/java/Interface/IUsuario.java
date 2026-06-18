package Interface;

import Model.Usuario;

public interface IUsuario {
    public Usuario validate(String user, String passw);
    public String listarClientesAdmin();
    public String obtenerHistorialCliente(int idPersona);
}