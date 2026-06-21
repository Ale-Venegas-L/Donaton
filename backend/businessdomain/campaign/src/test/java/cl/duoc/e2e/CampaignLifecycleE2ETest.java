package cl.duoc.e2e;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.repository.CampaignRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Campaign Lifecycle End-to-End Test")
class CampaignLifecycleE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CampaignRepository campaignRepository;

    @BeforeEach
    void cleanUp() {
        campaignRepository.deleteAll();
    }

    @Test
    @DisplayName("Full campaign lifecycle: create -> read -> update -> status change -> delete")
    void fullCampaignLifecycle() throws Exception {
        // Step 1: Create campaign
        CampaignModel newCampaign = new CampaignModel();
        newCampaign.setNombre("Food Drive 2026");
        newCampaign.setDescripcion("Collecting food for local communities");
        newCampaign.setEstado(CampaignStatus.PLANNED);
        newCampaign.setFechaInicio(LocalDate.of(2026, 1, 1));
        newCampaign.setFechaFin(LocalDate.of(2026, 12, 31));

        MvcResult createResult = mockMvc.perform(post("/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCampaign)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Food Drive 2026")))
                .andExpect(jsonPath("$.estado", is("PLANNED")))
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        CampaignModel createdCampaign = objectMapper.readValue(createResponse, CampaignModel.class);
        Long campaignId = createdCampaign.getId();
        assertNotNull(campaignId);

        // Step 2: Verify campaign is in database
        assertTrue(campaignRepository.findById(campaignId).isPresent());

        // Step 3: Cannot receive donations when PLANNED
        mockMvc.perform(get("/campaigns/{id}/can-donate", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        // Step 4: Activate the campaign
        mockMvc.perform(put("/campaigns/{id}/status", campaignId)
                .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ACTIVE")));

        // Step 5: Can now receive donations
        mockMvc.perform(get("/campaigns/{id}/can-donate", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        // Step 6: Update campaign details
        CampaignModel updateData = new CampaignModel();
        updateData.setNombre("Food Drive 2026 - Extended");
        updateData.setDescripcion("Extended food collection campaign");
        updateData.setEstado(CampaignStatus.ACTIVE);
        updateData.setFechaInicio(LocalDate.of(2026, 1, 1));
        updateData.setFechaFin(LocalDate.of(2026, 12, 31));

        mockMvc.perform(put("/campaigns/{id}", campaignId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Food Drive 2026 - Extended")));

        // Step 7: Complete the campaign
        mockMvc.perform(put("/campaigns/{id}/status", campaignId)
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("COMPLETED")));

        // Step 8: Cannot receive donations when COMPLETED
        mockMvc.perform(get("/campaigns/{id}/can-donate", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        // Step 9: Delete the campaign
        mockMvc.perform(delete("/campaigns/{id}", campaignId))
                .andExpect(status().isNoContent());

        // Step 10: Verify deletion
        assertFalse(campaignRepository.findById(campaignId).isPresent());
        mockMvc.perform(get("/campaigns/{id}", campaignId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Multiple campaigns: create, query by status, verify counts")
    void multipleCampaignsFlow() throws Exception {
        CampaignModel planned = new CampaignModel();
        planned.setNombre("Planned Campaign");
        planned.setDescripcion("Future campaign");
        planned.setEstado(CampaignStatus.PLANNED);

        CampaignModel active = new CampaignModel();
        active.setNombre("Active Campaign");
        active.setDescripcion("Current campaign");
        active.setEstado(CampaignStatus.ACTIVE);

        CampaignModel completed = new CampaignModel();
        completed.setNombre("Completed Campaign");
        completed.setDescripcion("Past campaign");
        completed.setEstado(CampaignStatus.COMPLETED);

        mockMvc.perform(post("/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planned)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(active)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completed)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/campaigns/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(3)));

        mockMvc.perform(get("/campaigns/active/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));

        mockMvc.perform(get("/campaigns/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Active Campaign")));

        mockMvc.perform(get("/campaigns/status/PLANNED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Planned Campaign")));
    }

    @Test
    @DisplayName("Business rule: cannot update a non-existent campaign")
    void nonExistentCampaignFlow() throws Exception {
        mockMvc.perform(get("/campaigns/999"))
                .andExpect(status().isNotFound());

        CampaignModel validUpdate = new CampaignModel();
        validUpdate.setNombre("Valid Name");
        validUpdate.setDescripcion("Valid description");
        validUpdate.setEstado(CampaignStatus.ACTIVE);

        mockMvc.perform(put("/campaigns/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdate)))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/campaigns/999/status")
                .param("status", "ACTIVE"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/campaigns/999"))
                .andExpect(status().isNotFound());
    }
}
