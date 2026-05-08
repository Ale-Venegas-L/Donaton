package cl.duoc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for assigning volunteers to campaigns")
public class CampaignAssignmentRequest {
    
    @NotNull(message = "El ID de la campaña es obligatorio")
    @Schema(description = "Campaign ID to assign the volunteer to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long campaignId;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }
}
