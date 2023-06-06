package com.javatechie.service;

import com.javatechie.entity.Product;
import com.javatechie.repository.ProductRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository repository;

   @Observed(name = "create product method")
    public Product addProduct(Product product) {
        return repository.save(product);

    }
    @Observed(name = "get product")
    public Product getProduct(int id) {
        log.info("ProductService : getProduct interact with DB");
        return repository.findById(id).get();
    }
    @Observed(name = "get product method")
    public List<Product> getProducts() {
        log.info("ProductService : getProducts interact with DB");
        return repository.findAll();
    }

    public Product updateProduct(int id, Product productRequest) {
        // get the product from DB by id
        // update with new value getting from request
        Product existingProduct = repository.findById(id).get(); // DB
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setProductType(productRequest.getProductType());
        return repository.save(existingProduct);
    }

    public String deleteProduct(int id) {
        repository.deleteById(id);
        return "product deleted";
    }

    public List<Product> getProductsByType(Product product) {
        log.info("ProductService : getProductsByType interact with DB");
        return repository.findByProductType(product.getProductType());
    }

    public Product updateProductsByField(int id, Map<String, Object> fields) {
        Optional<Product> existingProduct = repository.findById(id);
        if (existingProduct.isPresent()) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Product.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, existingProduct.get(), value);
            });
            return repository.save(existingProduct.get());
        }
        return null;
    }


}
