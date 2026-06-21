package cl.duoc.e2e;

import cl.duoc.dto.DonationRequest;
import cl.duoc.dto.DonationUpdateRequest;
import cl.duoc.facade.CampaignFacade;
import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.model.Donation;
import cl.duoc.model.MonetaryDonation;
import cl.duoc.model.ObjectDonation;
import cl.duoc.repo.DonationRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Donation Flow End-to-End Test")
class DonationFlowE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DonationRepo donationRepo;

    @MockBean
    private CampaignFacade campaignFacade;

    private CampaignModel activeCampaign;
    private CampaignModel plannedCampaign;

    @BeforeEach
    void setUp() {
        donationRepo.deleteAll();

        activeCampaign = new CampaignModel();
        activeCampaign.setId(1L);
        activeCampaign.setNombre("Active Campaign");
        activeCampaign.setDescripcion("Accepting donations");
        activeCampaign.setEstado(CampaignStatus.ACTIVE);

        plannedCampaign = new CampaignModel();
        plannedCampaign.setId(2L);
        plannedCampaign.setNombre("Planned Campaign");
        plannedCampaign.setDescripcion("Not yet accepting");
        plannedCampaign.setEstado(CampaignStatus.PLANNED);

        when(campaignFacade.getCampaignById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            if (id.equals(1L)) return Optional.of(activeCampaign);
            if (id.equals(2L)) return Optional.of(plannedCampaign);
            return Optional.empty();
        });
        when(campaignFacade.campaignExists(1L)).thenReturn(true);
        when(campaignFacade.campaignExists(2L)).thenReturn(true);
    }

    @Test
    @DisplayName("Full donation flow: create monetary -> create object -> list -> update -> delete")
    void fullDonationLifecycle() throws Exception {
        // Step 1: Create monetary donation
        DonationRequest monetaryReq = new DonationRequest();
        monetaryReq.setCampaignId(1L);
        monetaryReq.setType("MONETARY");
        monetaryReq.setDonorName("Alice");
        monetaryReq.setDescription("Monthly support");
        monetaryReq.setAmount(250.0);
        monetaryReq.setCurrency("USD");

        MvcResult monetaryResult = mockMvc.perform(post("/donation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monetaryReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.donorName", is("Alice")))
                .andExpect(jsonPath("$.type", is("MONETARY")))
                .andReturn();

        Long monetaryId = objectMapper.readValue(
                monetaryResult.getResponse().getContentAsString(), Donation.class).getId();

        // Step 2: Create object donation
        DonationRequest objectReq = new DonationRequest();
        objectReq.setCampaignId(1L);
        objectReq.setType("OBJECT");
        objectReq.setDonorName("Bob");
        objectReq.setDescription("School supplies");
        objectReq.setObjectName("Notebooks");
        objectReq.setCategory("Education");
        objectReq.setEstimatedValue(30.0);
        objectReq.setQuantity(20);

        MvcResult objectResult = mockMvc.perform(post("/donation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objectReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.donorName", is("Bob")))
                .andExpect(jsonPath("$.type", is("OBJECT")))
                .andReturn();

        Long objectId = objectMapper.readValue(
                objectResult.getResponse().getContentAsString(), Donation.class).getId();

        // Step 3: List all donations
        mockMvc.perform(get("/donation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Step 4: Get donation count
        mockMvc.perform(get("/donation/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(2)));

        // Step 5: Get specific donations
        mockMvc.perform(get("/donation/{id}", monetaryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donorName", is("Alice")));

        mockMvc.perform(get("/donation/{id}", objectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donorName", is("Bob")));

        // Step 6: Update monetary donation
        DonationUpdateRequest update = new DonationUpdateRequest();
        update.setAmount(300.0);

        mockMvc.perform(put("/donation/{id}", monetaryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donorName", is("Alice")));

        // Verify the amount was updated
        MonetaryDonation updatedDonation = (MonetaryDonation) donationRepo.findById(monetaryId).orElseThrow();
        assertEquals(300.0, updatedDonation.getAmount(), 0.01);

        // Step 7: Update object donation
        DonationUpdateRequest objUpdate = new DonationUpdateRequest();
        objUpdate.setObjectName("Pencils");
        objUpdate.setQuantity(50);

        mockMvc.perform(put("/donation/{id}", objectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(objUpdate)))
                .andExpect(status().isOk());

        ObjectDonation updatedObject = (ObjectDonation) donationRepo.findById(objectId).orElseThrow();
        assertEquals("Pencils", updatedObject.getObjectName());
        assertEquals(50, updatedObject.getQuantity());

        // Step 8: Donor count
        mockMvc.perform(get("/donation/donors/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(2)));

        // Step 9: Delete both donations
        mockMvc.perform(delete("/donation/{id}", monetaryId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/donation/{id}", objectId))
                .andExpect(status().isNoContent());

        // Step 10: Verify empty
        mockMvc.perform(get("/donation"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Business rule: cannot donate to non-active campaign")
    void cannotDonateToInactiveCampaign() throws Exception {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(2L);
        request.setType("MONETARY");
        request.setDonorName("Charlie");
        request.setDescription("Test");
        request.setAmount(100.0);
        request.setCurrency("USD");

        mockMvc.perform(post("/donation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Business rule: cannot donate to non-existent campaign")
    void cannotDonateToNonExistentCampaign() throws Exception {
        DonationRequest request = new DonationRequest();
        request.setCampaignId(999L);
        request.setType("MONETARY");
        request.setDonorName("Charlie");
        request.setDescription("Test");
        request.setAmount(100.0);
        request.setCurrency("USD");

        mockMvc.perform(post("/donation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
