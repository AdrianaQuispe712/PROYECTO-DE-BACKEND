package com.hotel.reservas.dto;

public class EmpleadoDTO {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String puesto;

    public EmpleadoDTO() {
    }

    public EmpleadoDTO(Long id, String nombre, String email, String telefono, String puesto) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.puesto = puesto;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getPuesto() {
        return puesto;
    }
    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }
}
