package cl.duoc.service;

import cl.duoc.model.Volunteer;
import cl.duoc.model.CampaignModel;
import cl.duoc.repository.VolunteerRepository;
import cl.duoc.repository.CampaignRepository;
import cl.duoc.facade.CampaignFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class VolunteerService {

    @Autowired
    private VolunteerRepository volunteerRepository;
    
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignFacade campaignFacade;

    public Volunteer createVolunteer(Volunteer volunteer) {
        if (volunteerRepository.existsByEmail(volunteer.getEmail())) {
            throw new IllegalArgumentException("Ya existe un voluntario con el email: " + volunteer.getEmail());
        }
        return volunteerRepository.save(volunteer);
    }

    public Optional<Volunteer> getVolunteerById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Volunteer ID cannot be null");
        }
        return volunteerRepository.findById(id);
    }

    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    public Optional<Volunteer> getVolunteerByEmail(String email) {
        return volunteerRepository.findByEmail(email);
    }

    public List<Volunteer> searchVolunteersByName(String nombre) {
        return volunteerRepository.findByNombreContaining(nombre);
    }

    public List<Volunteer> searchVolunteersByLastName(String apellido) {
        return volunteerRepository.findByApellidoContaining(apellido);
    }

    public List<Volunteer> searchVolunteersByAnyField(String search) {
        return volunteerRepository.findByAnyFieldContaining(search);
    }

    public Volunteer updateVolunteer(Long id, Volunteer volunteerDetails) {
        if (id == null) {
            throw new IllegalArgumentException("Volunteer ID cannot be null");
        }
        if (volunteerDetails == null) {
            throw new IllegalArgumentException("Volunteer details cannot be null");
        }
        Optional<Volunteer> optionalVolunteer = volunteerRepository.findById(id);
        if (optionalVolunteer.isPresent()) {
            Volunteer volunteer = optionalVolunteer.get();
            
            // Check if email is being changed and if new email already exists
            if (!volunteer.getEmail().equals(volunteerDetails.getEmail()) && 
                volunteerRepository.existsByEmail(volunteerDetails.getEmail())) {
                throw new IllegalArgumentException("Ya existe un voluntario con el email: " + volunteerDetails.getEmail());
            }
            
            volunteer.setNombre(volunteerDetails.getNombre());
            volunteer.setApellido(volunteerDetails.getApellido());
            volunteer.setEmail(volunteerDetails.getEmail());
            volunteer.setTelefono(volunteerDetails.getTelefono());
            volunteer.setDireccion(volunteerDetails.getDireccion());
            
            return volunteerRepository.save(volunteer);
        }
        return null;
    }

    public boolean deleteVolunteer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Volunteer ID cannot be null");
        }
        if (volunteerRepository.existsById(id)) {
            volunteerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Volunteer addCampaignToVolunteer(Long volunteerId, Long campaignId) {
        if (volunteerId == null) {
            throw new IllegalArgumentException("Volunteer ID cannot be null");
        }
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(volunteerId);
        if (volunteerOpt.isEmpty()) {
            throw new IllegalArgumentException("Voluntario no encontrado con ID: " + volunteerId);
        }

        // Verify campaign exists through facade
        if (!campaignFacade.campaignExists(campaignId)) {
            throw new IllegalArgumentException("Campaña no encontrada con ID: " + campaignId);
        }

        Volunteer volunteer = volunteerOpt.get();
        Optional<CampaignModel> campaignOpt = campaignFacade.getCampaignById(campaignId);

        if (campaignOpt.isEmpty()) {
            throw new IllegalArgumentException("No se pudo obtener la información de la campaña");
        }

        CampaignModel remoteCampaign = campaignOpt.get();
        Long campaignIdValue = remoteCampaign.getId();
        CampaignModel reference = campaignRepository.findById(campaignIdValue)
                .orElseGet(() -> campaignRepository.saveAndFlush(remoteCampaign));

        volunteer.getCampaigns().add(reference);
        return volunteerRepository.save(volunteer);
    }

    public Volunteer removeCampaignFromVolunteer(Long volunteerId, Long campaignId) {
        if (volunteerId == null) {
            throw new IllegalArgumentException("Volunteer ID cannot be null");
        }
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(volunteerId);
        if (volunteerOpt.isEmpty()) {
            throw new IllegalArgumentException("Voluntario no encontrado con ID: " + volunteerId);
        }

        Volunteer volunteer = volunteerOpt.get();
        volunteer.getCampaigns().removeIf(campaign -> campaign.getId().equals(campaignId));
        
        return volunteerRepository.save(volunteer);
    }

    public Set<CampaignModel> getVolunteerCampaigns(Long volunteerId) {
        if (volunteerId == null) {
            throw new IllegalArgumentException("Volunteer ID cannot be null");
        }
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(volunteerId);
        if (volunteerOpt.isPresent()) {
            return volunteerOpt.get().getCampaigns();
        }
        throw new IllegalArgumentException("Voluntario no encontrado con ID: " + volunteerId);
    }

    public List<Volunteer> getVolunteersByCampaign(Long campaignId) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        return volunteerRepository.findByCampaignId(campaignId);
    }
}
