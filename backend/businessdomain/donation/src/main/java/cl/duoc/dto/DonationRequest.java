package cl.duoc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Request object for creating different types of donations")
public class DonationRequest {
    
    @NotNull(message = "El ID de la campaña es obligatorio")
    @Schema(description = "ID de la campaña asociada", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long campaignId;
    
    @Schema(description = "Tipo de donación", requiredMode = Schema.RequiredMode.REQUIRED, 
            example = "MONETARY", allowableValues = {"MONETARY", "OBJECT"})
    private String type;
    
    @Schema(description = "Nombre del donante", requiredMode = Schema.RequiredMode.REQUIRED, example = "John Doe")
    private String donorName;
    
    @Schema(description = "Descripción de la donación", requiredMode = Schema.RequiredMode.REQUIRED, 
            example = "Donación para la comunidad")
    private String description;
    
    // Monetary donation fields
    @Schema(description = "Monto para donaciones monetarias", example = "100.50")
    private Double amount;
    
    @Schema(description = "Moneda para donaciones monetarias", example = "CLP")
    private String currency;
    
    // Object donation fields
    @Schema(description = "Nombre del objeto donado", example = "Laptop")
    private String objectName;
    
    @Schema(description = "Categoría del objeto donado", example = "Electrónico")
    private String category;
    
    @Schema(description = "Valor estimado del objeto", example = "500.0")
    private Double estimatedValue;
    
    @Schema(description = "Cantidad de objetos donados", example = "1")
    private Integer quantity;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        if (campaignId == null) {
            throw new IllegalArgumentException("El ID de la campaña es obligatorio");
        }
        this.campaignId = campaignId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        if (donorName == null || donorName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del donante es obligatorio");
        }
        this.donorName = donorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la donación es obligatoria");
        }
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }
        this.currency = currency;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del objeto es obligatorio");
        }
        this.objectName = objectName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría del objeto es obligatoria");
        }
        this.category = category;
    }

    public Double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(Double estimatedValue) {
        if (estimatedValue == null || estimatedValue <= 0) {
            throw new IllegalArgumentException("El valor estimado debe ser mayor a cero");
        }
        this.estimatedValue = estimatedValue;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.quantity = quantity;
    }
}
