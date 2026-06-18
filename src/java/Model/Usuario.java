
package Model;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Usuario {
    private int id_usuario;
    private String usuario; 
    private String password;
    private Rol rol;
    private Persona persona;

    public Usuario() {}

    public Usuario(int id_usuario, String usuario, String password, Rol rol, Persona persona) {
        this.id_usuario = id_usuario;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
        this.persona = persona;
    }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }
    
    public String HashPassword(String password){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length()==1) { hexString.append('0'); }
                hexString.append(hex);               
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash", e);
        }
    }
}