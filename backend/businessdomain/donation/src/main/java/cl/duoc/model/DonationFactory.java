package cl.duoc.model;

public class DonationFactory {
    
    public enum DonationType {
        MONETARY,
        OBJECT
    }
    
    public static Donation createDonation(DonationType type, CampaignModel campaign, String donorName, String description) {
        switch (type) {
            case MONETARY:
                return new MonetaryDonation(campaign, donorName, description, 0.0, "CLP");
            case OBJECT:
                return new ObjectDonation(campaign, donorName, description, "", "", 0.0, 1);
            default:
                throw new IllegalArgumentException("Tipo de donación no soportado: " + type);
        }
    }
    
    public static Donation createMonetaryDonation(CampaignModel campaign, String donorName, String description, double amount, String currency) {
        return new MonetaryDonation(campaign, donorName, description, amount, currency);
    }
    
    public static Donation createObjectDonation(CampaignModel campaign, String donorName, String description, String objectName, 
                                               String category, double estimatedValue, int quantity) {
        return new ObjectDonation(campaign, donorName, description, objectName, category, estimatedValue, quantity);
    }
    
    public static Donation createDonationFromData(DonationType type, CampaignModel campaign, String donorName, String description, 
                                                 Object... parameters) {
        switch (type) {
            case MONETARY:
                if (parameters.length < 2) {
                    throw new IllegalArgumentException("Para donación monetaria se requiere: amount, currency");
                }
                double amount = (Double) parameters[0];
                String currency = (String) parameters[1];
                return new MonetaryDonation(campaign, donorName, description, amount, currency);
                
            case OBJECT:
                if (parameters.length < 4) {
                    throw new IllegalArgumentException("Para donación de objeto se requiere: objectName, category, estimatedValue, quantity");
                }
                String objectName = (String) parameters[0];
                String category = (String) parameters[1];
                double estimatedValue = (Double) parameters[2];
                int quantity = (Integer) parameters[3];
                return new ObjectDonation(campaign, donorName, description, objectName, category, estimatedValue, quantity);
                
            default:
                throw new IllegalArgumentException("Tipo de donación no soportado: " + type);
        }
    }
}
