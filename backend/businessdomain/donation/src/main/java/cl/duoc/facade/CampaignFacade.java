package cl.duoc.facade;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class CampaignFacade {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String CAMPAIGN_SERVICE_URL = "http://campaign:8081/campaigns";

    @CircuitBreaker(name = "campaignService", fallbackMethod = "getCampaignByIdFallback")
    public Optional<CampaignModel> getCampaignById(Long campaignId) {
        try {
            WebClient webClient = webClientBuilder.build();
            CampaignModel campaign = webClient.get()
                    .uri(CAMPAIGN_SERVICE_URL + "/{id}", campaignId)
                    .retrieve()
                    .bodyToMono(CampaignModel.class)
                    .block();
            return Optional.ofNullable(campaign);
        } catch (Exception e) {
            throw e;
        }
    }

    public Optional<CampaignModel> getCampaignByIdFallback(Long campaignId, Throwable ex) {
        return Optional.empty();
    }

    @CircuitBreaker(name = "campaignService", fallbackMethod = "canReceiveDonationsFallback")
    public boolean canReceiveDonations(Long campaignId) {
        try {
            WebClient webClient = webClientBuilder.build();
            Boolean canDonate = webClient.get()
                    .uri(CAMPAIGN_SERVICE_URL + "/{id}/can-donate", campaignId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(canDonate);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean canReceiveDonationsFallback(Long campaignId, Throwable ex) {
        return false;
    }

    public boolean isCampaignActive(Long campaignId) {
        Optional<CampaignModel> campaign = getCampaignById(campaignId);
        return campaign.map(c -> c.getEstado() == CampaignStatus.ACTIVE).orElse(false);
    }

    public boolean campaignExists(Long campaignId) {
        return getCampaignById(campaignId).isPresent();
    }
}
