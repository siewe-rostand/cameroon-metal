package com.siewe.pos.model;

import lombok.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * model class for the customers of a company
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private  Long customerId;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "firstname")
    private  String firstName;

    @Column(name = "fullname")
    private  String fullName;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "created_date")
    private LocalDate createdDate;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private Set<Purchase> purchase = new HashSet<>();

    private  int phone;

//    @ManyToMany(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "order_id",referencedColumnName = "order_id")
//    @JoinTable(name = "customer_order", joinColumns = {@JoinColumn(name = "customer_id",referencedColumnName = "customer_id")},inverseJoinColumns =
//            {@JoinColumn(name = "order_id",referencedColumnName = "order_id")})
    @OneToMany(mappedBy = "customers", cascade = CascadeType.ALL)
    private Set<Order> orders = new HashSet<Order>();

    public String getCreatedDate() {
        String pattern = "yyyy-MM-dd";
        if(createdDate != null) {
            return createdDate.toString(pattern);
        }
        return null;
    }

    public void setCreatedDate(String createdDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        LocalDate cd = null;
        if(createdDate!=null && !createdDate.isEmpty())
            cd = formatter.parseLocalDate(createdDate);
        this.createdDate = cd;
    }

    public  String getFullName(){
        String name = "";
        if (this.firstName != null){
            name += this.firstName;
        }

        if (this.lastName != null){
            name += this.lastName;
        }
        return  name;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", phone=" + phone +
                '}';
    }

    public void add(Order order){
        if (order != null){
            if (orders == null){
                orders = new HashSet<>();
            }
            orders.add(order);
            order.setCustomers(this);
        }
    }
}
