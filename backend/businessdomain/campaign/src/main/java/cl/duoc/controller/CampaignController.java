package cl.duoc.controller;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import cl.duoc.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;
    
    @Operation(summary = "Crear campaña nueva")
    @ApiResponse(responseCode = "201", description = "Campaña creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CampaignModel.class)))
    @ApiResponse(responseCode = "400", description = "Datos de la campaña inválidos" )
    @PostMapping
    public ResponseEntity<CampaignModel> createCampaign(@Valid @RequestBody CampaignModel campaign) {
        CampaignModel createdCampaign = campaignService.createCampaign(campaign);
        return new ResponseEntity<>(createdCampaign, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todas las campañas")
    @ApiResponse(responseCode = "200", description = "Lista de campañas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "404", description = "Campañas no encontradas", content=@Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping
    public ResponseEntity<List<CampaignModel>> getAllCampaigns() {
        List<CampaignModel> campaigns = campaignService.getAllCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    @Operation(summary = "Obtener campaña por ID")
    @ApiResponse(responseCode = "200", description = "Campaña obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CampaignModel.class)))
    @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<CampaignModel> getCampaignById(@PathVariable Long id) {
        Optional<CampaignModel> campaign = campaignService.getCampaignById(id);
        return campaign.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener campañas por estado")
    @ApiResponse(responseCode = "200", description = "Lista de campañas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "404", description = "Campañas no encontradas", content=@Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CampaignModel>> getCampaignsByStatus(@PathVariable CampaignStatus status) {
        List<CampaignModel> campaigns = campaignService.getCampaignsByStatus(status);
        return ResponseEntity.ok(campaigns);
    }

    @Operation(summary = "Actualizar campaña")
    @ApiResponse(responseCode = "200", description = "Campaña actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CampaignModel.class)))
    @ApiResponse(responseCode = "404", description = "Campaña no encontrada", content=@Content)
    @ApiResponse(responseCode = "400", description = "Datos de la campaña inválidos", content=@Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content=@Content)
    @PutMapping("/{id}")
    public ResponseEntity<CampaignModel> updateCampaign(@PathVariable Long id, @Valid @RequestBody CampaignModel campaignDetails) {
        CampaignModel updatedCampaign = campaignService.updateCampaign(id, campaignDetails);
        if (updatedCampaign != null) {
            return ResponseEntity.ok(updatedCampaign);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Actualizar estado de campaña")
    @ApiResponse(responseCode = "200", description = "Estado de campaña actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CampaignModel.class)))
    @ApiResponse(responseCode = "404", description = "Campaña no encontrada", content=@Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content=@Content)
    @PutMapping("/{id}/status")
    public ResponseEntity<CampaignModel> changeCampaignStatus(@PathVariable Long id, @RequestParam CampaignStatus status) {
        CampaignModel updatedCampaign = campaignService.changeStatus(id, status);
        if (updatedCampaign != null) {
            return ResponseEntity.ok(updatedCampaign);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Verificar si una campaña puede recibir donaciones")
    @ApiResponse(responseCode = "200", description = "Verificación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description = "Campaña no encontrada", content=@Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content=@Content)
    @GetMapping("/{id}/can-donate")
    public ResponseEntity<Boolean> canReceiveDonations(@PathVariable Long id) {
        boolean canDonate = campaignService.canReceiveDonations(id);
        return ResponseEntity.ok(canDonate);
    }

    @Operation(summary = "Eliminar campaña")
    @ApiResponse(responseCode = "204", description = "Campaña eliminada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)))
    @ApiResponse(responseCode = "404", description = "Campaña no encontrada", content=@Content)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content=@Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        boolean deleted = campaignService.deleteCampaign(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
