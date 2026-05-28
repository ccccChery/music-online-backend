package com.sqa.musiconline.controller;

import com.sqa.musiconline.common.ApiResponse;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.CartItemCreateDTO;
import com.sqa.musiconline.dto.CartItemUpdateDTO;
import com.sqa.musiconline.service.CartService;
import com.sqa.musiconline.vo.CartViewVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final RequestUserContext requestUserContext;
    private final CartService cartService;

    public CartController(RequestUserContext requestUserContext, CartService cartService) {
        this.requestUserContext = requestUserContext;
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartViewVO> getCart(@RequestHeader("X-User-Id") String userIdHeader) {
        return ApiResponse.ok(cartService.getCart(requestUserContext.requireUser(userIdHeader)));
    }

    @PostMapping("/items")
    public ApiResponse<Void> addItem(@RequestHeader("X-User-Id") String userIdHeader,
                                     @RequestHeader(value = "Origin", required = false) String originHeader,
                                     @Valid @RequestBody CartItemCreateDTO request) {
        log.info("Cart add request received. origin={}, userIdHeader={}, vinylId={}, quantity={}",
                originHeader, userIdHeader, request.getVinylId(), request.getQuantity());
        cartService.addItem(requestUserContext.requireUser(userIdHeader), request);
        log.info("Cart add request completed. userIdHeader={}, vinylId={}, quantity={}",
                userIdHeader, request.getVinylId(), request.getQuantity());
        return ApiResponse.ok();
    }

    @PutMapping("/items/{id}")
    public ApiResponse<Void> updateItem(@RequestHeader("X-User-Id") String userIdHeader,
                                        @PathVariable("id") Long id,
                                        @Valid @RequestBody CartItemUpdateDTO request) {
        cartService.updateItemQuantity(requestUserContext.requireUser(userIdHeader), id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> removeItem(@RequestHeader("X-User-Id") String userIdHeader,
                                        @PathVariable("id") Long id) {
        cartService.removeItem(requestUserContext.requireUser(userIdHeader), id);
        return ApiResponse.ok();
    }
}
