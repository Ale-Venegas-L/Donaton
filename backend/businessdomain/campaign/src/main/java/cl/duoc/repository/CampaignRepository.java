package cl.duoc.repository;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignModel, Long> {
    
    List<CampaignModel> findByEstado(CampaignStatus estado);
    
    Optional<CampaignModel> findByIdAndEstado(Long id, CampaignStatus estado);
}
