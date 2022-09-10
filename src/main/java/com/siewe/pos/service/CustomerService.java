package com.siewe.pos.service;

import com.siewe.pos.dto.CustomerDto;
import com.siewe.pos.model.Customer;
import com.siewe.pos.repository.CustomerRepository;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * save a customer
     * @param customerDto data on a particular customer
     * @return customer entity
     */
    public Customer save(CustomerDto customerDto){
        logger.debug("Request to save a customer {}",customerDto);

        Customer customer = new Customer();

        customer.setCustomerId(customerDto.getCustomerId());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhone(customerDto.getPhone());
        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDateTime datetime = new LocalDateTime(DateTimeZone.forOffsetHours(1));
        customer.setCreatedDate(datetime.toString(pattern));

        Customer result = customerRepository.save(customer);
        if (result != null) {
            customer.setFullName(customerDto.getFullName());
        }

        return customerRepository.save(result);
    }

    /**
     * update a particular customer
     * @param customerDto to obtain a customer id
     * @return the updated customer
     */
    public ResponseEntity<CustomerDto> update(CustomerDto customerDto){
        logger.debug("Request to save a customer {}",customerDto);
        Customer customer = customerRepository.findByCustomerId(customerDto.getCustomerId());

        customer.setCustomerId(customerDto.getCustomerId());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhone(customerDto.getPhone());

        Customer result = customerRepository.save(customer);

       return new ResponseEntity<CustomerDto>(new CustomerDto().createDTO(result),HttpStatus.CREATED);
    }

    /**
     * get all customers
     * @param page number of pages
     * @param size customers per page
     * @param sortBy sort in asc or desc
     * @param direction either asc or desc
     * @param name name of customer
     * @return list of customers
     */
    public Page<CustomerDto> findAll(Integer page, Integer size, String sortBy, String direction, String name){
        logger.debug("Request to save a customer {}");

        Pageable pageable = PageRequest.of(page,size, Sort.Direction.fromString(direction),sortBy);
        Page<Customer> customers = customerRepository.findAll("%"+name+"%",pageable);

        Page<CustomerDto>customerDtos = customers.map(customer -> new CustomerDto().createDTO(customer));

        return customerDtos;
    }

    /**
     * get all the customer by a keyword
     * @param keyword word to be search
     * @return list of customers with a keyword
     */
    public List<CustomerDto> findByKeyword(String keyword){
        List<Customer> customers = customerRepository.findByKeyword("%"+keyword+"%");
        List<CustomerDto> customerDtos = new ArrayList<>();

        for (Customer customer : customers){
            customerDtos.add(new CustomerDto().createDTO(customer));
        }

        return customerDtos;
    }

    /**
     * get a customer by id
     * @param id of entity
     * @return customer with a given id
     */
    @Transactional(readOnly = true)
    public CustomerDto findOne(Long id){
        logger.debug("Request to get a customer id{}",id);
        Customer customer = customerRepository.findByCustomerId(id);

        return new CustomerDto().createDTO(customer);
    }

    /**
     * Delete a customer by id
     * @param id of the entity
     */
    public void delete(Long id){
        logger.debug("Request to delete a customer by id{}",id);
        Customer customer = customerRepository.findByCustomerId(id);
        if (Optional.ofNullable(customer).isPresent()){
            customerRepository.deleteById(id);
        }
    }
}
