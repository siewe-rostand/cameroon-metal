package com.siewe.pos.dto;

import com.siewe.pos.model.Customer;
import lombok.Data;

@Data
public class CustomerDto {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String fullName;
    private int phone;
    private String createdDate;

    public CustomerDto createDTO(Customer customer){
        CustomerDto customerDto = new CustomerDto();

        if (customer != null){
            customerDto.setCustomerId(customer.getCustomerId());
            customerDto.setFirstName(customer.getFirstName());
            customerDto.setLastName(customer.getLastName());
            customerDto.setPhone(customer.getPhone());
            customerDto.setCreatedDate(customer.getCreatedDate());
            customerDto.setFullName(customer.getFullName());
        }

        return customerDto;
    }
}
