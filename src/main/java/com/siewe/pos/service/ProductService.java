package com.siewe.pos.service;

import com.siewe.pos.dto.ProductDto;
import com.siewe.pos.exception.MyFileNotFoundException;
import com.siewe.pos.model.Category;
import com.siewe.pos.model.Product;
import com.siewe.pos.repository.CategoryRepository;
import com.siewe.pos.repository.ProductRepository;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
public class ProductService {
    private final Logger logger =LoggerFactory.getLogger(ProductService.class);


    private final ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HttpServletRequest request;

    @Value("${upload.path}")
    private String uploadPath;
    private final Path rootLocation;

    public ProductService(@Value("${upload.path}") Path rootLocation,ProductRepository productRepository) {
        this.rootLocation = rootLocation;
        this.productRepository=productRepository;
    }

    public ProductDto save(ProductDto productDto){
        logger.debug("Request to save a new product{}",productDto);

        Product product =new Product();
        if (productDto.getProductId() != null)
            product = productRepository.findByProductId(productDto.getProductId());

        product.setName(productDto.getName());
        product.setUnitPrice(productDto.getUnitPrice());
        product.setDescription(productDto.getDescription());
        if (product.getStockQty() == 0) {
            product.setAvailable(false);
        }else{
            product.setAvailable(true);
        }
        product.setStockQty(productDto.getStockQty());

        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate(DateTimeZone.forOffsetHours(1));
        product.setCreatedDate(date.toString(pattern));
        Product result = productRepository.save(product);

        return new ProductDto().createDTO(result);
    }
    @PostConstruct
    public void ensureDirectoryExists() throws IOException {
        if (!Files.exists(this.rootLocation)) {
            Files.createDirectories(this.rootLocation);
        }
    }

    public ResponseEntity<ProductDto> update(ProductDto productDto){
        logger.debug("Request to update a particular product{}",productDto);

        Product product = productRepository.findByProductId(productDto.getProductId());

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setUnitPrice(productDto.getUnitPrice());
        if (product.getStockQty() == 0) {
            product.setAvailable(false);
        }else{
            product.setAvailable(true);
        }
        product.setStockQty(productDto.getStockQty());

        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate(DateTimeZone.forOffsetHours(1));
        product.setCreatedDate(date.toString(pattern));

        Product result = productRepository.saveAndFlush(product);

        return new ResponseEntity<ProductDto>(new ProductDto().createDTO(result),HttpStatus.CREATED);
    }
/*
    public ResponseEntity<ProductDto> save(ProductDto productDto, MultipartFile file, MultipartFile thumb) throws IOException{
        logger.debug("Request to save a new product{}",productDto);

        Product product =new Product();
        if (productDto.getProductId() != null)
            product = productRepository.findOne(productDto.getProductId());

        product.setName(productDto.getName());
        product.setUnitPrice(productDto.getUnitPrice());
        product.setDescription(productDto.getDescription());
        product.setStockQty(productDto.getStockQty());
        product.setAvailable(true);
        ///set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate(DateTimeZone.forOffsetHours(1));
        product.setCreatedDate(date.toString(pattern));

        Product saveProduct = productRepository.save(product);
        if (file != null && thumb != null){
            if (saveProduct !=null){

                // Normalize file name
                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
                String fileName = "";
                rootLocation = Paths.get(uploadPath);
                if(originalFileName.contains("..")) {
                    throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
                }
                String fileExtension = "";
                try {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                } catch(Exception e) {
                    fileExtension = "";
                }

                fileName = saveProduct.getProductId() + "_" + saveProduct.getName() + fileExtension;
                product.setImageUrl(fileName);
              if (!Files.exists(Paths.get(uploadPath + "products/"))) {
                    File products = new File(uploadPath + "products/");
                    if(! products.mkdirs()) {
                        return new ResponseEntity(new RuntimeException("Unable to create folder ${upload.path}"), HttpStatus.CONFLICT);
                    }
                }


                if(!file.isEmpty()){
                    try {
                        file.transferTo(new File(uploadPath + "products/" + saveProduct.getProductId()));
                        thumb.transferTo(new File(uploadPath + "products/" + saveProduct.getProductId() + "_small"));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        return new ResponseEntity(new RuntimeException("Error while saving product image"), HttpStatus.NO_CONTENT);
                    }
                }
            }
        }

        return new ResponseEntity<>(new ProductDto().createDTO(saveProduct),HttpStatus.CREATED);
    }*/

    public ResponseEntity<ProductDto> create(ProductDto productDto, MultipartFile file)throws IOException {
        Product product = new Product();

        if(productDto.getProductId() != null){
            product = productRepository.findByProductId(productDto.getProductId());
        }
//        claim.setClaim_image(file.getOriginalFilename());
        product.setName(productDto.getName());
        product.setStockQty(productDto.getStockQty());
        product.setUnitPrice(productDto.getUnitPrice());
        product.setDescription(productDto.getDescription());
        product.setAvailable(true);

        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate(DateTimeZone.forOffsetHours(1));
        product.setCreatedDate(date.toString(pattern));

        Product saveProduct = productRepository.save(product);

        if (file !=null ){
            if (saveProduct !=null){
//                rootLocation = Paths.get(uploadPath+"/");
                String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                String fileName = "";
                if(originalFileName.contains("..")) {
                    throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
                }
                String fileExtension = "";
                try {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                } catch(Exception e) {
                    fileExtension = "";
                }
                StringBuffer  fullFilePath = new StringBuffer(uploadPath).append(File.separator).append(fileName);
                fileName = saveProduct.getProductId() + fileExtension;
                product.setImageUrl(String.valueOf(fullFilePath));
                final Path targetPath = this.rootLocation.resolve(fileName);
                System.out.println(fullFilePath);

                try (InputStream in = file.getInputStream()) {
                    try (OutputStream out = Files.newOutputStream(targetPath, StandardOpenOption.CREATE)) {
                        in.transferTo(out);
                    }
                }
            }
        }
        return new ResponseEntity<ProductDto>(new ProductDto().createDTO(saveProduct),HttpStatus.CREATED);
    }

    public ResponseEntity<ProductDto> update(ProductDto productDto, MultipartFile file) throws IOException{
        logger.debug("Request to update a new product{}",productDto);

        Product product =new Product();
        if (productDto.getProductId() != null)
            product = productRepository.findByProductId(productDto.getProductId());

        product.setName(productDto.getName());
        product.setUnitPrice(productDto.getUnitPrice());
        product.setDescription(productDto.getDescription());
        product.setAvailable(true);
        product.setStockQty(productDto.getStockQty());
        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate(DateTimeZone.forOffsetHours(1));
        product.setCreatedDate(date.toString(pattern));

        Product result = productRepository.save(product);
        if (file != null){
            if (result !=null){
//                rootLocation = Paths.get(uploadPath);
                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
                String fileName = "";
//                rootLocation = Paths.get(uploadPath);
                if(originalFileName.contains("..")) {
                    throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
                }
                String fileExtension = "";
                try {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                } catch(Exception e) {
                    fileExtension = "";
                }
                StringBuffer  fullFilePath = new StringBuffer(uploadPath).append(File.separator).append(fileName);
                fileName = result.getProductId() + fileExtension;
                product.setImageUrl(String.valueOf(fullFilePath));

                if (!Files.exists(rootLocation)){
                    File newRoot = new File(uploadPath);
                    if (!newRoot.mkdirs()){
                        return new ResponseEntity(new RuntimeException("unable to create "+uploadPath),HttpStatus.CONFLICT);
                    }
                }
                if (!file.isEmpty()){
                    try {
                        logger.debug("file name : {}" ,rootLocation);
                        System.out.print(rootLocation+"new location");
                        Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch (IOException e){
                        e.printStackTrace();
                        return new ResponseEntity(new RuntimeException("error while saving images"),HttpStatus.NO_CONTENT);
                    }

                }
            }
        }

        return new ResponseEntity<>(new ProductDto().createDTO(result),HttpStatus.CREATED);
    }

    /**
     *  Get all the products by category id.
     *
     *  @return the list of entities
     */

    public Page<ProductDto> findAll(Integer page, Integer size, String sortBy,
                                    String direction, String name,boolean available){
        logger.debug("Request to get all Products by Store");

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Product> products;
//        if (available){
//            productRepository.findByAvailableTrue("%"+name+"%", pageable);
//        }else {
//            products =productRepository.findAll("%"+name+"%", pageable);
//        }
        products =productRepository.findAll( pageable);

        Page<ProductDto> productDtos = products.map(product -> new ProductDto().createDTO(product));
//        Page<ProductDto> productDtos = products.map(product -> {
//            return new ProductDto().createDTO(product);
//        });
        return productDtos;
    }

    public Page<ProductDto> findByAvailableTrue(Integer page, Integer size, String sortBy,
                                                String direction){
        logger.debug("Request to get all Products availble");

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Product> products = null;
        products =productRepository.findByAvailableTrue(pageable);

        Page<ProductDto>productDtos = products.map(product -> {
            return new ProductDto().createDTO(product);
        });
        return productDtos;
    }

    //@Secured(value = {"ROLE_ADMIN"})
    /*public Page<ProductDto> findAll(Integer page, Integer size, String sortBy,
                                    String direction, String name, boolean stockBas){
        logger.debug("Request to get all Products by Store");

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<Product> products = null;

        Page<ProductDto> productDtos = products.map(product -> {
            return new ProductDto().createDTO(product);
        });
        return productDtos;
    }*/

    /**
     * find product according to a key word
     * @param keyword to search
     * @return list of products
     */
    public List<ProductDto> findByKeyword(String keyword) {

        List<Product> products = productRepository.findByKeyword("%"+keyword+"%");
        List<ProductDto> productDtos = new ArrayList<>();

        for (Product product : products)
            productDtos.add(new ProductDto().createDTO(product));

        return productDtos;
    }


    /**
     * get product by id
     * @param id of the product
     * @return the product
     */
    @Transactional(readOnly = true)
    public ProductDto findOne(Long id) {
        logger.debug("Request to get Product : {}", id);
        Product product = productRepository.findByProductId(id);

        /*ProductStock productStock = productStockRepository.findFirstByProductIdOrderByDateDesc(product.getId());
        if(productStock != null){
            return new ProductDto().createDTO(product, productStock.getStock(), productStock.getCump());
        }*/
        return new ProductDto().createDTO(product);
    }

    /**
     *  Delete the  product by id.
     *
     *  @param id the id of the entity
     */
    //@Secured(value = {"ROLE_ADMIN", "ROLE_USER"})
    public void delete(Long id){
        logger.debug("Request to delete a customer by id{}",id);
        Product product = productRepository.findByProductId(id);
        if (Optional.ofNullable(product).isPresent()){
            productRepository.deleteById(id);
        }
    }

    public void deleteAll() {
        List<Product> products = productRepository.findAll();
        for (Product product : products){

                productRepository.deleteById(product.getProductId());

        }
    }

    /**
     * get a particular product image
     * @param productId of the product
     * @return the image
     * @throws IOException if error
     */
    public byte[] getProductImage(Long productId) throws IOException {
        File f = new File(uploadPath  + productId);
        if(f.exists() && !f.isDirectory()) {
            return IOUtils.toByteArray(new FileInputStream(f));
        }
        f = new File(uploadPath + "/" + "no_image.png");
        if(f.exists() && !f.isDirectory()) {
            return IOUtils.toByteArray(new FileInputStream(f));
        }
        return null;
    }

    public byte[] findThumbById(Long productId) throws IOException {
        File f = new File(uploadPath + "products/" + productId + "_small");
        if(f.exists() && !f.isDirectory()) {
            return IOUtils.toByteArray(new FileInputStream(f));
        }
        f = new File(uploadPath + "products/" + "no_image.png");
        if(f.exists() && !f.isDirectory()) {
            return IOUtils.toByteArray(new FileInputStream(f));
        }
        return null;
    }
    public Resource loadFileAsResource(String fileName) {
        try {
//            rootLocation = Paths.get(uploadPath+"/");
            Path filePath = this.rootLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                filePath = this.rootLocation.resolve("no_image.png").normalize();
                resource = new UrlResource(filePath.toUri());
//                System.out.println("File not found " + fileName);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
    public ResponseEntity<Resource> downloadImage(String fileName) {
        final Path targetPath = this.rootLocation.resolve(fileName);
        if (!Files.exists(targetPath)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PathResource(targetPath));
    }
    public Resource load(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            System.out.println(file);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }


    public Product findByName(String name) {
        return productRepository.findByName(name);
    }
    public Page<ProductDto> findAllByCategory(Integer page, Integer size, String sortBy, String direction, String name, Long categoryId, boolean stockBas) {
        logger.debug("Request to get all Products by Store");

        Category cat = categoryRepository.findByCategoryId(categoryId);
        if(cat != null){
            Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
            Page<Product> products = productRepository.findAllByCategoryId("%"+name+"%", cat.getCategoryId(), pageable);;

            Page<ProductDto> productDtos = products.map(product -> {
                return new ProductDto().createDTO(product);
            });
            return productDtos;

        }
        return null;
    }
}
