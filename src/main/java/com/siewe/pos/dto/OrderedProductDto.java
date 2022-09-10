package com.siewe.pos.dto;

import com.siewe.pos.model.OrderedProduct;
import lombok.Data;

@Data
public class OrderedProductDto {

    private Long orderDetailNum;
    private Long productId;
    private Long orderId;
    private String name;
    private double quantity;
    private double subTotal;

    public OrderedProductDto createDTO(OrderedProduct orderedProduct){
        OrderedProductDto orderedProductDto = new OrderedProductDto();

        if (orderedProduct != null){
            orderedProductDto.setOrderDetailNum(orderedProduct.getId());
//            orderedProductDto.setSubTotal(orderedProduct.getSubTotal());
            if (orderedProduct.getProduct().getStockQty() > 0){
                orderedProductDto.setQuantity(orderedProduct.getQuantity());
            }else{
                 new RuntimeException("Unavailable quantity in stock");
            }


            if (orderedProduct.getProduct() != null) {
                orderedProductDto.setProductId(orderedProduct.getProduct().getProductId());
                orderedProductDto.setName(orderedProduct.getProduct().getName());
                double total = orderedProduct.getProduct().getUnitPrice()*orderedProduct.getQuantity();
                orderedProductDto.setSubTotal(total);
            }

            if (orderedProduct.getOrder() != null){
            orderedProductDto.setOrderId(orderedProduct.getOrder().getOrderId());}
        }

        return orderedProductDto;
    }
}
