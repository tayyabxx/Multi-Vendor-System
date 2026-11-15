package com.markethub;

import com.markethub.Customer;
import com.markethub.OrderRecord;
import com.markethub.Product;
import com.markethub.Vendor;
import com.markethub.OrderRepository;
import com.markethub.ProductRepository;
import com.markethub.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public WebController(UserService userService, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("platformName", com.markethub.SystemConfigManager.getInstance().getPlatformName());
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String role, @RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Object user = userService.authenticate(role, email, password);
        if (user != null) {
            session.setAttribute("loggedInUser", user);
            if ("BUYER".equalsIgnoreCase(role)) {
                return "redirect:/customer/dashboard";
            } else {
                return "redirect:/vendor/dashboard";
            }
        }
        model.addAttribute("error", "Invalid credentials or incorrect role selected.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String role, @RequestParam String fullName, @RequestParam String email,
                                 @RequestParam String password, @RequestParam(required = false) String storeName,
                                 @RequestParam(required = false) String cnic, Model model) {
        boolean success;
        if ("BUYER".equalsIgnoreCase(role)) {
            success = userService.registerCustomer(fullName, email, password);
        } else {
            success = userService.registerVendor(fullName, email, password, storeName, cnic);
        }

        if (!success) {
            model.addAttribute("error", "Email is already registered.");
            return "register";
        }
        return "redirect:/login?registered=true";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(HttpSession session, @RequestParam(required = false) String keyword, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInUser");
        if (customer == null) return "redirect:/login";
        model.addAttribute("user", customer);

        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("products", productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword));
        } else {
            model.addAttribute("products", productRepository.findAll());
        }
        model.addAttribute("orders", orderRepository.findByCustomerEmail(customer.getEmail()));
        model.addAttribute("keyword", keyword);
        return "customer_dashboard";
    }

    @PostMapping("/customer/order/place")
    public String placeOrder(@RequestParam Long productId, @RequestParam int quantity, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInUser");
        if (customer == null) return "redirect:/login";

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null && product.getStockQuantity() >= quantity) {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);

            double totalPrice = product.getPrice() * quantity;
            double commission = totalPrice * com.markethub.SystemConfigManager.getInstance().getDefaultCommissionRate();
            double netEarnings = totalPrice - commission;

            OrderRecord order = new OrderRecord();
            order.setCustomerEmail(customer.getEmail());
            order.setVendorId(product.getVendorId());
            order.setProductName(product.getName());
            order.setQuantity(quantity);
            order.setTotalPrice(totalPrice);
            order.setPlatformCommission(commission);
            order.setNetEarnings(netEarnings);
            order.setStatus("Placed"); // Initial state[cite: 1]
            orderRepository.save(order);
        }
        return "redirect:/customer/dashboard";
    }

    @GetMapping("/vendor/dashboard")
    public String vendorDashboard(HttpSession session, Model model) {
        Vendor vendor = (Vendor) session.getAttribute("loggedInUser");
        if (vendor == null) return "redirect:/login";
        model.addAttribute("vendor", vendor);
        model.addAttribute("products", productRepository.findByVendorId(vendor.getId()));
        model.addAttribute("orders", orderRepository.findByVendorId(vendor.getId()));
        return "vendor_dashboard";
    }

    @PostMapping("/vendor/product/add")
    public String addProduct(@RequestParam String name, @RequestParam String category, @RequestParam double price,
                             @RequestParam int stockQuantity, HttpSession session) {
        Vendor vendor = (Vendor) session.getAttribute("loggedInUser");
        if (vendor == null) return "redirect:/login";

        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setStockQuantity(stockQuantity);
        p.setVendorId(vendor.getId());
        productRepository.save(p);
        return "redirect:/vendor/dashboard";
    }

    @GetMapping("/vendor/product/delete")
    public String deleteProduct(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        productRepository.deleteById(id);
        return "redirect:/vendor/dashboard";
    }

    @PostMapping("/vendor/order/update-status")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status,
                                    @RequestParam(required = false) String vendorMessage, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        OrderRecord order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);

            if (vendorMessage != null && !vendorMessage.trim().isEmpty()) {
                order.setVendorMessage(vendorMessage);
            } else if ("Confirmed".equalsIgnoreCase(status)) {
                order.setVendorMessage("🎉 Order accepted by store! Will arrive in 3-5 business days.");
            } else if ("Shipped".equalsIgnoreCase(status)) {
                order.setVendorMessage("📦 Your shipment is on the way with our courier partner.");
            } else if ("Delivered".equalsIgnoreCase(status)) {
                order.setVendorMessage("✅ Order delivered successfully. Enjoy your purchase!");
            }

            orderRepository.save(order);
        }
        return "redirect:/vendor/dashboard";
    }

    @GetMapping("/contact")
    public String contactPage() { return "contact"; }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() { return "forgot-password"; }
}