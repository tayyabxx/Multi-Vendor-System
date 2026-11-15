package com.markethub;

import com.markethub.Customer;
import com.markethub.Vendor;
import com.markethub.CustomerRepository;
import com.markethub.VendorRepository;
import com.markethub.AsyncLoggerTask;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final AsyncLoggerTask asyncLoggerTask;

    public UserService(CustomerRepository customerRepository, VendorRepository vendorRepository, AsyncLoggerTask asyncLoggerTask) {
        this.customerRepository = customerRepository;
        this.vendorRepository = vendorRepository;
        this.asyncLoggerTask = asyncLoggerTask;
    }

    public boolean registerCustomer(String fullName, String email, String password) {
        if (customerRepository.findByEmail(email).isPresent()) return false;
        Customer c = new Customer();
        c.setFullName(fullName);
        c.setEmail(email);
        c.setPassword(password);
        customerRepository.save(c);
        asyncLoggerTask.logActivity("New Customer registered: " + email);
        return true;
    }

    public boolean registerVendor(String fullName, String email, String password, String storeName, String cnic) {
        if (vendorRepository.findByEmail(email).isPresent()) return false;
        Vendor v = new Vendor();
        v.setFullName(fullName);
        v.setEmail(email);
        v.setPassword(password);
        v.setStoreName(storeName);
        v.setCnic(cnic);
        vendorRepository.save(v);
        asyncLoggerTask.logActivity("New Vendor registered: " + email);
        return true;
    }

    public Object authenticate(String role, String email, String password) {
        if ("BUYER".equalsIgnoreCase(role)) {
            Optional<Customer> cust = customerRepository.findByEmail(email);
            if (cust.isPresent() && password.equals(cust.get().getPassword())) {
                asyncLoggerTask.logActivity("Customer logged in: " + email);
                return cust.get();
            }
        } else if ("VENDOR".equalsIgnoreCase(role)) {
            Optional<Vendor> vend = vendorRepository.findByEmail(email);
            if (vend.isPresent() && password.equals(vend.get().getPassword())) {
                asyncLoggerTask.logActivity("Vendor logged in: " + email);
                return vend.get();
            }
        }
        return null;
    }
}