package com.siewe.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderedProduct {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ordered_product_id")
    private Long id;

    private  double quantity;

    private double subTotal;

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "product_id")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id",referencedColumnName = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private  Purchase purchase;


}
