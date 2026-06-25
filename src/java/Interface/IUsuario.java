package Interface;

import Model.Persona;
import Model.Usuario;

public interface IUsuario {
    public Usuario validate(String user, String passw);
    public String listarClientesAdmin();
    public String obtenerHistorialCliente(int idPersona);
    public Persona obtenerPerfil(int idPersona);
    public boolean actualizarPerfil(Persona p);
}