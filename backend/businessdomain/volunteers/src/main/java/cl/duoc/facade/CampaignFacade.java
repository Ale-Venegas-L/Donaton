package cl.duoc.facade;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class CampaignFacade {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String CAMPAIGN_SERVICE_URL = "http://campaign:8081/campaigns";

    @CircuitBreaker(name = "campaignService", fallbackMethod = "getCampaignByIdFallback")
    public Optional<CampaignModel> getCampaignById(Long campaignId) {
        WebClient webClient = webClientBuilder.build();
        CampaignModel campaign = webClient.get()
                .uri(CAMPAIGN_SERVICE_URL + "/{id}", campaignId)
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    return response.bodyToMono(CampaignModel.class);
                })
                .block();
        return Optional.ofNullable(campaign);
    }

    public Optional<CampaignModel> getCampaignByIdFallback(Long campaignId, Throwable ex) {
        return Optional.empty();
    }

    public boolean campaignExists(Long campaignId) {
        return getCampaignById(campaignId).isPresent();
    }

    public boolean isCampaignActive(Long campaignId) {
        Optional<CampaignModel> campaign = getCampaignById(campaignId);
        return campaign.map(c -> c.getEstado() == CampaignStatus.ACTIVE).orElse(false);
    }

    public boolean canAcceptVolunteers(Long campaignId) {
        Optional<CampaignModel> campaign = getCampaignById(campaignId);
        return campaign.map(c -> c.getEstado() == CampaignStatus.ACTIVE || 
                                c.getEstado() == CampaignStatus.PLANNED).orElse(false);
    }
}
