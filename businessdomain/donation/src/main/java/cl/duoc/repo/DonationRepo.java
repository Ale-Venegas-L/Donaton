package cl.duoc.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.model.Donation;

public interface DonationRepo extends JpaRepository<Donation, Long> {

}
