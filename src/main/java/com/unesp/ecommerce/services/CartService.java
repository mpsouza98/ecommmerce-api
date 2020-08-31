package com.unesp.ecommerce.services;

import com.unesp.ecommerce.model.Cart;
import com.unesp.ecommerce.model.Product;
import com.unesp.ecommerce.model.User;
import com.unesp.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    public Cart getCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Error: Cart is not found."));

        return cart;
    }

    public String addProductOnCart(String productId, String cartId, String authorization) {
        Cart newCart = null;
        List<Product> cartProductList = new ArrayList<Product>();

        Optional<Cart> cart = cartRepository.findById(cartId);

        User user = userService.getUserByAuthorization(authorization);

        Product product = productService.getProductById(productId)
            .orElseThrow(() -> new RuntimeException("Error: User not found"));

        if (!cart.isPresent()) {
            cartProductList.add(product);
            newCart = new Cart(user.getId(), product.getPrice(), cartProductList);
            
            cartRepository.save(newCart);
            
            return newCart.getId();
        }

        if (isProductAlreadyOnCart(cart.get(), product.getId())) {
            incrementProductOrderQuantityOnCart(cart.get(), productId);
        } else {
            appendProductOnCart(cart.get(), product);
        }

        return cart.get().getId();
    }

    public boolean isProductAlreadyOnCart(Cart cart, String productId) {
        boolean condition = false;
        String cartProductId;
        List<Product> cartProductsList = cart.getProductList();

        for (Product cartProduct : cartProductsList) {
            cartProductId = cartProduct.getId();

            if (cartProductId.equals(productId)) {
                condition = true;
            }
        }

        return condition;
    }

    public void incrementProductOrderQuantityOnCart(Cart cart, String productId) {
        long orderQuantity = 0;
        float cartFinalPrice = cart.getFinalPrice();
        String cartProductId = null;
        List<Product> cartProductsList = cart.getProductList();

        for (Product cartProduct : cartProductsList) {
            cartProductId = cartProduct.getId();

            if (cartProductId.equals(productId)) {
                orderQuantity = cartProduct.getOrderQuantity();

                cartProduct.setOrderQuantity(orderQuantity + 1);
                cart.setProductList(cartProductsList);
                cart.setFinalPrice(cartFinalPrice + cartProduct.getPrice());

                cartRepository.save(cart);
            }
        }
    }

    public void appendProductOnCart(Cart cart, Product product) {
        List<Product> cartProductsList = cart.getProductList();
        float cartFinalPrice = cart.getFinalPrice();

        cartProductsList.add(product);
        cart.setProductList(cartProductsList);
        cart.setFinalPrice(cartFinalPrice + product.getPrice());
        
        cartRepository.save(cart);
    }
}
