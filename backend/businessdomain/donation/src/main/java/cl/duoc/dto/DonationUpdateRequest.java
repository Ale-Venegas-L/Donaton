package cl.duoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for updating a donation")
public class DonationUpdateRequest {

    @NotBlank(message = "El nombre del donante es obligatorio")
    @Schema(description = "Nombre del donante", requiredMode = Schema.RequiredMode.REQUIRED, example = "John Doe")
    private String donorName;

    @NotBlank(message = "La descripción de la donación es obligatoria")
    @Schema(description = "Descripción de la donación", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "Donación actualizada para la comunidad")
    private String description;

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
}
