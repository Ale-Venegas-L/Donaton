package cl.duoc.repository;

import cl.duoc.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    
    Optional<Volunteer> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT v FROM Volunteer v WHERE v.nombre LIKE %:nombre%")
    List<Volunteer> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT v FROM Volunteer v WHERE v.apellido LIKE %:apellido%")
    List<Volunteer> findByApellidoContaining(@Param("apellido") String apellido);
    
    @Query("SELECT v FROM Volunteer v WHERE v.nombre LIKE %:search% OR v.apellido LIKE %:search% OR v.email LIKE %:search%")
    List<Volunteer> findByAnyFieldContaining(@Param("search") String search);
    
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Volunteer v WHERE v.id = :volunteerId AND :campaignId MEMBER OF v.campaigns")
    boolean isVolunteerInCampaign(@Param("volunteerId") Long volunteerId, @Param("campaignId") Long campaignId);

    @Query("SELECT v FROM Volunteer v JOIN v.campaigns c WHERE c.id = :campaignId")
    List<Volunteer> findByCampaignId(@Param("campaignId") Long campaignId);
}
