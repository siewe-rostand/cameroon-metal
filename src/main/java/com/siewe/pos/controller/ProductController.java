package com.siewe.pos.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.siewe.pos.dto.ProductDto;
import com.siewe.pos.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static javax.servlet.http.MappingMatch.EXTENSION;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ProductController {
    private final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Value("${upload.path}")
    private String uploadPath;
    private static final String EXTERNAL_FILE_PATH = "C:/uploaded/img/";

    @Autowired
    private ProductService productService;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * POST  /products : Create a new product.
     *
     * @param productDto the product to create
     * @return the ResponseEntity with status 201 (Created) and with body the new product, or with status 400 (Bad Request) if the product has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/api/products")
    public ResponseEntity<ProductDto> createProduct( @RequestBody ProductDto productDto) throws URISyntaxException {
        log.debug("REST request to save Product : {}", productDto);
        if (productDto.getProductId() != null) {
            return new ResponseEntity(new RuntimeException("Unable to create. A product with id " +
                    productDto.getProductId() + " already exist."), HttpStatus.CONFLICT);
        }
        ProductDto result = productService.save(productDto);
        return new ResponseEntity<ProductDto>(result, HttpStatus.CREATED);

    }

    /**
     * update a product
     * @param productDto dto of the product
     * @return updated entity
     * @throws URISyntaxException if error
     */
    @PutMapping("/api/products")
    public ResponseEntity<ProductDto> updateProduct( @RequestBody ProductDto productDto) throws URISyntaxException {
        log.debug("REST request to update Product : {}", productDto);
        if (productDto.getProductId() == null) {
            return createProduct(productDto);
        }
        return productService.update(productDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/api/save", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<ProductDto> createProductWithImage(@RequestParam("product") String product ,
                                                             @RequestParam(name="file", required=false) MultipartFile file) throws IOException {

        log.debug("REST request to save Product : {}", product);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        ProductDto productDto = objectMapper.readValue(product,ProductDto.class);
        if (productDto.getProductId() != null) {
            return new ResponseEntity(new RuntimeException("Unable to create. A product with id " +
                    productDto.getProductId() + " already exist."), HttpStatus.CONFLICT);
        }

        return productService.create(productDto, file);
    }

    /**
     * PUT  /products : Updates an existing product.
     *
     * @param product the product to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated product,
     * or with status 400 (Bad Request) if the product is not valid,
     * or with status 500 (Internal Server Error) if the product couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/api/products-with-image/", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ProductDto> updateProduct(@RequestParam("product") String product,
                                                    @RequestParam(name="file", required=false) MultipartFile file) throws URISyntaxException, IOException {
        log.debug("REST request to update Product : {}", product);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        ProductDto productDto = objectMapper.readValue(product,ProductDto.class);
        if(productDto.getProductId() != null)
            return productService.update(productDto, file);
        else
            return productService.create(productDto, file);
    }

    /**
     * GET  /products : get all the products.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of products in body
     */

    @GetMapping("/api/products")
    public Page<ProductDto> getAllProducts(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "9999") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "product", defaultValue = "") String productName,
            @RequestParam(name = "available", required = false) boolean available) {
        log.debug("REST request to get page of Products");
        return productService.findAll(page, size, sortBy, direction, productName,available);
    }


    @GetMapping("/api/products-available")
    public Page<ProductDto> getAllProductsAvailable(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "9999") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        log.debug("REST request to get page of available Products");
        return productService.findByAvailableTrue(page,size,sortBy,direction);
    }
   /* @GetMapping("/api/products")
    public Page<ProductDto> getAllProducts(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "product", defaultValue = "") String product,
            @RequestParam(name = "stockBas") boolean stockBas) {
        log.debug("REST request to get page of Products by store");
        return productService.findAll(page, size, sortBy, direction, product, stockBas);
    }*/

    @GetMapping("/api/products-by-category/{categoryId}")
    public Page<ProductDto> getAllProductsByCategory(@PathVariable Long categoryId,
                                                     @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                     @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
                                                     @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                     @RequestParam(name = "product", defaultValue = "") String product,
                                                     @RequestParam(name = "stockBas") boolean stockBas) {
        log.debug("REST request to get page of Products by store");
        return productService.findAllByCategory(page, size, sortBy, direction, product, categoryId, stockBas);
    }
    @GetMapping("/api/products-search")
    public Map<String, List<ProductDto>> getProductsByKeyword(@RequestParam(name = "keyword") String keyword) {
        log.debug("REST request to get Products");
        Map<String, List<ProductDto>> map = new HashMap<>();
        map.put("results", productService.findByKeyword(keyword));
        return map;
    }

    /**
     * GET  /products/:id : get the "id" product.
     *
     * @param id the id of the product to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the product, or with status 404 (Not Found)
     */
    @GetMapping("/api/products/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        ProductDto productDto = productService.findOne(id);

        return Optional.ofNullable(productDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    /**
     * get product image
     * @return image of product
     * @throws IOException if error
     */
    @RequestMapping(value="/api/product-image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> downloadImage(String fileName)  {
        return productService.downloadImage(fileName);
    }
    @GetMapping(value = "/downloadFile/{fileName:.+}",produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws  IOException {
        // Load file as Resource
        File file = new File(uploadPath + File.separator + fileName );

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=img.jpg");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));


        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }



    /**
     * DELETE  /products/:id : delete the "id" product.
     *
     * @param id the id of the product to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        productService.delete(id);
        return new ResponseEntity<ProductDto>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/api/products-delete-all")
    public ResponseEntity<?> deleteAllProduct() {
        log.debug("REST request to delete Product : {}");
        productService.deleteAll();
        return new ResponseEntity<ProductDto>(HttpStatus.NO_CONTENT);
    }

}
