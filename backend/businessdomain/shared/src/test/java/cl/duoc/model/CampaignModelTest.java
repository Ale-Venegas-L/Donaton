package cl.duoc.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CampaignModelTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenNomeEmptyThenValidationFails() {
        CampaignModel campaign = new CampaignModel();
        campaign.setNombre("");
        campaign.setDescripcion("Description");
        campaign.setEstado(CampaignStatus.ACTIVE);

        Set<ConstraintViolation<CampaignModel>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El nombre de la campaña no puede estar vacío")));
    }

    @Test
    void whenDescriptionEmptyThenValidationFails() {
        CampaignModel campaign = new CampaignModel();
        campaign.setNombre("Name");
        campaign.setDescripcion("");
        campaign.setEstado(CampaignStatus.ACTIVE);

        Set<ConstraintViolation<CampaignModel>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La descripción de la campaña no puede estar vacía")));
    }

    @Test
    void whenEstadoNullThenValidationFails() {
        CampaignModel campaign = new CampaignModel();
        campaign.setNombre("Name");
        campaign.setDescripcion("Description");
        campaign.setEstado(null);

        Set<ConstraintViolation<CampaignModel>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El estado de la campaña es obligatorio")));
    }

    @ParameterizedTest
    @EnumSource(CampaignStatus.class)
    void testCanReceiveDonations(CampaignStatus status) {
        CampaignModel campaign = new CampaignModel();
        campaign.setEstado(status);

        boolean canReceive = campaign.canReceiveDonations();

        if (status == CampaignStatus.ACTIVE) {
            assertTrue(canReceive, "Campaign should receive donations when ACTIVE");
        } else {
            assertFalse(canReceive, "Campaign should NOT receive donations when " + status);
        }
    }
}
