package cl.duoc.service;

import cl.duoc.dto.DonationRequest;
import cl.duoc.dto.DonationUpdateRequest;
import cl.duoc.facade.CampaignFacade;
import cl.duoc.model.*;
import cl.duoc.repo.DonationRepo;
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
class DonationServiceTest {

    @Mock
    private DonationRepo donationRepo;

    @Mock
    private CampaignFacade campaignFacade;

    @InjectMocks
    private DonationService donationService;

    private CampaignModel campaign;

    @BeforeEach
    void setUp() {
        campaign = new CampaignModel();
        campaign.setId(1L);
        campaign.setNombre("Active Campaign");
        campaign.setEstado(CampaignStatus.ACTIVE);
    }

    @Test
    void createDonation_WhenCampaignActive_ShouldSaveDonation() {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(1L);
        request.setType("MONETARY");
        request.setDonorName("John");
        request.setAmount(100.0);
        request.setCurrency("USD");

        when(campaignFacade.getCampaignById(1L)).thenReturn(Optional.of(campaign));
        when(donationRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Donation result = donationService.createDonation(request);

        assertNotNull(result);
        assertTrue(result instanceof MonetaryDonation);
        verify(donationRepo).save(any());
    }

    @Test
    void createDonation_WhenCampaignInactive_ShouldThrowException() {
        campaign.setEstado(CampaignStatus.PLANNED);
        DonationRequest request = new DonationRequest();
        request.setCampaignId(1L);
        request.setType("MONETARY");
        request.setAmount(100.0);
        request.setCurrency("USD");

        when(campaignFacade.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        assertThrows(IllegalArgumentException.class, () -> donationService.createDonation(request));
    }

    @Test
    void createDonation_WhenCampaignNotFound_ShouldThrowException() {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(99L);
        request.setType("MONETARY");

        when(campaignFacade.getCampaignById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> donationService.createDonation(request));
    }

    @Test
    void createDonation_WhenMonetaryMissingData_ShouldThrowException() {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(1L);
        request.setType("MONETARY");
        
        assertThrows(IllegalArgumentException.class, () -> request.setAmount(null));
    }

    @Test
    void createDonation_WhenObjectMissingData_ShouldThrowException() {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(1L);
        request.setType("OBJECT");
        request.setObjectName("Laptop");
        // Missing other fields

        when(campaignFacade.getCampaignById(1L)).thenReturn(Optional.of(campaign));

        assertThrows(IllegalArgumentException.class, () -> donationService.createDonation(request));
    }

    @Test
    void updateDonation_WhenExists_ShouldUpdateFields() {
        MonetaryDonation monetaryDonation = new MonetaryDonation(campaign, "Old Donor", "Old Desc", 50.0, "USD");
        monetaryDonation.setId(1L);

        DonationUpdateRequest updateRequest = new DonationUpdateRequest();
        updateRequest.setDonorName("New Donor");
        updateRequest.setAmount(150.0);

        when(donationRepo.findById(1L)).thenReturn(Optional.of(monetaryDonation));
        when(donationRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Donation result = donationService.updateDonation(1L, updateRequest);

        assertNotNull(result);
        assertEquals("New Donor", result.getDonorName());
        assertEquals(150.0, ((MonetaryDonation)result).getAmount());
    }

    @Test
    void deleteDonation_WhenExists_ShouldReturnTrue() {
        when(donationRepo.existsById(1L)).thenReturn(true);

        boolean result = donationService.deleteDonation(1L);

        assertTrue(result);
        verify(donationRepo).deleteById(1L);
    }
}
