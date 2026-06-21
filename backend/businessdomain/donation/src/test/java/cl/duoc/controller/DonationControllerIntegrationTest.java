package cl.duoc.controller;

import cl.duoc.dto.DonationRequest;
import cl.duoc.dto.DonationUpdateRequest;
import cl.duoc.facade.CampaignFacade;
import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.model.Donation;
import cl.duoc.model.MonetaryDonation;
import cl.duoc.repo.DonationRepo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DonationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DonationRepo donationRepo;

    @MockBean
    private CampaignFacade campaignFacade;

    private CampaignModel activeCampaign;
    private Donation savedDonation;

    @BeforeEach
    void setUp() {
        donationRepo.deleteAll();

        activeCampaign = new CampaignModel();
        activeCampaign.setId(1L);
        activeCampaign.setNombre("Active Campaign");
        activeCampaign.setDescripcion("Accepting donations");
        activeCampaign.setEstado(CampaignStatus.ACTIVE);

        when(campaignFacade.getCampaignById(1L)).thenReturn(Optional.of(activeCampaign));
        when(campaignFacade.campaignExists(1L)).thenReturn(true);
        when(campaignFacade.getCampaignById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            if (id.equals(1L)) {
                return Optional.of(activeCampaign);
            }
            return Optional.empty();
        });

        MonetaryDonation donation = new MonetaryDonation(activeCampaign, "John Doe", "Test donation", 100.0, "USD");
        savedDonation = donationRepo.save(donation);
    }

    @Test
    void listDonations_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/donation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].donorName", is("John Doe")));
    }

    @Test
    void listDonations_WhenEmpty_ShouldReturn204() throws Exception {
        donationRepo.deleteAll();
        mockMvc.perform(get("/donation"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getDonationById_WhenExists_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/donation/{id}", savedDonation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donorName", is("John Doe")));
    }

    @Test
    void getDonationById_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/donation/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMonetaryDonation_ShouldReturn201() throws Exception {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(1L);
        request.setType("MONETARY");
        request.setDonorName("Jane Doe");
        request.setDescription("Monthly donation");
        request.setAmount(200.0);
        request.setCurrency("CLP");

        mockMvc.perform(post("/donation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.donorName", is("Jane Doe")));
    }

    @Test
    void createObjectDonation_ShouldReturn201() throws Exception {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(1L);
        request.setType("OBJECT");
        request.setDonorName("Bob Smith");
        request.setDescription("Food donation");
        request.setObjectName("Rice");
        request.setCategory("Food");
        request.setEstimatedValue(50.0);
        request.setQuantity(10);

        mockMvc.perform(post("/donation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.donorName", is("Bob Smith")));
    }

    @Test
    void createDonation_WhenCampaignNotFound_ShouldReturn400() throws Exception {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(999L);
        request.setType("MONETARY");
        request.setDonorName("No one");
        request.setDescription("Test");
        request.setAmount(100.0);
        request.setCurrency("USD");

        mockMvc.perform(post("/donation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDonation_WhenExists_ShouldReturn200() throws Exception {
        DonationUpdateRequest update = new DonationUpdateRequest();
        update.setDonorName("Updated Donor");
        update.setAmount(150.0);

        mockMvc.perform(put("/donation/{id}", savedDonation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donorName", is("Updated Donor")));
    }

    @Test
    void updateDonation_WhenNotExists_ShouldReturn404() throws Exception {
        DonationUpdateRequest update = new DonationUpdateRequest();
        update.setDonorName("No one");

        mockMvc.perform(put("/donation/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDonation_WhenExists_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/donation/{id}", savedDonation.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDonation_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/donation/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDonationCount_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/donation/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }
}
