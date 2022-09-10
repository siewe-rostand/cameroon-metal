package com.siewe.pos.service;

import com.siewe.pos.model.Purchase;
import com.siewe.pos.model.PurchaseResponse;

public interface CheckoutService {
    PurchaseResponse placeOrder(Purchase purchase);
}
