package cl.duoc.service;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    public CampaignModel createCampaign(CampaignModel campaign) {
        if (campaign.getEstado() == null) {
            campaign.setEstado(CampaignStatus.PLANNED);
        }
        return campaignRepository.save(campaign);
    }

    public Optional<CampaignModel> getCampaignById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        return campaignRepository.findById(id);
    }

    public List<CampaignModel> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public List<CampaignModel> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByEstado(status);
    }

    public CampaignModel updateCampaign(Long id, CampaignModel campaignDetails) {
        if (id == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        Optional<CampaignModel> optionalCampaign = campaignRepository.findById(id);
        if (optionalCampaign.isPresent()) {
            CampaignModel campaign = optionalCampaign.get();
            
            campaign.setNombre(campaignDetails.getNombre());
            campaign.setDescripcion(campaignDetails.getDescripcion());
            campaign.setEstado(campaignDetails.getEstado());
            campaign.setFechaInicio(campaignDetails.getFechaInicio());
            campaign.setFechaFin(campaignDetails.getFechaFin());
            
            return campaignRepository.save(campaign);
        }
        return null;
    }

    public boolean deleteCampaign(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        if (campaignRepository.existsById(id)) {
            campaignRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean canReceiveDonations(Long campaignId) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        Optional<CampaignModel> campaign = campaignRepository.findById(campaignId);
        return campaign.map(CampaignModel::canReceiveDonations).orElse(false);
    }

    public CampaignModel changeStatus(Long id, CampaignStatus newStatus) {
        if (id == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        Optional<CampaignModel> optionalCampaign = campaignRepository.findById(id);
        if (optionalCampaign.isPresent()) {
            CampaignModel campaign = optionalCampaign.get();
            campaign.setEstado(newStatus);
            return campaignRepository.save(campaign);
        }
        return null;
    }
}
