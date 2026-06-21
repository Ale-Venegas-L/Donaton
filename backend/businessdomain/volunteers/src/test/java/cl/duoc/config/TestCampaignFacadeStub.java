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
        CampaignModel active1 = new CampaignModel();
        active1.setId(1L);
        active1.setNombre("Beach Cleanup");
        active1.setDescripcion("Clean the beach");
        active1.setEstado(CampaignStatus.ACTIVE);
        campaigns.put(1L, active1);

        CampaignModel active2 = new CampaignModel();
        active2.setId(2L);
        active2.setNombre("Tree Planting");
        active2.setDescripcion("Plant trees");
        active2.setEstado(CampaignStatus.ACTIVE);
        campaigns.put(2L, active2);
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
            public boolean campaignExists(Long campaignId) {
                return campaigns.containsKey(campaignId);
            }

            @Override
            public boolean isCampaignActive(Long campaignId) {
                return Optional.ofNullable(campaigns.get(campaignId))
                        .map(c -> c.getEstado() == CampaignStatus.ACTIVE)
                        .orElse(false);
            }

            @Override
            public boolean canAcceptVolunteers(Long campaignId) {
                return Optional.ofNullable(campaigns.get(campaignId))
                        .map(c -> c.getEstado() == CampaignStatus.ACTIVE ||
                                  c.getEstado() == CampaignStatus.PLANNED)
                        .orElse(false);
            }
        };
    }
}
