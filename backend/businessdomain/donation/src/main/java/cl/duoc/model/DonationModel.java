package cl.duoc.model;

import java.util.ArrayList;
import java.util.List;

public class DonationModel {
    private List<Donation> donations;
    
    public DonationModel() {
        this.donations = new ArrayList<>();
    }
    
    public void addMonetaryDonation(CampaignModel campaign, String donorName, String description, double amount, String currency) {
        Donation donation = DonationFactory.createMonetaryDonation(campaign, donorName, description, amount, currency);
        donations.add(donation);
    }
    
    public void addObjectDonation(CampaignModel campaign, String donorName, String description, String objectName, 
                                 String category, double estimatedValue, int quantity) {
        Donation donation = DonationFactory.createObjectDonation(campaign, donorName, description, objectName, category, estimatedValue, quantity);
        donations.add(donation);
    }
    
    public void addDonation(DonationFactory.DonationType type, CampaignModel campaign, String donorName, String description, Object... parameters) {
        Donation donation = DonationFactory.createDonationFromData(type, campaign, donorName, description, parameters);
        donations.add(donation);
    }
    
    public List<Donation> getDonations() {
        return new ArrayList<>(donations);
    }
    
    public double getTotalValue() {
        return donations.stream().mapToDouble(Donation::getValue).sum();
    }
    
    public List<Donation> getMonetaryDonations() {
        return donations.stream()
                .filter(d -> "MONETARY".equals(d.getType()))
                .toList();
    }
    
    public List<Donation> getObjectDonations() {
        return donations.stream()
                .filter(d -> "OBJECT".equals(d.getType()))
                .toList();
    }
}
