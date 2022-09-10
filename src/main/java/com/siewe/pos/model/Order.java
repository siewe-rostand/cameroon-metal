package com.siewe.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * model class for order to be place for order
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`order`")
public class Order  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "total_amount")
    private  double totalAmount;

    @Column(name="order_tracking_number")
    private String orderTrackingNumber;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "customer_id")
    private Customer customers;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    private List<OrderedProduct> orderedProducts;
    private Set<OrderedProduct> orderedProducts = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private Set<Purchase> purchases = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id",referencedColumnName = "address_id")
    private Address address;

    /*
    public Double getTotal() {
        Double total = 0.0;
        int size = items.size();

        for(int i = 0; i < size; i++) {
            total += items.get(i).calculateImport();
        }

        return total;
    }*/


    public String getCreatedDate() {
        String pattern = "yyyy-MM-dd HH:mm";
        if(createdDate != null) {
            return createdDate.toString(pattern);
        }
        return null;
    }

    public void setCreatedDate(String createdDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        LocalDateTime cd = null;
        if(createdDate!=null)
            cd = LocalDateTime.parse(createdDate, formatter);
        this.createdDate = cd;
    }

    public String getDate(){
        String patternDate = "dd-MM-yyyy";
        String patternTime = "HH:mm";

        return createdDate.toString(patternDate) + " à " + createdDate.toString(patternTime);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", totalAmount=" + totalAmount +
                ", orderTrackingNumber='" + orderTrackingNumber + '\'' +
                ", createdDate=" + createdDate +
                ", customers=" + customers +
                ", orderedProducts=" + orderedProducts +
                ", user=" + user +
                ", purchases=" + purchases +
                ", address=" + address +
                '}';
    }

    public void add(OrderedProduct orderedProduct){
        if (orderedProduct != null){
            if (orderedProducts == null){
                orderedProducts = new HashSet<>();
            }
            orderedProducts.add(orderedProduct);
            orderedProduct.setOrder(this);
        }
    }
}
