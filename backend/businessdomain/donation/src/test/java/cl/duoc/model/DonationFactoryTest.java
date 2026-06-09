package cl.duoc.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DonationFactoryTest {

    @Test
    void createMonetaryDonation_ShouldReturnMonetaryDonation() {
        CampaignModel campaign = new CampaignModel();
        Donation donation = DonationFactory.createMonetaryDonation(campaign, "Donor", "Desc", 100.0, "USD");
        
        assertTrue(donation instanceof MonetaryDonation);
        assertEquals(100.0, ((MonetaryDonation)donation).getAmount());
        assertEquals("USD", ((MonetaryDonation)donation).getCurrency());
    }

    @Test
    void createObjectDonation_ShouldReturnObjectDonation() {
        CampaignModel campaign = new CampaignModel();
        Donation donation = DonationFactory.createObjectDonation(campaign, "Donor", "Desc", "Laptop", "Tech", 500.0, 1);
        
        assertTrue(donation instanceof ObjectDonation);
        assertEquals("Laptop", ((ObjectDonation)donation).getObjectName());
        assertEquals("Tech", ((ObjectDonation)donation).getCategory());
    }

    @Test
    void createDonation_WithUnsupportedType_ShouldThrowException() {
        // placeholder
    }
}
