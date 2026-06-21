package cl.duoc.config;

import cl.duoc.facade.CampaignFacade;
import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@TestConfiguration
public class TestCampaignFacadeStub {

    private final Map<Long, CampaignModel> campaigns = new HashMap<>();

    public TestCampaignFacadeStub() {
        CampaignModel active = new CampaignModel();
        active.setId(1L);
        active.setNombre("Active Campaign");
        active.setDescripcion("Accepting donations");
        active.setEstado(CampaignStatus.ACTIVE);
        campaigns.put(1L, active);

        CampaignModel planned = new CampaignModel();
        planned.setId(2L);
        planned.setNombre("Planned Campaign");
        planned.setDescripcion("Not yet accepting");
        planned.setEstado(CampaignStatus.PLANNED);
        campaigns.put(2L, planned);
    }

    @Bean
    @Primary
    public CampaignFacade testCampaignFacade() {
        return new CampaignFacade() {
            @Override
            public Optional<CampaignModel> getCampaignById(Long campaignId) {
                return Optional.ofNullable(campaigns.get(campaignId));
            }

            @Override
            public Optional<CampaignModel> getCampaignByIdFallback(Long campaignId, Throwable ex) {
                return Optional.empty();
            }

            @Override
            public boolean canReceiveDonations(Long campaignId) {
                return Optional.ofNullable(campaigns.get(campaignId))
                        .map(c -> c.getEstado() == CampaignStatus.ACTIVE)
                        .orElse(false);
            }

            @Override
            public boolean canReceiveDonationsFallback(Long campaignId, Throwable ex) {
                return false;
            }

            @Override
            public boolean isCampaignActive(Long campaignId) {
                return Optional.ofNullable(campaigns.get(campaignId))
                        .map(c -> c.getEstado() == CampaignStatus.ACTIVE)
                        .orElse(false);
            }

            @Override
            public boolean campaignExists(Long campaignId) {
                return campaigns.containsKey(campaignId);
            }
        };
    }
}
