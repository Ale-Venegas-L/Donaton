package cl.duoc.util;

import cl.duoc.model.Donation;
import cl.duoc.model.MonetaryDonation;
import cl.duoc.model.ObjectDonation;
import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class DonationDataFaker {
    
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    
    public static Donation createFakeDonation() {
        return createFakeDonation(createFakeCampaign());
    }
    
    public static Donation createFakeDonation(CampaignModel campaign) {
        // Randomly choose between monetary and object donation
        if (random.nextBoolean()) {
            return createFakeMonetaryDonation(campaign);
        } else {
            return createFakeObjectDonation(campaign);
        }
    }
    
    public static MonetaryDonation createFakeMonetaryDonation() {
        return createFakeMonetaryDonation(createFakeCampaign());
    }
    
    public static MonetaryDonation createFakeMonetaryDonation(CampaignModel campaign) {
        String donorName = faker.name().fullName();
        String description = generateDonationDescription("monetary");
        double amount = generateRealisticAmount();
        String currency = getRandomCurrency();
        
        return new MonetaryDonation(campaign, donorName, description, amount, currency);
    }
    
    public static ObjectDonation createFakeObjectDonation() {
        return createFakeObjectDonation(createFakeCampaign());
    }
    
    public static ObjectDonation createFakeObjectDonation(CampaignModel campaign) {
        String donorName = faker.name().fullName();
        String description = generateDonationDescription("object");
        
        String[] objects = {"Laptop", "Tablet", "Smartphone", "Impresora", "Monitor", "Teclado", "Mouse", "Auriculares", 
                           "Silla de oficina", "Escritorio", "Libros", "Ropa de invierno", "Alimentos no perecederos",
                           "Juguetes educativos", "Material escolar", "Equipo deportivo", "Herramientas"};
        String[] categories = {"Electrónica", "Muebles", "Libros", "Ropa", "Alimentos", "Juguetes", "Educación", "Deportes", "Herramientas"};
        
        String objectName = objects[random.nextInt(objects.length)];
        String category = categories[random.nextInt(categories.length)];
        double estimatedValue = generateRealisticValue();
        int quantity = random.nextInt(10) + 1; // 1-10 items
        
        return new ObjectDonation(campaign, donorName, description, objectName, category, estimatedValue, quantity);
    }
    
    public static Donation[] createFakeDonations(int count) {
        Donation[] donations = new Donation[count];
        CampaignModel campaign = createFakeCampaign();
        
        for (int i = 0; i < count; i++) {
            donations[i] = createFakeDonation(campaign);
        }
        return donations;
    }
    
    public static Donation[] createFakeDonationsForCampaign(CampaignModel campaign, int count) {
        Donation[] donations = new Donation[count];
        
        for (int i = 0; i < count; i++) {
            donations[i] = createFakeDonation(campaign);
        }
        return donations;
    }
    
    public static Donation[] createMixedDonations(int monetaryCount, int objectCount) {
        CampaignModel campaign = createFakeCampaign();
        Donation[] donations = new Donation[monetaryCount + objectCount];
        
        int index = 0;
        for (int i = 0; i < monetaryCount; i++) {
            donations[index++] = createFakeMonetaryDonation(campaign);
        }
        
        for (int i = 0; i < objectCount; i++) {
            donations[index++] = createFakeObjectDonation(campaign);
        }
        
        return donations;
    }
    
    private static CampaignModel createFakeCampaign() {
        CampaignModel campaign = new CampaignModel();
        campaign.setId((long) (random.nextInt(1000) + 1));
        campaign.setNombre("Campaña " + faker.lorem().word() + " 2024");
        campaign.setDescripcion("Descripción de campaña de prueba");
        campaign.setEstado(CampaignStatus.ACTIVE); // Most donations go to active campaigns
        
        // Set realistic dates
        LocalDateTime now = LocalDateTime.now();
        campaign.setFechaInicio(java.util.Date.from(now.minusDays(30).atZone(java.time.ZoneId.systemDefault()).toInstant()));
        campaign.setFechaFin(java.util.Date.from(now.plusDays(60).atZone(java.time.ZoneId.systemDefault()).toInstant()));
        
        return campaign;
    }
    
    private static String generateDonationDescription(String type) {
        String[] baseDescriptions = {
            "Donación generosa para apoyar nuestra causa",
            "Contribución para hacer la diferencia",
            "Apoyo solidario para quienes más lo necesitan",
            "Colaboración desinteresada por un mejor futuro",
            "Ayuda valiosa para nuestros proyectos"
        };
        
        String base = baseDescriptions[random.nextInt(baseDescriptions.length)];
        
        if (type.equals("monetary")) {
            String[] monetaryAdditions = {
                " para financiar nuestras actividades",
                " para comprar materiales necesarios",
                " para cubrir gastos operativos",
                " para expandir nuestro alcance",
                " para mantener nuestros programas"
            };
            return base + monetaryAdditions[random.nextInt(monetaryAdditions.length)];
        } else {
            String[] objectAdditions = {
                " para equipar nuestras instalaciones",
                " para distribuir entre beneficiarios",
                " para mejorar nuestros servicios",
                " para apoyar a familias necesitadas",
                " para fortalecer nuestros programas"
            };
            return base + objectAdditions[random.nextInt(objectAdditions.length)];
        }
    }
    
    private static double generateRealisticAmount() {
        // Generate realistic donation amounts in different ranges
        double[] ranges = {1000, 5000, 10000, 25000, 50000, 100000};
        double baseAmount = ranges[random.nextInt(ranges.length)];
        
        // Add some randomness (±20%)
        double variation = baseAmount * 0.2 * (random.nextDouble() * 2 - 1);
        return Math.round((baseAmount + variation) / 100) * 100.0; // Round to nearest 100
    }
    
    private static double generateRealisticValue() {
        // Generate realistic object values
        double[] ranges = {5000, 10000, 25000, 50000, 100000, 250000};
        double baseValue = ranges[random.nextInt(ranges.length)];
        
        // Add some randomness (±30%)
        double variation = baseValue * 0.3 * (random.nextDouble() * 2 - 1);
        return Math.round((baseValue + variation) / 100) * 100.0; // Round to nearest 100
    }
    
    private static String getRandomCurrency() {
        String[] currencies = {"CLP", "USD", "EUR", "GBP"};
        // Weighted probability - more likely to be CLP for Chile
        int[] weights = {70, 15, 10, 5}; // percentages
        
        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }
        
        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (int i = 0; i < currencies.length; i++) {
            currentWeight += weights[i];
            if (randomWeight < currentWeight) {
                return currencies[i];
            }
        }
        
        return currencies[0]; // Default to CLP
    }
    
    // Specialized methods for specific scenarios
    
    public static MonetaryDonation createLargeMonetaryDonation() {
        CampaignModel campaign = createFakeCampaign();
        String donorName = faker.name().fullName();
        String description = "Donación corporativa significativa";
        double amount = 500000 + random.nextInt(1500000); // 500k to 2M
        String currency = "CLP";
        
        return new MonetaryDonation(campaign, donorName, description, amount, currency);
    }
    
    public static ObjectDonation createHighValueObjectDonation() {
        CampaignModel campaign = createFakeCampaign();
        String donorName = faker.name().fullName();
        String description = "Donación de equipo de alto valor";
        
        String[] highValueObjects = {"Servidor empresarial", "Equipo médico", "Vehículo", "Maquinaria industrial", "Equipo de laboratorio"};
        String[] highValueCategories = {"Tecnología", "Salud", "Transporte", "Industrial", "Investigación"};
        
        String objectName = highValueObjects[random.nextInt(highValueObjects.length)];
        String category = highValueCategories[random.nextInt(highValueCategories.length)];
        double estimatedValue = 1000000 + random.nextInt(4000000); // 1M to 5M
        int quantity = 1;
        
        return new ObjectDonation(campaign, donorName, description, objectName, category, estimatedValue, quantity);
    }
    
    public static Donation[] createDonationsForTesting() {
        return new Donation[] {
            createFakeMonetaryDonation(),
            createFakeObjectDonation(),
            createLargeMonetaryDonation(),
            createHighValueObjectDonation(),
            createFakeMonetaryDonation()
        };
    }
}
