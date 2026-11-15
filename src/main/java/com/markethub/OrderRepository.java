package com.markethub;

import com.markethub.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderRecord, Long> {
    List<OrderRecord> findByCustomerEmail(String customerEmail);
    List<OrderRecord> findByVendorId(Long vendorId);
}