package cl.duoc.util;

import cl.duoc.model.Volunteer;
import cl.duoc.model.CampaignModel;
import cl.duoc.model.CampaignStatus;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class VolunteerDataFaker {
    
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    
    public static Volunteer createFakeVolunteer() {
        return createFakeVolunteer(null);
    }
    
    public static Volunteer createFakeVolunteer(Set<CampaignModel> campaigns) {
        Volunteer volunteer = new Volunteer();
        
        // Generate realistic Chilean names
        String[] chileanFirstNames = {"María", "Ana", "Sofía", "Isabella", "Valentina", "Josefina", 
                                      "Benjamín", "Mateo", "Santiago", "Lucas", "Joaquín", "Matías"};
        String[] chileanLastNames = {"González", "Muñoz", "Rojas", "Díaz", "Fernández", "Morales", 
                                    "López", "Martínez", "Pérez", "Gómez", "Soto", "Silva"};
        
        String firstName = chileanFirstNames[random.nextInt(chileanFirstNames.length)];
        String lastName = chileanLastNames[random.nextInt(chileanLastNames.length)];
        String secondLastName = chileanLastNames[random.nextInt(chileanLastNames.length)];
        
        volunteer.setNombre(firstName);
        volunteer.setApellido(lastName + " " + secondLastName);
        
        // Generate realistic email
        String email = generateEmail(firstName, lastName);
        volunteer.setEmail(email);
        
        // Generate Chilean phone number
        volunteer.setTelefono(generateChileanPhoneNumber());
        
        // Generate realistic Chilean address
        volunteer.setDireccion(generateChileanAddress());
        
        // Set campaigns if provided
        if (campaigns != null && !campaigns.isEmpty()) {
            volunteer.setCampaigns(campaigns);
        } else {
            volunteer.setCampaigns(new HashSet<>());
        }
        
        return volunteer;
    }
    
    public static Volunteer createFakeVolunteerWithCampaigns() {
        Set<CampaignModel> campaigns = new HashSet<>();
        int campaignCount = random.nextInt(3) + 1; // 1-3 campaigns
        
        for (int i = 0; i < campaignCount; i++) {
            campaigns.add(createFakeCampaign());
        }
        
        return createFakeVolunteer(campaigns);
    }
    
    public static Volunteer[] createFakeVolunteers(int count) {
        Volunteer[] volunteers = new Volunteer[count];
        Set<String> usedEmails = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            Volunteer volunteer;
            do {
                volunteer = createFakeVolunteer();
            } while (usedEmails.contains(volunteer.getEmail()));
            
            usedEmails.add(volunteer.getEmail());
            volunteers[i] = volunteer;
        }
        
        return volunteers;
    }
    
    public static Volunteer[] createFakeVolunteersWithCampaigns(int count) {
        Volunteer[] volunteers = new Volunteer[count];
        
        // Create some campaigns to assign
        Set<CampaignModel> availableCampaigns = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            availableCampaigns.add(createFakeCampaign());
        }
        
        Set<String> usedEmails = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            Volunteer volunteer;
            do {
                // Assign 1-2 random campaigns to each volunteer
                Set<CampaignModel> volunteerCampaigns = availableCampaigns.stream()
                    .skip(random.nextInt(availableCampaigns.size()))
                    .limit(random.nextInt(2) + 1)
                    .collect(Collectors.toSet());
                
                volunteer = createFakeVolunteer(volunteerCampaigns);
            } while (usedEmails.contains(volunteer.getEmail()));
            
            usedEmails.add(volunteer.getEmail());
            volunteers[i] = volunteer;
        }
        
        return volunteers;
    }
    
    public static Volunteer createFakeVolunteerForCampaign(CampaignModel campaign) {
        Set<CampaignModel> campaigns = new HashSet<>();
        campaigns.add(campaign);
        return createFakeVolunteer(campaigns);
    }
    
    public static Volunteer createFakeCorporateVolunteer() {
        Volunteer volunteer = new Volunteer();
        
        // Corporate names
        String[] corporateFirstNames = {"Roberto", "Carlos", "Andrés", "Felipe", "Diego", "Patricia", "Cecilia", "Verónica"};
        String[] corporateLastNames = {"García", "Rodríguez", "Hernández", "López", "Martínez", "Gómez"};
        
        String firstName = corporateFirstNames[random.nextInt(corporateFirstNames.length)];
        String lastName = corporateLastNames[random.nextInt(corporateLastNames.length)];
        
        volunteer.setNombre(firstName);
        volunteer.setApellido(lastName + " " + corporateLastNames[random.nextInt(corporateLastNames.length)]);
        
        // Corporate email
        String[] companies = {"empresa", "corporacion", "fundacion", "instituto", "consultora"};
        String company = companies[random.nextInt(companies.length)];
        String email = (firstName.toLowerCase() + "." + lastName.toLowerCase().split(" ")[0] + "@" + company + ".cl").replace(" ", "");
        volunteer.setEmail(email);
        
        // Corporate phone
        volunteer.setTelefono("+562" + String.format("%08d", random.nextInt(10000000)));
        
        // Corporate address (Santiago)
        volunteer.setDireccion("Oficina " + (random.nextInt(50) + 1) + ", " + 
                             faker.address().streetName() + ", " + 
                             faker.address().buildingNumber() + ", Santiago, Región Metropolitana");
        
        volunteer.setCampaigns(new HashSet<>());
        
        return volunteer;
    }
    
    public static Volunteer createFakeStudentVolunteer() {
        Volunteer volunteer = new Volunteer();
        
        // Younger names for students
        String[] studentFirstNames = {"Tomás", "Ignacio", "Diego", "Francisco", "Javiera", "Catalina", "Isidora", "Martina"};
        String[] studentLastNames = {"Vargas", "Fuentes", "Castillo", "Ramírez", "Sánchez", "Torres"};
        
        String firstName = studentFirstNames[random.nextInt(studentFirstNames.length)];
        String lastName = studentLastNames[random.nextInt(studentLastNames.length)];
        
        volunteer.setNombre(firstName);
        volunteer.setApellido(lastName + " " + studentLastNames[random.nextInt(studentLastNames.length)]);
        
        // Student email (university)
        String[] universities = {"uc", "uchile", "udp", "usach", "puc", "uandes"};
        String university = universities[random.nextInt(universities.length)];
        String email = (firstName.toLowerCase() + "." + lastName.toLowerCase().split(" ")[0] + "@" + university + ".cl").replace(" ", "");
        volunteer.setEmail(email);
        
        // Student phone (mobile)
        volunteer.setTelefono("+569" + String.format("%08d", random.nextInt(10000000)));
        
        // Student address (near universities)
        String[] communes = {"Providencia", "Ñuñoa", "Santiago Centro", "Las Condes", "Vitacura"};
        String commune = communes[random.nextInt(communes.length)];
        volunteer.setDireccion(faker.address().streetAddress() + ", " + commune + ", Santiago");
        
        volunteer.setCampaigns(new HashSet<>());
        
        return volunteer;
    }
    
    private static String generateEmail(String firstName, String lastName) {
        String[] domains = {"gmail.com", "hotmail.com", "outlook.com", "yahoo.com", "mail.com"};
        String domain = domains[random.nextInt(domains.length)];
        
        // Different email formats
        int format = random.nextInt(4);
        String baseLastName = lastName.split(" ")[0].toLowerCase();
        
        switch (format) {
            case 0:
                return firstName.toLowerCase() + "." + baseLastName + "@" + domain;
            case 1:
                return firstName.toLowerCase() + baseLastName + random.nextInt(100) + "@" + domain;
            case 2:
                return firstName.charAt(0) + baseLastName + "@" + domain;
            default:
                return firstName.toLowerCase() + "_" + baseLastName + "@" + domain;
        }
    }
    
    private static String generateChileanPhoneNumber() {
        // Generate realistic Chilean phone numbers
        int format = random.nextInt(3);
        
        switch (format) {
            case 0:
                // Mobile: +569 XXXX XXXX
                return "+569" + String.format("%04d %04d", random.nextInt(10000), random.nextInt(10000));
            case 1:
                // Santiago landline: +562 XXXX XXXX
                return "+562" + String.format("%04d %04d", random.nextInt(10000), random.nextInt(10000));
            default:
                // Regional landline: +56XX XXXX XXXX
                int regionCode = random.nextInt(7) + 32; // 32-38 (Chilean region codes)
                return "+56" + regionCode + String.format("%04d %04d", random.nextInt(10000), random.nextInt(10000));
        }
    }
    
    private static String generateChileanAddress() {
        String[] cities = {"Santiago", "Valparaíso", "Concepción", "La Serena", "Antofagasta", "Temuco", "Rancagua", "Talca"};
        String[] streetTypes = {"Calle", "Avenida", "Pasaje", "Plaza", "Camino"};
        
        String city = cities[random.nextInt(cities.length)];
        String streetType = streetTypes[random.nextInt(streetTypes.length)];
        String streetName = faker.address().streetName();
        int number = random.nextInt(9999) + 1;
        
        // Add apartment info sometimes
        String apartment = "";
        if (random.nextBoolean()) {
            apartment = ", Depto. " + (random.nextInt(200) + 1);
        }
        
        return streetType + " " + streetName + " " + number + apartment + ", " + city;
    }
    
    private static CampaignModel createFakeCampaign() {
        CampaignModel campaign = new CampaignModel();
        campaign.setId((long) (random.nextInt(1000) + 1));
        
        String[] campaignTypes = {"Educación", "Salud", "Medio Ambiente", "Comunidad", "Infancia"};
        String type = campaignTypes[random.nextInt(campaignTypes.length)];
        campaign.setNombre("Campaña de " + type + " " + faker.lorem().word());
        campaign.setDescripcion("Descripción de campaña de " + type.toLowerCase());
        campaign.setEstado(CampaignStatus.ACTIVE);
        
        LocalDateTime now = LocalDateTime.now();
        campaign.setFechaInicio(Date.from(now.minusDays(30).atZone(ZoneId.systemDefault()).toInstant()));
        campaign.setFechaFin(Date.from(now.plusDays(60).atZone(ZoneId.systemDefault()).toInstant()));
        
        return campaign;
    }
    
    // Specialized methods for testing scenarios
    
    public static Volunteer[] createVolunteersForTesting() {
        return new Volunteer[] {
            createFakeVolunteer(),
            createFakeCorporateVolunteer(),
            createFakeStudentVolunteer(),
            createFakeVolunteerWithCampaigns()
        };
    }
    
    public static Volunteer createVolunteerWithEmail(String email) {
        Volunteer volunteer = createFakeVolunteer();
        volunteer.setEmail(email);
        return volunteer;
    }
    
    public static Volunteer createVolunteerWithName(String firstName, String lastName) {
        Volunteer volunteer = createFakeVolunteer();
        volunteer.setNombre(firstName);
        volunteer.setApellido(lastName);
        volunteer.setEmail(generateEmail(firstName, lastName));
        return volunteer;
    }
    
    // Method to create volunteers with specific characteristics for testing
    
    public static Volunteer createVolunteerWithMultipleCampaigns() {
        Set<CampaignModel> campaigns = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            campaigns.add(createFakeCampaign());
        }
        return createFakeVolunteer(campaigns);
    }
    
    public static Volunteer createVolunteerFromRegion(String region) {
        Volunteer volunteer = createFakeVolunteer();
        
        // Adjust phone and address based on region
        switch (region.toLowerCase()) {
            case "norte":
                volunteer.setTelefono("+562" + String.format("%08d", random.nextInt(10000000)));
                volunteer.setDireccion("Avenida Antofagasta " + (random.nextInt(1000) + 1) + ", Antofagasta");
                break;
            case "sur":
                volunteer.setTelefono("+5641" + String.format("%07d", random.nextInt(10000000)));
                volunteer.setDireccion("Calle Concepción " + (random.nextInt(1000) + 1) + ", Concepción");
                break;
            default:
                // Santiago (default)
                break;
        }
        
        return volunteer;
    }
}
