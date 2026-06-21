package cl.duoc.controller;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.repository.CampaignRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CampaignControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CampaignRepository campaignRepository;

    private CampaignModel savedCampaign;

    @BeforeEach
    void setUp() {
        campaignRepository.deleteAll();
        CampaignModel campaign = new CampaignModel();
        campaign.setNombre("Test Campaign");
        campaign.setDescripcion("Test Description");
        campaign.setEstado(CampaignStatus.ACTIVE);
        campaign.setFechaInicio(LocalDate.now());
        campaign.setFechaFin(LocalDate.now().plusDays(30));
        savedCampaign = campaignRepository.save(campaign);
    }

    @Test
    void createCampaign_ShouldReturn201() throws Exception {
        CampaignModel newCampaign = new CampaignModel();
        newCampaign.setNombre("New Campaign");
        newCampaign.setDescripcion("New Description");
        newCampaign.setEstado(CampaignStatus.PLANNED);

        mockMvc.perform(post("/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCampaign)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("New Campaign")))
                .andExpect(jsonPath("$.estado", is("PLANNED")));
    }

    @Test
    void getAllCampaigns_ShouldReturn200WithList() throws Exception {
        mockMvc.perform(get("/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Test Campaign")));
    }

    @Test
    void getCampaignById_WhenExists_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/campaigns/{id}", savedCampaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Test Campaign")))
                .andExpect(jsonPath("$.descripcion", is("Test Description")));
    }

    @Test
    void getCampaignById_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/campaigns/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCampaignsByStatus_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/campaigns/status/{status}", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateCampaign_WhenExists_ShouldReturn200() throws Exception {
        CampaignModel updated = new CampaignModel();
        updated.setNombre("Updated Campaign");
        updated.setDescripcion("Updated Description");
        updated.setEstado(CampaignStatus.COMPLETED);

        mockMvc.perform(put("/campaigns/{id}", savedCampaign.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Updated Campaign")))
                .andExpect(jsonPath("$.estado", is("COMPLETED")));
    }

    @Test
    void updateCampaign_WhenNotExists_ShouldReturn404() throws Exception {
        CampaignModel updated = new CampaignModel();
        updated.setNombre("Updated");
        updated.setDescripcion("Desc");
        updated.setEstado(CampaignStatus.ACTIVE);

        mockMvc.perform(put("/campaigns/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeCampaignStatus_WhenExists_ShouldReturn200() throws Exception {
        mockMvc.perform(put("/campaigns/{id}/status", savedCampaign.getId())
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("COMPLETED")));
    }

    @Test
    void canReceiveDonations_WhenActive_ShouldReturnTrue() throws Exception {
        mockMvc.perform(get("/campaigns/{id}/can-donate", savedCampaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    void canReceiveDonations_WhenNotActive_ShouldReturnFalse() throws Exception {
        savedCampaign.setEstado(CampaignStatus.PLANNED);
        campaignRepository.save(savedCampaign);

        mockMvc.perform(get("/campaigns/{id}/can-donate", savedCampaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    void deleteCampaign_WhenExists_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/campaigns/{id}", savedCampaign.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCampaign_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/campaigns/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCampaignCount_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/campaigns/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    void getActiveCampaignCount_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/campaigns/active/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }
}
