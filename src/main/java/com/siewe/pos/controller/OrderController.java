package com.siewe.pos.controller;

import com.siewe.pos.InvalidOrderItemException;
import com.siewe.pos.dto.OrderDto;
import com.siewe.pos.model.Order;
import com.siewe.pos.model.Purchase;
import com.siewe.pos.model.PurchaseResponse;
import com.siewe.pos.service.CheckoutService;
import com.siewe.pos.service.OrderedProductService;
import com.siewe.pos.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Optional;

@CrossOrigin
@RestController
public class OrderController {
    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;


    private CheckoutService checkOutService;

    @Autowired
    private OrderedProductService orderedProductService;

    @PostMapping("/purchase")
    public PurchaseResponse placeOrder(@RequestBody Purchase purchase) {

        PurchaseResponse purchaseResponse = checkOutService.placeOrder(purchase);

        return purchaseResponse;
    }
    /**
     * POST  /orders : Create a new order.
     *
     * @param orderDto the order to create
     * @return the ResponseEntity with status 201 (Created) and with body the new product, or with status 400 (Bad Request) if the order has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/api/orders")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) throws URISyntaxException {
        log.debug("REST request to save new order : {}", orderDto);
        if (orderDto.getOrderId() != null) {
            return new ResponseEntity(new RuntimeException("Unable to create. A product with id " +
                    orderDto.getOrderId() + " already exist."), HttpStatus.CONFLICT);
        }
        Order result = null;
        try {
            result = orderService.save(orderDto);
        } catch (InvalidOrderItemException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<OrderDto>(new OrderDto().createDTO(result), HttpStatus.CREATED);

    }

    @PutMapping("/api/orders")
    public ResponseEntity<OrderDto> updateOrder(@RequestBody OrderDto orderDto) throws URISyntaxException {
        log.debug("REST request to update order : {}", orderDto);
        if (orderDto.getOrderId() == null) {
            return createOrder(orderDto);
        }
        return orderService.update(orderDto);
    }

    /**
     * GET  /orders/:id : get the "id" order.
     *
     * @param id the id of the order to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the product, or with status 404 (Not Found)
     */
    @GetMapping("/api/orders/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        log.debug("REST request to get order : {}", id);
        OrderDto orderDto = orderService.findOne(id);

        return Optional.ofNullable(orderDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    /**
     * DELETE  /orders/:id : delete the "id" order.
     *
     * @param id the id of the order to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/api/order/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete order : {}", id);
        orderService.delete(id);
        return new ResponseEntity<OrderDto>(HttpStatus.NO_CONTENT);
    }
}
