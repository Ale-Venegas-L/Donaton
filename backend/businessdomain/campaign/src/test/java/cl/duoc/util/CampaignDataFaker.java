package cl.duoc.util;

import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@Component
public class CampaignDataFaker {
    
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    
    public static CampaignModel createFakeCampaign() {
        return createFakeCampaign(null);
    }
    
    public static CampaignModel createFakeCampaign(CampaignStatus status) {
        CampaignModel campaign = new CampaignModel();
        
        // Generate realistic campaign name
        String[] campaignTypes = {"Educación", "Salud", "Medio Ambiente", "Comunidad", "Infancia", "Ancianos", "Animales", "Arte y Cultura"};
        String type = campaignTypes[random.nextInt(campaignTypes.length)];
        String actionWord = faker.lorem().word();
        String action = actionWord.substring(0, 1).toUpperCase() + actionWord.substring(1);
        campaign.setNombre("Campaña de " + type + " " + action + " 2024");
        
        // Generate realistic description
        campaign.setDescripcion(generateCampaignDescription(type));
        
        // Set status (random if not specified)
        if (status == null) {
            CampaignStatus[] statuses = CampaignStatus.values();
            campaign.setEstado(statuses[random.nextInt(statuses.length)]);
        } else {
            campaign.setEstado(status);
        }
        
        // Generate dates based on status
        generateDatesForCampaign(campaign);
        
        return campaign;
    }
    
    public static CampaignModel createFakeActiveCampaign() {
        return createFakeCampaign(CampaignStatus.ACTIVE);
    }
    
    public static CampaignModel createFakePlannedCampaign() {
        return createFakeCampaign(CampaignStatus.PLANNED);
    }
    
    public static CampaignModel createFakeCompletedCampaign() {
        return createFakeCampaign(CampaignStatus.COMPLETED);
    }
    
    public static CampaignModel createFakeCancelledCampaign() {
        return createFakeCampaign(CampaignStatus.CANCELLED);
    }
    
    private static String generateCampaignDescription(String type) {
        String[] descriptions = {
            "Ayudemos a mejorar la calidad de vida de nuestra comunidad través de esta iniciativa.",
            "Tu apoyo puede marcar la diferencia en la vida de muchas personas.",
            "Juntos podemos lograr un cambio positivo y duradero en nuestra sociedad.",
            "Esta campaña busca brindar oportunidades y esperanza a quienes más lo necesitan.",
            "Con tu generosidad, podremos continuar con nuestra labor de ayuda social."
        };
        
        String baseDescription = descriptions[random.nextInt(descriptions.length)];
        
        switch (type) {
            case "Educación":
                return baseDescription + " Fomentamos el acceso a la educación de calidad para niños y jóvenes.";
            case "Salud":
                return baseDescription + " Brindamos atención médica y medicamentos a personas de escasos recursos.";
            case "Medio Ambiente":
                return baseDescription + " Trabajamos por un planeta más limpio y sostenible para las futuras generaciones.";
            case "Comunidad":
                return baseDescription + " Fortalecemos los lazos comunitarios y promovemos la participación ciudadana.";
            case "Infancia":
                return baseDescription + " Protegemos y cuidamos el bienestar de los niños más vulnerables.";
            case "Ancianos":
                return baseDescription + " Acompañamos y asistimos a nuestros adultos mayores en sus necesidades diarias.";
            case "Animales":
                return baseDescription + " Rescatamos y cuidamos de animales abandonados y maltratados.";
            case "Arte y Cultura":
                return baseDescription + " Promovemos el acceso a la cultura y las expresiones artísticas en todos los sectores.";
            default:
                return baseDescription;
        }
    }
    
    private static void generateDatesForCampaign(CampaignModel campaign) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate;
        
        switch (campaign.getEstado()) {
            case CampaignStatus.PLANNED:
                // Future campaign
                startDate = now.plusDays(random.nextInt(30) + 1);
                endDate = startDate.plusDays(random.nextInt(90) + 30);
                break;
            case CampaignStatus.ACTIVE:
                // Currently running campaign
                startDate = now.minusDays(random.nextInt(30) + 1);
                endDate = now.plusDays(random.nextInt(60) + 1);
                break;
            case CampaignStatus.COMPLETED:
                // Past campaign
                endDate = now.minusDays(random.nextInt(30) + 1);
                startDate = endDate.minusDays(random.nextInt(90) + 30);
                break;
            case CampaignStatus.CANCELLED:
                // Cancelled campaign (could be past or future)
                if (random.nextBoolean()) {
                    // Cancelled before it started
                    startDate = now.minusDays(random.nextInt(15) + 1);
                    endDate = startDate.plusDays(random.nextInt(60) + 30);
                } else {
                    // Cancelled during execution
                    startDate = now.minusDays(random.nextInt(30) + 1);
                    endDate = now.plusDays(random.nextInt(60) + 1);
                }
                break;
            default:
                startDate = now;
                endDate = now.plusDays(30);
        }
        
        campaign.setFechaInicio(Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()));
        campaign.setFechaFin(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
    }
    
    // Utility method to generate multiple campaigns
    public static CampaignModel[] createFakeCampaigns(int count) {
        CampaignModel[] campaigns = new CampaignModel[count];
        for (int i = 0; i < count; i++) {
            campaigns[i] = createFakeCampaign();
        }
        return campaigns;
    }
    
    // Generate campaigns with specific status distribution
    public static CampaignModel[] createFakeCampaignsWithDistribution() {
        return new CampaignModel[] {
            createFakeActiveCampaign(),
            createFakePlannedCampaign(),
            createFakeCompletedCampaign(),
            createFakeCancelledCampaign(),
            createFakeActiveCampaign(),
            createFakePlannedCampaign()
        };
    }
}
