package com.siewe.pos.model;

import lombok.*;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Purchase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "prixTotal")
    private Double prixTotal;

    //@Size(max = 20)
    @Column(name = "typePaiement")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String typePaiement;

    @Column(name = "deleted")
    private Boolean deleted;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "created_date")
    private LocalDateTime createdDate;


    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    @ManyToOne
    private Customer customer;



    @ManyToOne
    @JoinColumn(name = "order_order_id")
    private Order order;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "purchase")
    private Set<OrderedProduct> orderedProducts;

    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User user;

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
}
