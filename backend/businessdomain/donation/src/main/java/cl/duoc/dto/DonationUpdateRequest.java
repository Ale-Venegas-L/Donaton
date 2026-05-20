package cl.duoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for updating a donation")
public class DonationUpdateRequest {

    @Schema(description = "Nombre del donante", example = "John Doe")
    private String donorName;

    @Schema(description = "Descripción de la donación", example = "Donación actualizada")
    private String description;

    @Schema(description = "ID de la campaña", example = "1")
    private Long campaignId;

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

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(Double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
