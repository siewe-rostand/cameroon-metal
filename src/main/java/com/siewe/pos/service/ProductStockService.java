package com.siewe.pos.service;

import com.siewe.pos.repository.ProductRepository;
import com.siewe.pos.repository.ProductStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductStockService {

    private final Logger log = LoggerFactory.getLogger(ProductStockService.class);

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private ProductRepository productRepository;


}
