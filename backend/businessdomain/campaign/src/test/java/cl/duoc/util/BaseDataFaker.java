package cl.duoc.util;

import com.github.javafaker.Faker;
import java.util.Random;
import java.util.Locale;

/**
 * Base utility class for fake data generation.
 * This class provides common methods that can be used across all modules.
 */
public class BaseDataFaker {
    
    protected static final Faker faker = new Faker(Locale.forLanguageTag("es"));
    protected static final Random random = new Random();
    
    /**
     * Generates a realistic Chilean ID (RUT format)
     */
    public static String generateChileanRUT() {
        int number = random.nextInt(19999999) + 1000000; // Between 1M and 20M
        return formatRUT(number);
    }
    
    /**
     * Formats a number as Chilean RUT
     */
    private static String formatRUT(int number) {
        String rut = String.valueOf(number);
        char dv = calculateDV(number);
        return rut + "-" + dv;
    }
    
    /**
     * Calculates verification digit for Chilean RUT
     */
    private static char calculateDV(int rut) {
        int m = 0;
        int s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - ++m % 6)) % 11;
        }
        return (char) (s != 0 ? s + 47 : 75);
    }
    
    /**
     * Generates a realistic age between min and max
     */
    public static int generateAge(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    
    /**
     * Generates a random boolean with weighted probability
     */
    public static boolean weightedBoolean(double trueProbability) {
        return random.nextDouble() < trueProbability;
    }
    
    /**
     * Selects a random element from an array
     */
    public static <T> T randomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }
    
    /**
     * Selects a random element from a list
     */
    public static <T> T randomElement(java.util.List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
    
    /**
     * Generates a random percentage between 0 and 100
     */
    public static double generatePercentage() {
        return random.nextDouble() * 100;
    }
    
    /**
     * Generates a random amount within the specified range
     */
    public static double generateAmount(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    
    /**
     * Generates a random integer within the specified range
     */
    public static int generateInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    
    /**
     * Creates a delay in milliseconds (useful for testing async operations)
     */
    public static void randomDelay(int maxMillis) {
        try {
            Thread.sleep(random.nextInt(maxMillis));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Generates a random timestamp within the last specified days
     */
    public static java.time.LocalDateTime randomTimestampWithinDays(int days) {
        return java.time.LocalDateTime.now()
                .minusDays(random.nextInt(days))
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60));
    }
    
    /**
     * Generates a random future timestamp within the specified days
     */
    public static java.time.LocalDateTime randomFutureTimestampWithinDays(int days) {
        return java.time.LocalDateTime.now()
                .plusDays(random.nextInt(days))
                .plusHours(random.nextInt(24))
                .plusMinutes(random.nextInt(60));
    }
    
    /**
     * Common Chilean first names
     */
    public static final String[] CHILEAN_FIRST_NAMES = {
        "María", "Ana", "Sofía", "Isabella", "Valentina", "Josefina", "Catalina", "Francisca",
        "Benjamín", "Mateo", "Santiago", "Lucas", "Joaquín", "Matías", "Tomás", "Diego"
    };
    
    /**
     * Common Chilean last names
     */
    public static final String[] CHILEAN_LAST_NAMES = {
        "González", "Muñoz", "Rojas", "Díaz", "Fernández", "Morales", "López", "Martínez",
        "Pérez", "Gómez", "Soto", "Silva", "Rodríguez", "Contreras", "Araya", "Gutiérrez"
    };
    
    /**
     * Chilean cities
     */
    public static final String[] CHILEAN_CITIES = {
        "Santiago", "Valparaíso", "Concepción", "La Serena", "Antofagasta", "Temuco", 
        "Rancagua", "Talca", "Arica", "Chillán", "Puerto Montt", "Iquique", "Osorno"
    };
    
    /**
     * Chilean regions
     */
    public static final String[] CHILEAN_REGIONS = {
        "Región Metropolitana", "Región de Valparaíso", "Región del Biobío", "Región de Coquimbo",
        "Región de Antofagasta", "Región de la Araucanía", "Región del Libertador", "Región del Maule"
    };
    
    /**
     * Common email domains in Chile
     */
    public static final String[] CHILEAN_EMAIL_DOMAINS = {
        "gmail.com", "hotmail.com", "outlook.com", "yahoo.com", "mail.com", "uc.cl", "uchile.cl", "udp.cl"
    };
    
    /**
     * Generates a realistic Chilean phone number
     */
    public static String generateChileanPhoneNumber() {
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
                int regionCode = random.nextInt(7) + 32; // 32-38
                return "+56" + regionCode + String.format("%04d %04d", random.nextInt(10000), random.nextInt(10000));
        }
    }
    
    /**
     * Generates a realistic Chilean address
     */
    public static String generateChileanAddress() {
        String[] streetTypes = {"Calle", "Avenida", "Pasaje", "Plaza", "Camino"};
        String city = randomElement(CHILEAN_CITIES);
        String streetType = randomElement(streetTypes);
        String streetName = faker.address().streetName();
        int number = random.nextInt(9999) + 1;
        
        String apartment = "";
        if (weightedBoolean(0.3)) { // 30% chance of having apartment
            apartment = ", Depto. " + (random.nextInt(200) + 1);
        }
        
        return streetType + " " + streetName + " " + number + apartment + ", " + city;
    }
    
    /**
     * Generates a realistic email based on name and domain
     */
    public static String generateEmail(String firstName, String lastName, String domain) {
        String[] formats = {
            firstName.toLowerCase() + "." + lastName.toLowerCase(),
            firstName.toLowerCase() + lastName.toLowerCase(),
            firstName.charAt(0) + lastName.toLowerCase(),
            firstName.toLowerCase() + "_" + lastName.toLowerCase()
        };
        
        String base = randomElement(formats);
        
        // Sometimes add numbers
        if (weightedBoolean(0.2)) {
            base += random.nextInt(100);
        }
        
        return base + "@" + domain;
    }
    
    /**
     * Generates a realistic description for different contexts
     */
    public static String generateDescription(String context) {
        String[] baseDescriptions = {
            "Iniciativa para mejorar nuestra comunidad",
            "Proyecto enfocado en el bienestar social",
            "Programa diseñado para generar impacto positivo",
            "Esfuerzo colaborativo por un mejor futuro",
            "Trabajo conjunto para el desarrollo sostenible"
        };
        
        String base = randomElement(baseDescriptions);
        
        switch (context.toLowerCase()) {
            case "campaign":
                String[] campaignAdditions = {
                    " a través de la participación ciudadana",
                    " mediante el voluntariado y donaciones",
                    " con el apoyo de nuestra comunidad",
                    " para beneficiar a quienes más lo necesitan"
                };
                return base + randomElement(campaignAdditions);
            case "donation":
                String[] donationAdditions = {
                    " para apoyar causas nobles",
                    " contribuyendo al cambio social",
                    " ayudando a construir una mejor sociedad",
                    " marcando la diferencia en la vida de otros"
                };
                return base + randomElement(donationAdditions);
            case "volunteer":
                String[] volunteerAdditions = {
                    " mediante el compromiso solidario",
                    " con la dedicación de nuestros voluntarios",
                    " gracias al tiempo y esfuerzo compartido",
                    " a través del trabajo voluntario"
                };
                return base + randomElement(volunteerAdditions);
            default:
                return base;
        }
    }
    
    /**
     * Generates a realistic monetary amount in CLP
     */
    public static double generateCLPAmount() {
        Double[] ranges = {1000.0, 5000.0, 10000.0, 25000.0, 50000.0, 100000.0, 250000.0, 500000.0, 1000000.0};
        double baseAmount = randomElement(ranges);
        
        // Add some randomness (±20%)
        double variation = baseAmount * 0.2 * (random.nextDouble() * 2 - 1);
        return Math.round((baseAmount + variation) / 100) * 100.0; // Round to nearest 100
    }
    
    /**
     * Generates a realistic monetary amount in USD
     */
    public static double generateUSDAmount() {
        Double[] ranges = {10.0, 25.0, 50.0, 100.0, 250.0, 500.0, 1000.0, 2500.0, 5000.0};
        double baseAmount = randomElement(ranges);
        
        // Add some randomness (±15%)
        double variation = baseAmount * 0.15 * (random.nextDouble() * 2 - 1);
        return Math.round((baseAmount + variation) * 100) / 100.0; // Round to 2 decimal places
    }
}
