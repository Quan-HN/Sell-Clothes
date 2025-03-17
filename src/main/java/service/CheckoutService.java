package com.example.AsmGD1.service;
import com.example.AsmGD1.entity.Checkout;
import com.example.AsmGD1.repository.CheckoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckoutService {

    private final CheckoutRepository checkoutRepository;

    public CheckoutService(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    public List<Checkout> getAllCheckouts() {
        return checkoutRepository.findAll();
    }

    public Checkout getCheckoutById(Integer id) {
        Optional<Checkout> checkout = checkoutRepository.findById(id);
        return checkout.orElse(null);
    }

    public Checkout createCheckout(Checkout checkout) {
        return checkoutRepository.save(checkout);
    }

    public Checkout updateCheckoutStatus(Integer id, String status) {
        Optional<Checkout> optionalCheckout = checkoutRepository.findById(id); // Tìm checkout theo id
        if (optionalCheckout.isPresent()) {
            Checkout checkout = optionalCheckout.get(); // Lấy ra checkout từ optional
            checkout.setStatus(status); // Set status mới
            return checkoutRepository.save(checkout); // Lưu checkout mới vào database
        }
        return null;
    }

    public void deleteCheckout(Integer id) {
        checkoutRepository.deleteById(id);
    }
}
