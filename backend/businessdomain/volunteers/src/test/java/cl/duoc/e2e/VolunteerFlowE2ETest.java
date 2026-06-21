package cl.duoc.e2e;

import cl.duoc.dto.CampaignAssignmentRequest;
import cl.duoc.dto.VolunteerRequest;
import cl.duoc.facade.CampaignFacade;
import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.model.Volunteer;
import cl.duoc.repository.VolunteerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
@DisplayName("Volunteer Flow End-to-End Test")
class VolunteerFlowE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @MockBean
    private CampaignFacade campaignFacade;

    private CampaignModel campaign1;
    private CampaignModel campaign2;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        volunteerRepository.deleteAll();
        entityManager.createNativeQuery("DELETE FROM volunteer_campaigns").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM campaign").executeUpdate();

        campaign1 = new CampaignModel();
        campaign1.setNombre("Beach Cleanup");
        campaign1.setDescripcion("Clean the beach");
        campaign1.setEstado(CampaignStatus.ACTIVE);
        entityManager.persist(campaign1);
        entityManager.flush();

        campaign2 = new CampaignModel();
        campaign2.setNombre("Tree Planting");
        campaign2.setDescripcion("Plant trees");
        campaign2.setEstado(CampaignStatus.ACTIVE);
        entityManager.persist(campaign2);
        entityManager.flush();

        Long campaignId1 = campaign1.getId();
        Long campaignId2 = campaign2.getId();
        when(campaignFacade.getCampaignById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            if (id.equals(campaignId1)) return Optional.of(campaign1);
            if (id.equals(campaignId2)) return Optional.of(campaign2);
            return Optional.empty();
        });
        when(campaignFacade.campaignExists(campaignId1)).thenReturn(true);
        when(campaignFacade.campaignExists(campaignId2)).thenReturn(true);
        when(campaignFacade.campaignExists(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id.equals(campaignId1) || id.equals(campaignId2);
        });
        when(campaignFacade.canAcceptVolunteers(campaignId1)).thenReturn(true);
        when(campaignFacade.canAcceptVolunteers(campaignId2)).thenReturn(true);
        when(campaignFacade.isCampaignActive(campaignId1)).thenReturn(true);
        when(campaignFacade.isCampaignActive(campaignId2)).thenReturn(true);
    }

    @Test
    @DisplayName("Full volunteer flow: create -> assign to campaigns -> search -> unassign")
    void fullVolunteerLifecycle() throws Exception {
        // Step 1: Create volunteer
        VolunteerRequest request = new VolunteerRequest();
        request.setNombre("Carlos");
        request.setApellido("Muñoz");
        request.setEmail("carlos@example.com");
        request.setTelefono("+56911111111");

        MvcResult createResult = mockMvc.perform(post("/volunteers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Carlos")))
                .andExpect(jsonPath("$.email", is("carlos@example.com")))
                .andReturn();

        Long volunteerId = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Volunteer.class).getId();

        // Step 2: Verify in database
        assertTrue(volunteerRepository.findById(volunteerId).isPresent());

        Long campaignId1 = campaign1.getId();
        Long campaignId2 = campaign2.getId();

        // Step 3: Assign to campaign 1
        CampaignAssignmentRequest assign1 = new CampaignAssignmentRequest();
        assign1.setCampaignId(campaignId1);

        mockMvc.perform(post("/volunteers/{id}/campaigns", volunteerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assign1)))
                .andExpect(status().isOk());

        // Step 4: Assign to campaign 2
        CampaignAssignmentRequest assign2 = new CampaignAssignmentRequest();
        assign2.setCampaignId(campaignId2);

        mockMvc.perform(post("/volunteers/{id}/campaigns", volunteerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assign2)))
                .andExpect(status().isOk());

        // Step 5: Verify volunteer has 2 campaigns
        mockMvc.perform(get("/volunteers/{id}/campaigns", volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Step 6: Search by name
        mockMvc.perform(get("/volunteers/search/nombre/{nombre}", "Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Step 7: Search by any field (email)
        mockMvc.perform(get("/volunteers/search/{search}", "carlos@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Step 8: Get volunteers by campaign
        mockMvc.perform(get("/volunteers/campaigns/{campaignId}", campaignId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("carlos@example.com")));

        // Step 9: Remove from campaign 1
        mockMvc.perform(delete("/volunteers/{volunteerId}/campaigns/{campaignId}", volunteerId, campaignId1))
                .andExpect(status().isOk());

        // Step 10: Verify only 1 campaign remains
        mockMvc.perform(get("/volunteers/{id}/campaigns", volunteerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Step 11: Delete volunteer
        mockMvc.perform(delete("/volunteers/{id}", volunteerId))
                .andExpect(status().isNoContent());

        // Step 12: Verify deletion
        assertFalse(volunteerRepository.findById(volunteerId).isPresent());
        mockMvc.perform(get("/volunteers/{id}", volunteerId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Business rule: duplicate email prevents creation")
    void duplicateEmailPrevention() throws Exception {
        VolunteerRequest request = new VolunteerRequest();
        request.setNombre("Ana");
        request.setApellido("Lopez");
        request.setEmail("ana@example.com");
        request.setTelefono("+56922222222");

        mockMvc.perform(post("/volunteers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/volunteers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Volunteer count and empty state")
    void countAndEmptyState() throws Exception {
        mockMvc.perform(get("/volunteers"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/volunteers/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0)));

        VolunteerRequest request = new VolunteerRequest();
        request.setNombre("Pedro");
        request.setApellido("Gonzalez");
        request.setEmail("pedro@example.com");
        request.setTelefono("+56933333333");

        mockMvc.perform(post("/volunteers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/volunteers/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }
}
