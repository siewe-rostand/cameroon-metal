package com.siewe.pos.dto;

import com.siewe.pos.model.Product;
import lombok.Data;

@Data
public class ProductDto {
    private Long productId;
    private String name;
    private Double unitPrice;
    private String description;
    private String createdDate;
    private String imageUrl;
    private Double stockQty;
    private Boolean available;

    public ProductDto createDTO(Product product){

        ProductDto productDto = new ProductDto();

        if (product != null){
            productDto.setProductId(product.getProductId());
            productDto.setName(product.getName());
            productDto.setUnitPrice(product.getUnitPrice());
            productDto.setDescription(product.getDescription());
            productDto.setCreatedDate(product.getCreatedDate());
            productDto.setAvailable(product.getAvailable());
            productDto.setStockQty(product.getStockQty());
            productDto.setImageUrl(product.getImageUrl());
        }

        return productDto;
    }
}
