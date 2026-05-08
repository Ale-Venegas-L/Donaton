package cl.duoc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.Data;
import java.util.Date;

@Data
@Entity
public class CampaignModel {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false, length = 1000)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus estado;
    
    private Date fechaInicio;
    private Date fechaFin;
    
    @Transient
    public boolean canReceiveDonations() {
        return estado == CampaignStatus.ACTIVE;
    }
}
