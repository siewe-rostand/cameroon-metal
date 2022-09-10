package com.siewe.pos.dto;

import com.siewe.pos.model.Address;
import com.siewe.pos.model.Order;
import com.siewe.pos.model.OrderedProduct;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Data
public class OrderDto {
    private Long orderId;
    private String orderNum;
    private double totalAmount;
    private Long customerId;
    private CustomerDto customer;
    private Long userId;
    private String userName;
    private String createdDate;
    private Set<OrderedProduct> orderedProduct;
    private Set<OrderedProductDto> orderedProductDtos;
    private  Set<Address> addresses;

    public OrderDto createDTO(Order order){
        OrderDto orderDto = new OrderDto();

        if (order != null){
            orderDto.setOrderId(order.getOrderId());
            orderDto.setOrderNum(StringUtils.leftPad(order.getOrderId().toString(),6,"0"));
            orderDto.setCreatedDate(order.getCreatedDate());
//            orderDto.setTotalAmount(order.getTotalAmount());

            if (order.getCustomers() != null){
                orderDto.setCustomerId(order.getCustomers().getCustomerId());
                orderDto.setCustomer(new CustomerDto().createDTO(order.getCustomers()));
            }

            if (order.getUser() != null){
                orderDto.setUserId(order.getUser().getUserId());
                orderDto.setUserName(order.getUser().getFullName());
            }

            double total = 0;
//            ArrayList<OrderedProductDto> orderedProductDtos = new ArrayList<>();
//            if (order.getOrderedProducts() != null){
//                for (OrderedProduct orderedProduct : order.getOrderedProducts()){
//                    orderDto.setTotalAmount(orderedProduct.getSubTotal());
//                    orderedProductDtos.add(new OrderedProductDto().createDTO(orderedProduct));
//                }
//            }
            if (order.getOrderedProducts() != null){
                HashSet<OrderedProduct> orderedProducts = new HashSet<>();
                if (order.getOrderedProducts() != null){
                    for (OrderedProduct orderedProduct : order.getOrderedProducts()){
                        orderedProducts.add(orderedProduct);
                    }
                }
                orderDto.setOrderedProduct(orderedProducts);
            }

            if (order.getAddress() != null){
                HashSet<Address> addresses = new HashSet<>();
                if (order.getAddress() != null){

                    orderDto.setAddresses(addresses);
                }
            }

//            orderDto.setOrderedProductDtos(orderedProductDtos);
        }

        return orderDto;
    }
}
