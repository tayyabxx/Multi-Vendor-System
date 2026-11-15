package com.markethub;

public class SystemConfigManager {
    private static SystemConfigManager instance;
    private String platformName;
    private double defaultCommissionRate;

    private SystemConfigManager() {
        this.platformName = "MarketHub Multi-Vendor System";
        this.defaultCommissionRate = 0.10; // 10% commission
    }

    public static synchronized SystemConfigManager getInstance() {
        if (instance == null) {
            instance = new SystemConfigManager();
        }
        return instance;
    }

    public String getPlatformName() { return platformName; }
    public double getDefaultCommissionRate() { return defaultCommissionRate; }
}