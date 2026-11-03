package com.presupuestoya.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClientForm {

    private Long id;

    @NotBlank(message = "El nombre del cliente es obligatorio.")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres.")
    private String name;

    @Size(max = 40, message = "El teléfono no puede superar 40 caracteres.")
    private String phone;

    @Email(message = "Ingresá un email válido.")
    @Size(max = 254, message = "El email es demasiado largo.")
    private String email;

    @Size(max = 250, message = "La dirección no puede superar 250 caracteres.")
    private String address;

    @Size(max = 1000, message = "Las notas no pueden superar 1000 caracteres.")
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
