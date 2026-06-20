package cl.duoc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "campaign")
public class CampaignModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    @NotBlank(message = "El nombre de la campaña no puede estar vacío")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "La descripción de la campaña no puede estar vacía")
    @Column(nullable = false, length = 1000)
    private String descripcion;
    
    @NotNull(message = "El estado de la campaña es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus estado;
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    public boolean canReceiveDonations() {
        return estado == CampaignStatus.ACTIVE;
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
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public CampaignStatus getEstado() {
        return estado;
    }
    
    public void setEstado(CampaignStatus estado) {
        this.estado = estado;
    }
    
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
