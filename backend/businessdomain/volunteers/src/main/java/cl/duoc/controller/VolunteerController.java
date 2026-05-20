package cl.duoc.controller;

import cl.duoc.model.Volunteer;
import cl.duoc.model.CampaignModel;
import cl.duoc.service.VolunteerService;
import cl.duoc.dto.VolunteerRequest;
import cl.duoc.dto.CampaignAssignmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/volunteers")
@Tag(name = "Volunteer API", description = "API para gestión de voluntarios")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @Operation(summary = "Crear un nuevo voluntario", description = "Crea un nuevo voluntario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Voluntario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de voluntario inválidos o email duplicado")
    })
    @PostMapping
    public ResponseEntity<Volunteer> createVolunteer(@Valid @RequestBody VolunteerRequest request) {
        try {
            Volunteer volunteer = new Volunteer();
            volunteer.setNombre(request.getNombre());
            volunteer.setApellido(request.getApellido());
            volunteer.setEmail(request.getEmail());
            volunteer.setTelefono(request.getTelefono());
            volunteer.setDireccion(request.getDireccion());
            
            Volunteer createdVolunteer = volunteerService.createVolunteer(volunteer);
            return new ResponseEntity<>(createdVolunteer, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener todos los voluntarios", description = "Devuelve una lista de todos los voluntarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntarios encontrados"),
        @ApiResponse(responseCode = "204", description = "No hay voluntarios")
    })
    @GetMapping
    public ResponseEntity<List<Volunteer>> getAllVolunteers() {
        List<Volunteer> volunteers = volunteerService.getAllVolunteers();
        if (volunteers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(volunteers);
    }

    @Operation(summary = "Contar voluntarios")
    @GetMapping("/count")
    public ResponseEntity<Integer> getVolunteerCount() {
        return ResponseEntity.ok(volunteerService.getAllVolunteers().size());
    }

    @Operation(summary = "Obtener voluntario por ID", description = "Devuelve un voluntario específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntario encontrado"),
        @ApiResponse(responseCode = "404", description = "Voluntario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> getVolunteerById(@Parameter(description = "ID del voluntario") @PathVariable Long id) {
        Optional<Volunteer> volunteer = volunteerService.getVolunteerById(id);
        return volunteer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar voluntario por email", description = "Busca un voluntario por su dirección de email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntario encontrado"),
        @ApiResponse(responseCode = "404", description = "Voluntario no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Volunteer> getVolunteerByEmail(@Parameter(description = "Email del voluntario") @PathVariable String email) {
        Optional<Volunteer> volunteer = volunteerService.getVolunteerByEmail(email);
        return volunteer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar voluntarios por nombre", description = "Busca voluntarios cuyo nombre contenga el texto especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntarios encontrados"),
        @ApiResponse(responseCode = "204", description = "No hay voluntarios que coincidan")
    })
    @GetMapping("/search/nombre/{nombre}")
    public ResponseEntity<List<Volunteer>> searchByNombre(@Parameter(description = "Texto a buscar en el nombre") @PathVariable String nombre) {
        List<Volunteer> volunteers = volunteerService.searchVolunteersByName(nombre);
        if (volunteers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(volunteers);
    }

    @Operation(summary = "Buscar voluntarios por apellido", description = "Busca voluntarios cuyo apellido contenga el texto especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntarios encontrados"),
        @ApiResponse(responseCode = "204", description = "No hay voluntarios que coincidan")
    })
    @GetMapping("/search/apellido/{apellido}")
    public ResponseEntity<List<Volunteer>> searchByApellido(@Parameter(description = "Texto a buscar en el apellido") @PathVariable String apellido) {
        List<Volunteer> volunteers = volunteerService.searchVolunteersByLastName(apellido);
        if (volunteers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(volunteers);
    }

    @Operation(summary = "Buscar voluntarios por cualquier campo", description = "Busca voluntarios por nombre, apellido o email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntarios encontrados"),
        @ApiResponse(responseCode = "204", description = "No hay voluntarios que coincidan")
    })
    @GetMapping("/search/{search}")
    public ResponseEntity<List<Volunteer>> searchByAnyField(@Parameter(description = "Texto a buscar en cualquier campo") @PathVariable String search) {
        List<Volunteer> volunteers = volunteerService.searchVolunteersByAnyField(search);
        if (volunteers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(volunteers);
    }

    @Operation(summary = "Actualizar voluntario", description = "Actualiza la información de un voluntario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Voluntario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email duplicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Volunteer> updateVolunteer(
            @Parameter(description = "ID del voluntario a actualizar") @PathVariable Long id, 
            @Valid @RequestBody VolunteerRequest request) {
        try {
            Volunteer volunteerDetails = new Volunteer();
            volunteerDetails.setNombre(request.getNombre());
            volunteerDetails.setApellido(request.getApellido());
            volunteerDetails.setEmail(request.getEmail());
            volunteerDetails.setTelefono(request.getTelefono());
            volunteerDetails.setDireccion(request.getDireccion());
            
            Volunteer updatedVolunteer = volunteerService.updateVolunteer(id, volunteerDetails);
            if (updatedVolunteer != null) {
                return ResponseEntity.ok(updatedVolunteer);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Eliminar voluntario", description = "Elimina un voluntario del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Voluntario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Voluntario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteer(@Parameter(description = "ID del voluntario a eliminar") @PathVariable Long id) {
        boolean deleted = volunteerService.deleteVolunteer(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Asignar voluntario a campaña", description = "Asigna un voluntario a una campaña específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntario asignado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Voluntario o campaña no encontrados"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/{volunteerId}/campaigns")
    public ResponseEntity<Volunteer> assignToCampaign(
            @Parameter(description = "ID del voluntario") @PathVariable Long volunteerId,
            @Valid @RequestBody CampaignAssignmentRequest request) {
        try {
            Volunteer updatedVolunteer = volunteerService.addCampaignToVolunteer(volunteerId, request.getCampaignId());
            return ResponseEntity.ok(updatedVolunteer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Remover voluntario de campaña", description = "Remueve un voluntario de una campaña específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntario removido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Voluntario no encontrado")
    })
    @DeleteMapping("/{volunteerId}/campaigns/{campaignId}")
    public ResponseEntity<Volunteer> removeFromCampaign(
            @Parameter(description = "ID del voluntario") @PathVariable Long volunteerId,
            @Parameter(description = "ID de la campaña") @PathVariable Long campaignId) {
        Volunteer updatedVolunteer = volunteerService.removeCampaignFromVolunteer(volunteerId, campaignId);
        if (updatedVolunteer != null) {
            return ResponseEntity.ok(updatedVolunteer);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener campañas de voluntario", description = "Devuelve todas las campañas en las que participa un voluntario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campañas encontradas"),
        @ApiResponse(responseCode = "404", description = "Voluntario no encontrado")
    })
    @GetMapping("/{volunteerId}/campaigns")
    public ResponseEntity<Set<CampaignModel>> getVolunteerCampaigns(@Parameter(description = "ID del voluntario") @PathVariable Long volunteerId) {
        try {
            Set<CampaignModel> campaigns = volunteerService.getVolunteerCampaigns(volunteerId);
            return ResponseEntity.ok(campaigns);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener voluntarios de campaña", description = "Devuelve todos los voluntarios asignados a una campaña específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Voluntarios encontrados"),
        @ApiResponse(responseCode = "204", description = "No hay voluntarios en esta campaña")
    })
    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<List<Volunteer>> getVolunteersByCampaign(@Parameter(description = "ID de la campaña") @PathVariable Long campaignId) {
        List<Volunteer> volunteers = volunteerService.getVolunteersByCampaign(campaignId);
        if (volunteers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(volunteers);
    }
}
