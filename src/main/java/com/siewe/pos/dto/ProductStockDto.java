package com.siewe.pos.dto;

import com.siewe.pos.model.ProductStock;
import lombok.Data;

@Data
public class ProductStockDto {

    private Long id;
    private String date;
    private Long productId;
    private Double stock;

    public ProductStockDto createDTO(ProductStock productStock){
        ProductStockDto productStockDto = new ProductStockDto();

        if (productStock != null){
            productStockDto.setId(productStock.getId());
            if (productStock.getProduct() != null){
            productStockDto.setProductId(productStock.getProduct().getProductId());}
            productStockDto.setStock(productStock.getStock());
            productStockDto.setDate(productStock.getDate());
        }

        return productStockDto;
    }
}
