package cl.duoc.controller;

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
@Transactional
class VolunteerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @MockBean
    private CampaignFacade campaignFacade;

    private CampaignModel campaign;
    private Volunteer savedVolunteer;

    @BeforeEach
    void setUp() {
        volunteerRepository.deleteAll();
        entityManager.createNativeQuery("DELETE FROM volunteer_campaigns").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM campaign").executeUpdate();

        campaign = new CampaignModel();
        campaign.setNombre("Help Campaign");
        campaign.setDescripcion("Campaign description");
        campaign.setEstado(CampaignStatus.ACTIVE);
        entityManager.persist(campaign);
        entityManager.flush();

        Long campaignId = campaign.getId();
        when(campaignFacade.getCampaignById(campaignId)).thenReturn(Optional.of(campaign));
        when(campaignFacade.campaignExists(campaignId)).thenReturn(true);
        when(campaignFacade.getCampaignById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            if (id.equals(campaignId)) {
                return Optional.of(campaign);
            }
            return Optional.empty();
        });
        when(campaignFacade.canAcceptVolunteers(campaignId)).thenReturn(true);
        when(campaignFacade.isCampaignActive(campaignId)).thenReturn(true);

        Volunteer volunteer = new Volunteer();
        volunteer.setNombre("Juan");
        volunteer.setApellido("Perez");
        volunteer.setEmail("juan@example.com");
        volunteer.setTelefono("+56912345678");
        savedVolunteer = volunteerRepository.save(volunteer);
    }

    @Test
    void createVolunteer_ShouldReturn201() throws Exception {
        VolunteerRequest request = new VolunteerRequest();
        request.setNombre("Maria");
        request.setApellido("Gomez");
        request.setEmail("maria@example.com");
        request.setTelefono("+56987654321");

        mockMvc.perform(post("/volunteers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Maria")))
                .andExpect(jsonPath("$.email", is("maria@example.com")));
    }

    @Test
    void createVolunteer_WhenEmailDuplicated_ShouldReturn400() throws Exception {
        VolunteerRequest request = new VolunteerRequest();
        request.setNombre("Another");
        request.setApellido("User");
        request.setEmail("juan@example.com");
        request.setTelefono("+56911111111");

        mockMvc.perform(post("/volunteers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllVolunteers_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")));
    }

    @Test
    void getAllVolunteers_WhenEmpty_ShouldReturn204() throws Exception {
        volunteerRepository.deleteAll();
        mockMvc.perform(get("/volunteers"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getVolunteerById_WhenExists_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/volunteers/{id}", savedVolunteer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan")));
    }

    @Test
    void getVolunteerById_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/volunteers/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVolunteerByEmail_WhenExists_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/volunteers/email/{email}", "juan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan")));
    }

    @Test
    void updateVolunteer_WhenExists_ShouldReturn200() throws Exception {
        VolunteerRequest update = new VolunteerRequest();
        update.setNombre("Juan Updated");
        update.setApellido("Perez");
        update.setEmail("juan.updated@example.com");
        update.setTelefono("+56912345678");

        mockMvc.perform(put("/volunteers/{id}", savedVolunteer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan Updated")));
    }

    @Test
    void updateVolunteer_WhenNotExists_ShouldReturn404() throws Exception {
        VolunteerRequest update = new VolunteerRequest();
        update.setNombre("No one");
        update.setApellido("No one");
        update.setEmail("noone@example.com");
        update.setTelefono("+56900000000");

        mockMvc.perform(put("/volunteers/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVolunteer_WhenExists_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/volunteers/{id}", savedVolunteer.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVolunteer_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/volunteers/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignVolunteerToCampaign_ShouldReturn200() throws Exception {
        CampaignAssignmentRequest assignment = new CampaignAssignmentRequest();
        assignment.setCampaignId(campaign.getId());

        mockMvc.perform(post("/volunteers/{volunteerId}/campaigns", savedVolunteer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignment)))
                .andExpect(status().isOk());
    }

    @Test
    void assignVolunteerToCampaign_WhenCampaignNotFound_ShouldReturn400() throws Exception {
        CampaignAssignmentRequest assignment = new CampaignAssignmentRequest();
        assignment.setCampaignId(999L);

        mockMvc.perform(post("/volunteers/{volunteerId}/campaigns", savedVolunteer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getVolunteerCampaigns_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/volunteers/{volunteerId}/campaigns", savedVolunteer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getVolunteerCount_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/volunteers/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }
}
