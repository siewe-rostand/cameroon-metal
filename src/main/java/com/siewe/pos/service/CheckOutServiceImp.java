package com.siewe.pos.service;

import com.siewe.pos.model.Customer;
import com.siewe.pos.model.Order;
import com.siewe.pos.model.Purchase;
import com.siewe.pos.model.PurchaseResponse;
import com.siewe.pos.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class CheckOutServiceImp implements CheckoutService {
    private CustomerRepository customerRepository;

    public CheckOutServiceImp(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {

        // retrieve the order info from dto
        Order order = purchase.getOrder();

        // generate tracking number
        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);


        // populate customer with order
        Customer customer = purchase.getCustomer();
        customer.add(order);

        // save to the database
        customerRepository.save(customer);

        // return a response
        return new PurchaseResponse(orderTrackingNumber);
    }

    private String generateOrderTrackingNumber() {

        // generate a random UUID number (UUID version-4)
        // For details see: https://en.wikipedia.org/wiki/Universally_unique_identifier
        //
        return UUID.randomUUID().toString();
    }
}
