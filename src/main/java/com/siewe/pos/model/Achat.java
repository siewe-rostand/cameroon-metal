package com.siewe.pos.model;

import lombok.Data;

import javax.persistence.Column;
import java.util.Set;

@Data
public class Achat {
    @Column(name="order_tracking_number")
    private String orderTrackingNumber;
    private Customer customer;
    private  Order order;
    private Set<OrderedProduct> orderedProductSet;
}
