package logica;

public class Cliente {

    private String id;
    private String nombre;
    private String email;

    public Cliente(String id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }

	public void setNombre(String nombre2) {
		// TODO Auto-generated method stub
		
	}

	public void setEmail(String email2) {
		// TODO Auto-generated method stub
		
	}
}