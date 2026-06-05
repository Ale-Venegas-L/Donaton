package cl.duoc.service;

import cl.duoc.model.Volunteer;
import cl.duoc.model.CampaignModel;
import cl.duoc.repository.VolunteerRepository;
import cl.duoc.facade.CampaignFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolunteerServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private CampaignFacade campaignFacade;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer volunteer;
    private CampaignModel campaign;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setNombre("Juan");
        volunteer.setApellido("Perez");
        volunteer.setEmail("juan@example.com");
        volunteer.setCampaigns(new HashSet<>());

        campaign = new CampaignModel();
        campaign.setId(10L);
        campaign.setNombre("Help the world");
    }

    @Test
    void createVolunteer_WhenEmailUnique_ShouldSave() {
        when(volunteerRepository.existsByEmail(volunteer.getEmail())).thenReturn(false);
        when(volunteerRepository.save(any())).thenReturn(volunteer);

        Volunteer saved = volunteerService.createVolunteer(volunteer);

        assertNotNull(saved);
        verify(volunteerRepository).save(volunteer);
    }

    @Test
    void createVolunteer_WhenEmailExists_ShouldThrowException() {
        when(volunteerRepository.existsByEmail(volunteer.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> volunteerService.createVolunteer(volunteer));
    }

    @Test
    void updateVolunteer_WhenEmailChangedAndUnique_ShouldUpdate() {
        Volunteer details = new Volunteer();
        details.setNombre("Juan Updated");
        details.setApellido("Perez");
        details.setEmail("juan.updated@example.com");

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(volunteerRepository.existsByEmail(details.getEmail())).thenReturn(false);
        when(volunteerRepository.save(any())).thenReturn(volunteer);

        Volunteer updated = volunteerService.updateVolunteer(1L, details);

        assertNotNull(updated);
        assertEquals("Juan Updated", updated.getNombre());
        verify(volunteerRepository).save(volunteer);
    }

    @Test
    void updateVolunteer_WhenEmailChangedAndExists_ShouldThrowException() {
        Volunteer details = new Volunteer();
        details.setNombre("Juan");
        details.setApellido("Perez");
        details.setEmail("existing@example.com");

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(volunteerRepository.existsByEmail(details.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> volunteerService.updateVolunteer(1L, details));
    }

    @Test
    void addCampaignToVolunteer_WhenBothExist_ShouldAddAndSave() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(campaignFacade.campaignExists(10L)).thenReturn(true);
        when(campaignFacade.getCampaignById(10L)).thenReturn(Optional.of(campaign));
        when(volunteerRepository.save(any())).thenReturn(volunteer);

        Volunteer result = volunteerService.addCampaignToVolunteer(1L, 10L);

        assertNotNull(result);
        assertTrue(result.getCampaigns().contains(campaign));
        verify(volunteerRepository).save(volunteer);
    }

    @Test
    void addCampaignToVolunteer_WhenCampaignNotFound_ShouldThrowException() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(campaignFacade.campaignExists(10L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> volunteerService.addCampaignToVolunteer(1L, 10L));
    }

    @Test
    void removeCampaignFromVolunteer_ShouldRemoveAndSave() {
        volunteer.getCampaigns().add(campaign);
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(volunteerRepository.save(any())).thenReturn(volunteer);

        Volunteer result = volunteerService.removeCampaignFromVolunteer(1L, 10L);

        assertNotNull(result);
        assertFalse(result.getCampaigns().contains(campaign));
        verify(volunteerRepository).save(volunteer);
    }

    @Test
    void getVolunteersByCampaign_ShouldReturnList() {
        List<Volunteer> list = Collections.singletonList(volunteer);
        when(volunteerRepository.findByCampaignId(10L)).thenReturn(list);

        List<Volunteer> result = volunteerService.getVolunteersByCampaign(10L);

        assertEquals(1, result.size());
        assertEquals(volunteer, result.get(0));
    }
}
