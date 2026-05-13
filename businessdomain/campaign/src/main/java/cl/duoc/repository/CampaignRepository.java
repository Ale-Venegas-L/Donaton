package cl.duoc.repository;

import cl.duoc.model.CampaignModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignModel, Long> {
    
    List<CampaignModel> findByEstado(cl.duoc.model.CampaignStatus estado);
    
    Optional<CampaignModel> findByIdAndEstado(Long id, cl.duoc.model.CampaignStatus estado);
}
