package cl.duoc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "volunteers", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email", name = "uk_volunteer_email")
})
@Schema(description = "Volunteer entity representing a volunteer in the system")
public class Volunteer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the volunteer", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @NotBlank(message = "El nombre del voluntario es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    @Schema(description = "First name of the volunteer", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @NotBlank(message = "El apellido del voluntario es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    @Schema(description = "Last name of the volunteer", example = "Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellido;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Column(nullable = false, unique = true)
    @Schema(description = "Email address of the volunteer", example = "juan.perez@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "El formato del teléfono no es válido")
    @Column(nullable = false)
    @Schema(description = "Phone number of the volunteer", example = "+56912345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefono;
    
    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    @Schema(description = "Address of the volunteer", example = "Av. Siempre Viva 123, Santiago")
    private String direccion;
    
    @Column(nullable = false)
    @Schema(description = "Registration date of the volunteer", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaRegistro;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "volunteer_campaigns",
        joinColumns = @JoinColumn(name = "volunteer_id"),
        inverseJoinColumns = @JoinColumn(name = "campaign_id")
    )
    @Schema(description = "Campaigns in which the volunteer participates")
    private Set<CampaignModel> campaigns = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public Volunteer(String nombre, String apellido, String email, String telefono, String direccion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
