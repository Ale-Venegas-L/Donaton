package cl.duoc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "monetary_donation")
public class MonetaryDonation extends Donation {
    private double amount;
    private String currency;
    
    public MonetaryDonation() {
        super();
    }
    
    public MonetaryDonation(CampaignModel campaign, String donorName, String description, double amount, String currency) {
        super(campaign, donorName, description);
        this.amount = amount;
        this.currency = currency;
    }
    
    @Override
    public String getType() {
        return "MONETARY";
    }
    
    @Override
    public double getValue() {
        return amount;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
