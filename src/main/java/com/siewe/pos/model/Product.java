package com.siewe.pos.model;

import lombok.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long productId;

    private String name;
    @Value("${upload.path}")
    private String uploadPath;

    @Column(name = "unit_price")
    private double unitPrice;

    @Column(name = "stock_qty")
    private double stockQty;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "stock_alert")
    private Double stockAlert;

    private Boolean available;

    private Boolean deleted;

    private String description;
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "ordered_product_id",referencedColumnName = "ordered_product_id")
    private OrderedProduct orderedProduct;

    @OneToMany(mappedBy = "product")
    private List<ProductStock> productStocks;

    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @ManyToOne
    private Category category;

    @Transient
    public String getPhotosImagePath() {
        if (imageUrl == null || productId == null) return null;

        return "./uploaded/img" + productId + "/" + imageUrl;
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
        if(createdDate!=null)
            cd = formatter.parseLocalDate(createdDate);
        this.createdDate = cd;
    }
//    @Transient
//    public String getPhotosImagePath() {
//        if (imageUrl == null || productId == null) return null;
//
//        return uploadPath + productId + "/" + imageUrl;
//    }

}
