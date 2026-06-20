package cl.duoc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "object_donation")
public class ObjectDonation extends Donation {
    private String objectName;
    private String category;
    private double estimatedValue;
    private int quantity;
    
    public ObjectDonation() {
        super();
    }
    
    public ObjectDonation(CampaignModel campaign, String donorName, String description, String objectName, String category, double estimatedValue, int quantity) {
        super(campaign, donorName, description);
        this.objectName = objectName;
        this.category = category;
        this.estimatedValue = estimatedValue;
        this.quantity = quantity;
    }
    
    @Override
    public String getType() {
        return "OBJECT";
    }
    
    @Override
    public double getValue() {
        return estimatedValue * quantity;
    }
    
    public String getObjectName() {
        return objectName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public double getEstimatedValue() {
        return estimatedValue;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
