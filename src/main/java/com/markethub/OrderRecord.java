package com.markethub;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;
    private Long vendorId;
    private String productName;
    private int quantity;
    private double totalPrice;
    private double platformCommission;
    private double netEarnings;
    private String status;           // Placed, Confirmed, Shipped, Delivered
    private String vendorMessage;    // Notification message sent by vendor (e.g., delivery timeframe)

    public OrderRecord() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public double getPlatformCommission() { return platformCommission; }
    public void setPlatformCommission(double platformCommission) { this.platformCommission = platformCommission; }
    public double getNetEarnings() { return netEarnings; }
    public void setNetEarnings(double netEarnings) { this.netEarnings = netEarnings; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getVendorMessage() { return vendorMessage; }
    public void setVendorMessage(String vendorMessage) { this.vendorMessage = vendorMessage; }
}