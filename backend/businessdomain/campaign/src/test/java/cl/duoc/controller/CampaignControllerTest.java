package cl.duoc.controller;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignControllerTest {

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

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
    void createCampaign_ShouldReturnCreated() {
        when(campaignService.createCampaign(any())).thenReturn(campaign);

        ResponseEntity<CampaignModel> response = campaignController.createCampaign(campaign);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(campaign, response.getBody());
    }

    @Test
    void getAllCampaigns_ShouldReturnOk() {
        List<CampaignModel> list = Collections.singletonList(campaign);
        when(campaignService.getAllCampaigns()).thenReturn(list);

        ResponseEntity<List<CampaignModel>> response = campaignController.getAllCampaigns();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody());
    }

    @Test
    void getCampaignById_WhenExists_ShouldReturnOk() {
        when(campaignService.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        ResponseEntity<CampaignModel> response = campaignController.getCampaignById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(campaign, response.getBody());
    }

    @Test
    void getCampaignById_WhenNotExists_ShouldReturnNotFound() {
        when(campaignService.getCampaignById(1L)).thenReturn(Optional.empty());

        ResponseEntity<CampaignModel> response = campaignController.getCampaignById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateCampaign_WhenExists_ShouldReturnOk() {
        when(campaignService.updateCampaign(eq(1L), any())).thenReturn(campaign);

        ResponseEntity<CampaignModel> response = campaignController.updateCampaign(1L, campaign);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(campaign, response.getBody());
    }

    @Test
    void updateCampaign_WhenNotExists_ShouldReturnNotFound() {
        when(campaignService.updateCampaign(eq(1L), any())).thenReturn(null);

        ResponseEntity<CampaignModel> response = campaignController.updateCampaign(1L, campaign);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void changeCampaignStatus_WhenExists_ShouldReturnOk() {
        when(campaignService.changeStatus(eq(1L), any())).thenReturn(campaign);

        ResponseEntity<CampaignModel> response = campaignController.changeCampaignStatus(1L, CampaignStatus.COMPLETED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(campaign, response.getBody());
    }

    @Test
    void canReceiveDonations_ShouldReturnOk() {
        when(campaignService.canReceiveDonations(1L)).thenReturn(true);

        ResponseEntity<Boolean> response = campaignController.canReceiveDonations(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void deleteCampaign_WhenExists_ShouldReturnNoContent() {
        when(campaignService.deleteCampaign(1L)).thenReturn(true);

        ResponseEntity<Void> response = campaignController.deleteCampaign(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteCampaign_WhenNotExists_ShouldReturnNotFound() {
        when(campaignService.deleteCampaign(1L)).thenReturn(false);

        ResponseEntity<Void> response = campaignController.deleteCampaign(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
