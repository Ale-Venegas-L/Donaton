package cl.duoc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class CampaignModel {
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    
    private Date fechaInicio;
    private Date fechaFin;
    
    public boolean canReceiveDonations() {
        return estado == CampaignStatus.ACTIVE;
    }
    
    // Getters and setters
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
    
    public Date getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public Date getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
}
