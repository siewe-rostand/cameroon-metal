package com.siewe.pos.model;

import lombok.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    private  int phone;

    @Column(nullable = false, unique = true)
    private String login;

    private String password;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "created_date")
    private LocalDate createdDate;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "update_date")
    private LocalDate updateDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Purchase> purchases;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> role = new HashSet<>();

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

    public String getUpdatedDate() {
        String pattern = "yyyy-MM-dd";
        if(updateDate != null) {
            return updateDate.toString(pattern);
        }
        return null;
    }

    public void setUpdatedDate(String updatedDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        LocalDate cd = null;
        if(updatedDate!=null && !updatedDate.isEmpty())
            cd = formatter.parseLocalDate(updatedDate);
        this.updateDate = cd;
    }
    public String getName() {
        String name = this.getLastName();
        if(this.getFirstName() != null){
            name += " " + this.getFirstName();
        }
        return name;
    }

    public void add(Order order){
        if (order != null){
            if (orders == null){
                orders = new HashSet<>();
            }
            orders.add(order);
            order.setUser(this);
        }
    }
}
