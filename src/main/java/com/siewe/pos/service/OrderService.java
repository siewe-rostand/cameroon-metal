package com.siewe.pos.service;

import com.siewe.pos.InvalidOrderItemException;
import com.siewe.pos.dto.OrderDto;
import com.siewe.pos.dto.OrderedProductDto;
import com.siewe.pos.model.Customer;
import com.siewe.pos.model.Order;
import com.siewe.pos.model.User;
import com.siewe.pos.repository.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class OrderService {
    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderedProductService orderedProductService;

    @Autowired
    private ProductRepository productRepository;

    private AddressRepository addressRepository;


    /*
    public OrderDto save(OrderDto orderDto){

        Order order = new Order();
        order.setOrderId(orderDto.getOrderId());
        order.setTotalAmount(orderDto.getTotalAmount());

        Customer customer = customerRepository.findOne(order.getOrderId());
        if (customer !=null){
            order.setCustomers(customer);
        }

        Order result = orderRepository.save(order);
        return new OrderDto().createDTO(result);
    }*/

    /**
     * save new order
     * @param orderDto the entity to be save
     * @return the entity in question
     * @throws InvalidOrderItemException if error
     */
    @Transactional(rollbackFor = InvalidOrderItemException.class)
    public Order save(OrderDto orderDto)throws InvalidOrderItemException{
        log.debug("Request to save an order{}", orderDto);

        HashMap<String,String> error = new HashMap<>();

        Order order = new Order();
        order.setOrderId(orderDto.getOrderId());
        order.setTotalAmount(orderDto.getTotalAmount());
        if (orderDto.getCustomerId() != null){
            Customer customer = customerRepository.findByCustomerId(orderDto.getCustomerId());
            customerService.update(orderDto.getCustomer());
            order.setCustomers(customer);
        }
        else {
//            create a  new customer
            if (orderDto.getCustomer() != null){
                Customer customer = customerService.save(orderDto.getCustomer());
                if (customer != null){
                    order.setCustomers(customer);
                }
            }
        }

        /// set the created date
        String pattern = "yyyy-MM-dd HH:mm";
        if (orderDto.getCreatedDate() != null){
            LocalDateTime dateTime = new LocalDateTime(DateTimeZone.forOffsetHours(1));
            order.setCreatedDate(dateTime.toString(pattern));
        }else {
            order.setCreatedDate(orderDto.getCreatedDate());
        }

        if (orderDto.getUserId() != null){
            User user = userRepository.findByUserId(orderDto.getUserId());
            order.setUser(user);
        }

        Order result = orderRepository.save(order);
        if (result != null){
            if (orderDto.getOrderedProductDtos() != null){
                for (OrderedProductDto orderedProductDto : orderDto.getOrderedProductDtos()){
                    if (orderedProductDto != null){
                        orderedProductDto.setOrderId(result.getOrderId());
                        orderedProductService.save(orderedProductDto);
                    }
                }
            }
        }

        orderRepository.save(result);
        return result;
    }

    public ResponseEntity<OrderDto> save2(OrderDto orderDto) {
        log.debug("Request to save Vente : {}", orderDto);

        Order vente = new Order();

        vente.setOrderId(orderDto.getOrderId());
        vente.setCreatedDate(orderDto.getCreatedDate());

        if(orderDto.getUserId() != null){
            User user = userRepository.findByUserId(orderDto.getUserId());
            vente.setUser(user);
        }

        Order result = orderRepository.save(vente);
        /*
        if(result != null){
            productStockService.deleteStockVente(result);
        }
        */
        return new ResponseEntity<OrderDto>(new OrderDto().createDTO(result), HttpStatus.CREATED);
    }

    public ResponseEntity<OrderDto> update(OrderDto orderDto){
        log.debug("Request to update order{}", orderDto);

        Order order = orderRepository.findByOrderId(orderDto.getOrderId());

        order.setTotalAmount(orderDto.getTotalAmount());
       /* Customer customer = customerRepository.findOne(order.getOrderId());
        if (customer !=null){
            order.setCustomers(customer);
        }*/

       return new ResponseEntity<OrderDto>(new OrderDto().createDTO(order),HttpStatus.CREATED);
    }

    /**
     * get all orders
     * @param page number
     * @param size of each page
     * @param sortBy a variable
     * @param direction asc or desc
     * @param name of the entity
     * @return entities
     */
    public Page<OrderDto> findAll(Integer page, Integer size, String sortBy, String direction, String name){
        log.debug("Request to save a customer {}");

        Pageable pageable = PageRequest.of(page,size, Sort.Direction.fromString(direction),sortBy);
        Page<Order> orders = orderRepository.findAll("%"+name+"%",pageable);

        Page<OrderDto>orderDtos = orders.map(order -> new OrderDto().createDTO(order));

        return orderDtos;
    }

    /**
     * get orders by key word
     * @param keyword to search
     * @return entities
     */
    public List<OrderDto> findByKeyword(String keyword) {
        List<Order> orders = orderRepository.findByKeyword("%"+keyword+"%");
        List<OrderDto> orderDtos = new ArrayList<>();

        for (Order user : orders)
            orderDtos.add(new OrderDto().createDTO(user));

        return orderDtos;
    }


    /**
     * get product by id
     * @param id of the product
     * @return the product
     */
    @Transactional(readOnly = true)
    public OrderDto findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        Order product = orderRepository.findByOrderId(id);

        /*ProductStock productStock = productStockRepository.findFirstByProductIdOrderByDateDesc(product.getId());
        if(productStock != null){
            return new ProductDto().createDTO(product, productStock.getQuantityInStock(), productStock.getCump());
        }*/
        return new OrderDto().createDTO(product);
    }

    /**
     *  Delete order by id.
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete User : {}", id);
        Order user = orderRepository.findByOrderId(id);
        if(Optional.ofNullable(user).isPresent()){
            orderRepository.deleteById(id);
        }
    }
}
