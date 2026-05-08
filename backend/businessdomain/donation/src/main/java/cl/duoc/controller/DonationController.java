package cl.duoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import cl.duoc.repo.DonationRepo;
import cl.duoc.model.Donation;
import cl.duoc.model.DonationFactory;
import cl.duoc.model.DonationFactory.DonationType;
import cl.duoc.model.CampaignModel;
import cl.duoc.dto.DonationRequest;
import cl.duoc.dto.DonationUpdateRequest;
import cl.duoc.facade.CampaignFacade;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/donation")
@Tag(name = "Donation API", description = "API para gestionar donaciones")
public class DonationController {

    @Autowired
    private DonationRepo donationRepo;

    @Autowired
    private CampaignFacade campaignFacade;

    @Operation(summary = "Obtener todas las donaciones", description = "Devuelve una lista de todas las donaciones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Donaciones encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay donaciones")
    })
    @GetMapping
    public ResponseEntity<List<Donation>> list() {
        List<Donation> findAll = donationRepo.findAll();
        if (findAll.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(findAll);
    }

    @Operation(summary = "Obtener donación por ID", description = "Devuelve una donación específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Donación encontrada"),
        @ApiResponse(responseCode = "404", description = "Donación no encontrada"),
        @ApiResponse(responseCode = "503", description = "Servicio no disponible - circuit breaker activado")
    })
    @GetMapping("/{id}")
    @CircuitBreaker(name = "donation", fallbackMethod = "getDonationFallback")
    public ResponseEntity<Donation> get(@Parameter(description = "ID of the donation to retrieve") @PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Donation ID cannot be null");
        }
        Optional<Donation> optionalDonation = donationRepo.findById(id);
        if (optionalDonation.isPresent()) {
            Donation donation = optionalDonation.get();            
            return ResponseEntity.ok(donation);
        }else
        return ResponseEntity.notFound().build();
    }
    
    public ResponseEntity<Donation> getDonationFallback(Long id, Throwable ex) {
        CampaignModel dummyCampaign = new CampaignModel();
        dummyCampaign.setId(0L);
        dummyCampaign.setNombre("Unknown");
        dummyCampaign.setDescripcion("Fallback campaign");
        
        return ResponseEntity.ok(new Donation(dummyCampaign, "Unknown", "Fallback donation") {
            @Override
            public String getType() {
                return "UNKNOWN";
            }
            
            @Override
            public double getValue() {
                return 0.0;
            }
        });
    }

    @Operation(summary = "Crear donación", description = "Crea una nueva donación (monetaria u objeto)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Donación creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de donación inválidos")
    })
    @PostMapping
    public ResponseEntity<Donation> create(@Parameter(description = "Información de la donación") @Valid @RequestBody DonationRequest request) {
        try {
            // Validar que la campaña existe y puede recibir donaciones
            if (!campaignFacade.campaignExists(request.getCampaignId())) {
                return ResponseEntity.badRequest().build();
            }
            
            if (!campaignFacade.canReceiveDonations(request.getCampaignId())) {
                return ResponseEntity.badRequest().build();
            }
            
            // Obtener la campaña
            Optional<CampaignModel> campaignOpt = campaignFacade.getCampaignById(request.getCampaignId());
            if (campaignOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Donation donation = createDonationFromRequest(request, campaignOpt.get());
            if (donation == null) {
                return ResponseEntity.badRequest().build();
            }
            Donation savedDonation = donationRepo.save(donation);
            return ResponseEntity.status(201).body(savedDonation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private Donation createDonationFromRequest(DonationRequest request, CampaignModel campaign) {
        DonationType type = DonationType.valueOf(request.getType().toUpperCase());
        
        switch (type) {
            case MONETARY:
                if (request.getAmount() == null || request.getCurrency() == null) {
                    throw new IllegalArgumentException("Monetary donations require amount and currency");
                }
                return DonationFactory.createMonetaryDonation(
                    campaign,
                    request.getDonorName(), 
                    request.getDescription(), 
                    request.getAmount(), 
                    request.getCurrency()
                );
                
            case OBJECT:
                if (request.getObjectName() == null || request.getCategory() == null || 
                    request.getEstimatedValue() == null || request.getQuantity() == null) {
                    throw new IllegalArgumentException("Object donations require objectName, category, estimatedValue, and quantity");
                }
                return DonationFactory.createObjectDonation(
                    campaign,
                    request.getDonorName(), 
                    request.getDescription(), 
                    request.getObjectName(), 
                    request.getCategory(), 
                    request.getEstimatedValue(), 
                    request.getQuantity()
                );
                
            default:
                throw new IllegalArgumentException("Unsupported donation type: " + type);
        }
    }

    @Operation(summary = "Actualizar donación", description = "Actualiza una donación existente con nueva información")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Donación actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Donación no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de donación inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Donation> update(
            @Parameter(description = "ID de la donación a actualizar") @PathVariable("id") Long id, 
            @Parameter(description = "Información actualizada de la donación") @Valid @RequestBody DonationUpdateRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Donation ID cannot be null");
        }
        Optional<Donation> optionalDonation = donationRepo.findById(id);
        if (optionalDonation.isPresent()) {
            Donation donationToUpdate = optionalDonation.get();
            donationToUpdate.setDonorName(request.getDonorName());
            donationToUpdate.setDescription(request.getDescription());
            donationRepo.save(donationToUpdate);
            return ResponseEntity.ok(donationToUpdate);
        }
        return ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "Eliminar donación", description = "Elimina una donación por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Donación eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Donación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Parameter(description = "ID de la donación a eliminar") @PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Donation ID cannot be null");
        }
        Optional<Donation> donation = donationRepo.findById(id);
        if (donation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        donationRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
}