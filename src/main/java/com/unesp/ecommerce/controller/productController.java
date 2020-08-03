package com.unesp.ecommerce.controller;

import com.unesp.ecommerce.model.Product;
import com.unesp.ecommerce.services.ProductService;
import com.unesp.ecommerce.services.UserHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/api")
public class productController {

    @Autowired
    private ProductService productService;
    private UserHistoryService userHistoryService;

    @PostMapping("/insert-product")
    @PostAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Product insertProduct(@RequestBody Product productToBeInserted) {
        String name = productToBeInserted.getName();
        String category = productToBeInserted.getCategory();
        String price = productToBeInserted.getPrice();

        return productService.saveProduct(name, category, price);
    }

    @GetMapping("/get-product/{id}")
    public Optional<Product> listProduct(@PathVariable String id, @RequestHeader("Authorization") String authorization) {
        userHistoryService.updateUserHistory(id, authorization);

        return productService.getProductById(id);
    }

    @GetMapping("/list-products")
    public List<Product> listAllProducts() {
        return productService.getAllProducts();
    }
}
