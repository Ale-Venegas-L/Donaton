package cl.duoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request object for creating and updating volunteers")
public class VolunteerRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "First name of the volunteer", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Schema(description = "Last name of the volunteer", example = "Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellido;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Schema(description = "Email address of the volunteer", example = "juan.perez@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "El formato del teléfono no es válido")
    @Schema(description = "Phone number of the volunteer", example = "+56912345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefono;
    
    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    @Schema(description = "Address of the volunteer", example = "Av. Siempre Viva 123, Santiago")
    private String direccion;

    // Getters and Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
