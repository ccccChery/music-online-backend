package com.sqa.musiconline.service;

import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.CartItemCreateDTO;
import com.sqa.musiconline.dto.CartItemUpdateDTO;
import com.sqa.musiconline.vo.CartViewVO;

public interface CartService {

    CartViewVO getCart(RequestUserContext.CurrentUser currentUser);

    void addItem(RequestUserContext.CurrentUser currentUser, CartItemCreateDTO request);

    void updateItemQuantity(RequestUserContext.CurrentUser currentUser, Long cartItemId, CartItemUpdateDTO request);

    void removeItem(RequestUserContext.CurrentUser currentUser, Long cartItemId);
}
