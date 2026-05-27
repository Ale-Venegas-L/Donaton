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
import cl.duoc.model.Donation;
import cl.duoc.model.CampaignModel;
import cl.duoc.dto.DonationRequest;
import cl.duoc.dto.DonationUpdateRequest;
import cl.duoc.service.DonationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/donation")
@Tag(name = "Donation API", description = "API para gestionar donaciones")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @Operation(summary = "Obtener todas las donaciones", description = "Devuelve una lista de todas las donaciones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Donaciones encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay donaciones")
    })
    @GetMapping
    public ResponseEntity<List<Donation>> list() {
        List<Donation> findAll = donationService.listAllDonations();
        if (findAll.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(findAll);
    }

    @Operation(summary = "Contar donaciones")
    @GetMapping("/count")
    public ResponseEntity<Integer> getDonationCount() {
        return ResponseEntity.ok(donationService.listAllDonations().size());
    }

    @Operation(summary = "Contar donadores únicos")
    @GetMapping("/donors/count")
    public ResponseEntity<Integer> getDonorCount() {
        List<Donation> donations = donationService.listAllDonations();
        long uniqueDonors = donations.stream()
            .map(Donation::getDonorName)
            .filter(name -> name != null && !name.isEmpty())
            .distinct()
            .count();
        return ResponseEntity.ok((int) uniqueDonors);
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
        Optional<Donation> optionalDonation = donationService.findDonationById(id);
        return optionalDonation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
            Donation savedDonation = donationService.createDonation(request);
            return ResponseEntity.status(201).body(savedDonation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
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
        try {
            Donation updatedDonation = donationService.updateDonation(id, request);
            if (updatedDonation != null) {
                return ResponseEntity.ok(updatedDonation);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Eliminar donación", description = "Elimina una donación por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Donación eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Donación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Parameter(description = "ID de la donación a eliminar") @PathVariable("id") Long id) {
        try {
            boolean deleted = donationService.deleteDonation(id);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
}