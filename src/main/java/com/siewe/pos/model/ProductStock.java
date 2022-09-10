package com.siewe.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;

import javax.persistence.*;

/**
 * model class to store product from suppliers
 * created by rostand siewe
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "product_stock")
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double stock;

    private LocalDate date;

    @ManyToOne
    @JoinColumn
    private Product product;

    public String getDate() {
        String pattern = "yyyy-MM-dd";
        if(date != null) {
            return date.toString(pattern);
        }
        return null;
    }

}
