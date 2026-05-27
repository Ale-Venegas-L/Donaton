package cl.duoc.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MonetaryDonation.class, name = "MONETARY"),
    @JsonSubTypes.Type(value = ObjectDonation.class, name = "OBJECT")
})
@Schema(description = "Base donation entity representing a donation")
public abstract class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Identificador único de la donación", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @NotNull(message = "La campaña es obligatoria")
    @Column(name = "campaign_id", nullable = false)
    @Schema(description = "ID de la campaña asociada a la donación", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long campaignId;
    
    @Transient
    @Schema(description = "Campaña asociada a la donación", requiredMode = Schema.RequiredMode.REQUIRED)
    private CampaignModel campaign;
    
    @NotBlank(message = "El nombre del donante es obligatorio")
    @Schema(description = "Nombre del donante", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    protected String donorName;
    
    @NotBlank(message = "La descripción de la donación es obligatoria")
    @Schema(description = "Descripción de la donación", example = "Donación monetaria para caridad", requiredMode = Schema.RequiredMode.REQUIRED)
    protected String description;
    
    @NotNull(message = "La fecha de registro es obligatoria")
    @Column(name = "registration_date", nullable = false)
    @Schema(description = "Fecha de registro de la donación", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime registrationDate;
    
    public Donation() {
        this.registrationDate = LocalDateTime.now();
    }
    
    public Donation(CampaignModel campaign, String donorName, String description) {
        this.campaignId = campaign.getId();
        this.campaign = campaign;
        this.donorName = donorName;
        this.description = description;
        this.registrationDate = LocalDateTime.now();
    }
    
    public abstract String getType();
    public abstract double getValue();
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCampaignId() {
        return campaignId;
    }
    
    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }
    
    public CampaignModel getCampaign() {
        return campaign;
    }
    
    public void setCampaign(CampaignModel campaign) {
        this.campaign = campaign;
        if (campaign != null) {
            this.campaignId = campaign.getId();
        }
    }
    
    public String getDonorName() {
        return donorName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Donation{type=" + getType() + ", donor=" + donorName + ", description=" + description + ", value=" + getValue() + "}";
    }
}
