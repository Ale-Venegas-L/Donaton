package cl.duoc.service;

import cl.duoc.dto.DonationRequest;
import cl.duoc.dto.DonationUpdateRequest;
import cl.duoc.facade.CampaignFacade;
import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.model.Donation;
import cl.duoc.model.MonetaryDonation;
import cl.duoc.model.ObjectDonation;
import cl.duoc.model.DonationFactory;
import cl.duoc.model.DonationFactory.DonationType;
import cl.duoc.repo.DonationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DonationService {

    @Autowired
    private DonationRepo donationRepo;

    @Autowired
    private CampaignFacade campaignFacade;

    public List<Donation> listAllDonations() {
        List<Donation> donations = donationRepo.findAll();
        for (Donation donation : donations) {
            populateCampaign(donation);
        }
        return donations;
    }

    public Optional<Donation> findDonationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Donation ID cannot be null");
        }
        Optional<Donation> donation = donationRepo.findById(id);
        donation.ifPresent(this::populateCampaign);
        return donation;
    }

    private void populateCampaign(Donation donation) {
        try {
            if (donation.getCampaignId() != null) {
                Optional<CampaignModel> campaignOpt = campaignFacade.getCampaignById(donation.getCampaignId());
                campaignOpt.ifPresent(donation::setCampaign);
            }
        } catch (Exception ignored) {
        }
    }

    public Donation createDonation(DonationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Donation request cannot be null");
        }

        Optional<CampaignModel> campaignOpt = campaignFacade.getCampaignById(request.getCampaignId());
        if (campaignOpt.isEmpty()) {
            throw new IllegalArgumentException("Campaign does not exist or is not accessible");
        }

        CampaignModel campaign = campaignOpt.get();
        
        if (campaign.getEstado() != CampaignStatus.ACTIVE) {
            throw new IllegalArgumentException("Campaign cannot receive donations - status is " + campaign.getEstado());
        }

        Donation donation = createDonationFromRequest(request, campaign);
        return donationRepo.save(donation);
    }

    public Donation updateDonation(Long id, DonationUpdateRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Donation ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Donation update request cannot be null");
        }
        Optional<Donation> optionalDonation = donationRepo.findById(id);
        if (optionalDonation.isEmpty()) {
            return null;
        }
        Donation donation = optionalDonation.get();

        if (request.getDonorName() != null) {
            donation.setDonorName(request.getDonorName());
        }
        if (request.getDescription() != null) {
            donation.setDescription(request.getDescription());
        }
        if (request.getCampaignId() != null) {
            Optional<CampaignModel> campaignOpt = campaignFacade.getCampaignById(request.getCampaignId());
            if (campaignOpt.isPresent()) {
                donation.setCampaignId(request.getCampaignId());
                donation.setCampaign(campaignOpt.get());
            }
        }
        if (donation instanceof MonetaryDonation md) {
            if (request.getAmount() != null) md.setAmount(request.getAmount());
            if (request.getCurrency() != null) md.setCurrency(request.getCurrency());
        } else if (donation instanceof ObjectDonation od) {
            if (request.getObjectName() != null) od.setObjectName(request.getObjectName());
            if (request.getCategory() != null) od.setCategory(request.getCategory());
            if (request.getEstimatedValue() != null) od.setEstimatedValue(request.getEstimatedValue());
            if (request.getQuantity() != null) od.setQuantity(request.getQuantity());
        }
        return donationRepo.save(donation);
    }

    public boolean deleteDonation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Donation ID cannot be null");
        }
        if (donationRepo.existsById(id)) {
            donationRepo.deleteById(id);
            return true;
        }
        return false;
    }

    private Donation createDonationFromRequest(DonationRequest request, CampaignModel campaign) {
        DonationType type = DonationType.valueOf(request.getType().toUpperCase());
        return switch (type) {
            case MONETARY -> {
                if (request.getAmount() == null || request.getCurrency() == null) {
                    throw new IllegalArgumentException("Monetary donations require amount and currency");
                }
                yield DonationFactory.createMonetaryDonation(
                    campaign,
                    request.getDonorName(),
                    request.getDescription(),
                    request.getAmount(),
                    request.getCurrency()
                );
            }
            case OBJECT -> {
                if (request.getObjectName() == null || request.getCategory() == null ||
                        request.getEstimatedValue() == null || request.getQuantity() == null) {
                    throw new IllegalArgumentException("Object donations require objectName, category, estimatedValue, and quantity");
                }
                yield DonationFactory.createObjectDonation(
                    campaign,
                    request.getDonorName(),
                    request.getDescription(),
                    request.getObjectName(),
                    request.getCategory(),
                    request.getEstimatedValue(),
                    request.getQuantity()
                );
            }
            default -> throw new IllegalArgumentException("Unsupported donation type: " + type);
        };
    }
}
