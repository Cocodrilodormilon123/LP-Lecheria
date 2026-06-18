
package Model;
import java.sql.Timestamp;

public class Persona {
    private int id_persona;
    private String nombre;
    
    private String apellido;
    private String dni;
    
    private String email;
    private String telefono;
    private String direccion;
    private Timestamp creado_en;

    public Persona() {}

    public Persona(int id_persona, String nombre, String apellido, String dni, String email, String telefono, String direccion, Timestamp creado_en) {
        this.id_persona = id_persona;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.creado_en = creado_en;
    }

    public int getId_persona() { return id_persona; }
    public void setId_persona(int id_persona) { this.id_persona = id_persona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Timestamp getCreado_en() { return creado_en; }
    public void setCreado_en(Timestamp creado_en) { this.creado_en = creado_en; }
}