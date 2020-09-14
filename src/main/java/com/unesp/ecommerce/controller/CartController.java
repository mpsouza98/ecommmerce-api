package com.unesp.ecommerce.controller;

import com.unesp.ecommerce.model.Cart;
import com.unesp.ecommerce.model.Product;
import com.unesp.ecommerce.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/cart/{productId}")
    @PostAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public Product buyProduct(@PathVariable String productId, @RequestHeader(value = "Authorization") String authorization) {
        return cartService.addProductOnCart(productId, authorization);
    }

    @GetMapping("/cart")
    @PostAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public Optional<Cart> listCartByAuthToken(@RequestHeader(value = "Authorization") String authorization) {
        return cartService.getCart(authorization);
    }

    @DeleteMapping("/cart-product/{productId}")
    @PostAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public void deleteProductOnCart(@RequestHeader(value = "Authorization") String authorization, @PathVariable String productId) {
        cartService.deleteProductOnCart(authorization, productId);
    }
}
