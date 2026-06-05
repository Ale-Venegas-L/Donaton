package cl.duoc.service;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignService campaignService;

    private CampaignModel campaign;

    @BeforeEach
    void setUp() {
        campaign = new CampaignModel();
        campaign.setId(1L);
        campaign.setNombre("Test Campaign");
        campaign.setDescripcion("Test Description");
        campaign.setEstado(CampaignStatus.ACTIVE);
    }

    @Test
    void createCampaign_WhenStatusNull_ShouldSetToPlanned() {
        CampaignModel newCampaign = new CampaignModel();
        newCampaign.setNombre("New");
        newCampaign.setDescripcion("Desc");
        newCampaign.setEstado(null);

        when(campaignRepository.save(any())).thenReturn(newCampaign);

        CampaignModel created = campaignService.createCampaign(newCampaign);

        assertEquals(CampaignStatus.PLANNED, created.getEstado());
        verify(campaignRepository).save(newCampaign);
    }

    @Test
    void getCampaignById_WhenExists_ShouldReturnCampaign() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        Optional<CampaignModel> result = campaignService.getCampaignById(1L);

        assertTrue(result.isPresent());
        assertEquals(campaign, result.get());
    }

    @Test
    void getCampaignById_WhenNullId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> campaignService.getCampaignById(null));
    }

    @Test
    void updateCampaign_WhenExists_ShouldUpdateAndSave() {
        CampaignModel details = new CampaignModel();
        details.setNombre("Updated Name");
        details.setDescripcion("Updated Desc");
        details.setEstado(CampaignStatus.COMPLETED);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenReturn(campaign);

        CampaignModel updated = campaignService.updateCampaign(1L, details);

        assertNotNull(updated);
        assertEquals("Updated Name", updated.getNombre());
        assertEquals(CampaignStatus.COMPLETED, updated.getEstado());
        verify(campaignRepository).save(campaign);
    }

    @Test
    void updateCampaign_WhenNotExists_ShouldReturnNull() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        CampaignModel updated = campaignService.updateCampaign(1L, new CampaignModel());

        assertNull(updated);
    }

    @Test
    void deleteCampaign_WhenExists_ShouldReturnTrue() {
        when(campaignRepository.existsById(1L)).thenReturn(true);

        boolean deleted = campaignService.deleteCampaign(1L);

        assertTrue(deleted);
        verify(campaignRepository).deleteById(1L);
    }

    @Test
    void deleteCampaign_WhenNotExists_ShouldReturnFalse() {
        when(campaignRepository.existsById(1L)).thenReturn(false);

        boolean deleted = campaignService.deleteCampaign(1L);

        assertFalse(deleted);
        verify(campaignRepository, never()).deleteById(any());
    }

    @Test
    void canReceiveDonations_WhenActive_ShouldReturnTrue() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        boolean canDonate = campaignService.canReceiveDonations(1L);

        assertTrue(canDonate);
    }

    @Test
    void canReceiveDonations_WhenPlanned_ShouldReturnFalse() {
        campaign.setEstado(CampaignStatus.PLANNED);
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        boolean canDonate = campaignService.canReceiveDonations(1L);

        assertFalse(canDonate);
    }

    @Test
    void canReceiveDonations_WhenNotFound_ShouldReturnFalse() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        boolean canDonate = campaignService.canReceiveDonations(1L);

        assertFalse(canDonate);
    }

    @Test
    void changeStatus_WhenExists_ShouldUpdateStatusAndSave() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenReturn(campaign);

        CampaignModel updated = campaignService.changeStatus(1L, CampaignStatus.COMPLETED);

        assertNotNull(updated);
        assertEquals(CampaignStatus.COMPLETED, updated.getEstado());
        verify(campaignRepository).save(campaign);
    }
}
