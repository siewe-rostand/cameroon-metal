package com.siewe.pos.service;

import com.siewe.pos.InvalidOrderItemException;
import com.siewe.pos.dto.OrderedProductDto;
import com.siewe.pos.model.Order;
import com.siewe.pos.model.OrderedProduct;
import com.siewe.pos.model.Product;
import com.siewe.pos.repository.OrderedProductRepository;
import com.siewe.pos.repository.OrderRepository;
import com.siewe.pos.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderedProductService {
    private final Logger log = LoggerFactory.getLogger(OrderedProductService.class);

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;


    /**
     * save new order detail
     * @param orderedProductDto of the order
     * @return the entity
     */
    public OrderedProductDto save(OrderedProductDto orderedProductDto) throws InvalidOrderItemException{
        log.debug("Request to save new order detail{}", orderedProductDto);
        OrderedProduct orderedProduct = orderedProductRepository.findByOrder_OrderIdAndProductProductId(orderedProductDto.getOrderId(),
                orderedProductDto.getProductId());

        if (orderedProduct != null){
            orderedProduct = new OrderedProduct();
        }

        orderedProduct.setId(orderedProductDto.getOrderDetailNum());
        orderedProduct.setQuantity(orderedProductDto.getQuantity());
        orderedProduct.setSubTotal(orderedProductDto.getSubTotal());

        Order order = orderRepository.findByOrderId(orderedProductDto.getOrderId());
        if (orderedProduct.getOrder() != null){
            orderedProduct.setOrder(order);
        }

        Product product = productRepository.findByProductId(orderedProductDto.getProductId());
        if (orderedProduct.getProduct() != null){
            orderedProduct.setProduct(product);
        }

        if (product.getStockQty() - orderedProduct.getQuantity() < 0){
            throw new InvalidOrderItemException("Stock " + orderedProductDto.getName() + "insufficient");
        }


        OrderedProduct result = orderedProductRepository.save(orderedProduct);

        return new OrderedProductDto().createDTO(result);
    }

    /**
     * update ordered product
     * @param orderedProductDto the entity to be update
     * @return the updated entity
     */
    public ResponseEntity<OrderedProductDto> update(OrderedProductDto orderedProductDto){
        log.debug("Request to update order detail{}", orderedProductDto);

        OrderedProduct orderedProduct = orderedProductRepository.findOne(orderedProductDto.getOrderId());
        orderedProduct.setSubTotal(orderedProductDto.getSubTotal());
        orderedProduct.setQuantity(orderedProductDto.getQuantity());
        if (orderedProduct.getOrder() != null){
            Order order = orderRepository.findByOrderId(orderedProductDto.getOrderId());
            orderedProduct.setOrder(order);
        }

        if (orderedProduct.getProduct() != null){
            Product product = productRepository.findByProductId(orderedProductDto.getProductId());
            orderedProduct.setProduct(product);
        }
        OrderedProduct result = orderedProductRepository.save(orderedProduct);

       return new ResponseEntity<>(new OrderedProductDto().createDTO(result),HttpStatus.CREATED);
    }

    /**
     *  Get one orderedProduct by id.
     *
     *  @param id the id of the orderedProduct
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public OrderedProductDto findOne(Long id) {
        log.debug("Request to get OrderedProduct : {}", id);
        OrderedProduct orderedProduct = orderedProductRepository.findOne(id);
        return new OrderedProductDto().createDTO(orderedProduct);
    }

    /**
     * get order products by id
     * @param id of the entity
     * @return the entities
     */
    @Transactional(readOnly = true)
    public List<OrderedProductDto> findAllByOrder(Long id) {
        log.debug("Request to get all OrderedProducts by Vente");

        List<OrderedProductDto> orderedProductDtos = new ArrayList<>();
        List<OrderedProduct> orderedProducts = orderedProductRepository.findByOrder_OrderId(id);

        for (OrderedProduct orderedProduct : orderedProducts)
            orderedProductDtos.add(new OrderedProductDto().createDTO(orderedProduct));

        return orderedProductDtos;
    }

    /**
     *  Delete order by id.
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete User : {}", id);
        OrderedProduct user = orderedProductRepository.findOne(id);
        if(Optional.ofNullable(user).isPresent()){
            orderedProductRepository.deleteById(id);
        }
    }
}
