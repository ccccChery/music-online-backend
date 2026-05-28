package com.sqa.musiconline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqa.musiconline.common.exception.CartStateException;
import com.sqa.musiconline.config.RequestUserContext;
import com.sqa.musiconline.dto.CartItemCreateDTO;
import com.sqa.musiconline.dto.CartItemUpdateDTO;
import com.sqa.musiconline.entity.Cart;
import com.sqa.musiconline.entity.CartItem;
import com.sqa.musiconline.entity.User;
import com.sqa.musiconline.entity.Vinyl;
import com.sqa.musiconline.mapper.CartItemMapper;
import com.sqa.musiconline.mapper.CartMapper;
import com.sqa.musiconline.mapper.UserMapper;
import com.sqa.musiconline.mapper.VinylMapper;
import com.sqa.musiconline.service.CartService;
import com.sqa.musiconline.vo.CartItemVO;
import com.sqa.musiconline.vo.CartViewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final VinylMapper vinylMapper;
    private final UserMapper userMapper;

    public CartServiceImpl(CartMapper cartMapper, CartItemMapper cartItemMapper, VinylMapper vinylMapper, UserMapper userMapper) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.vinylMapper = vinylMapper;
        this.userMapper = userMapper;
    }

    @Override
    public CartViewVO getCart(RequestUserContext.CurrentUser currentUser) {
        Cart cart = getOrCreateCart(currentUser.user().getId());
        return buildCartView(cart);
    }

    @Override
    @Transactional
    public void addItem(RequestUserContext.CurrentUser currentUser, CartItemCreateDTO request) {
        try {
            Cart cart = getOrCreateCart(currentUser.user().getId());
            Vinyl vinyl = requirePurchasableVinyl(request.getVinylId(), currentUser.user().getId());
            CartItem existingItem = findCartItemByCartAndVinyl(cart.getId(), vinyl.getId());
            log.info("Cart add state resolved. userId={}, cartId={}, vinylId={}, existingItemId={}, existingQuantity={}, stockQuantity={}",
                    currentUser.user().getId(),
                    cart.getId(),
                    vinyl.getId(),
                    existingItem != null ? existingItem.getId() : null,
                    existingItem != null ? existingItem.getQuantity() : null,
                    vinyl.getStockQuantity());

            int nextQuantity = request.getQuantity();
            if (existingItem != null) {
                nextQuantity += existingItem.getQuantity();
            }
            ensureStock(vinyl, nextQuantity);
            log.info("Cart add quantity validated. userId={}, vinylId={}, requestedQuantity={}, nextQuantity={}",
                    currentUser.user().getId(), vinyl.getId(), request.getQuantity(), nextQuantity);

            if (existingItem == null) {
                CartItem cartItem = new CartItem();
                cartItem.setCartId(cart.getId());
                cartItem.setVinylId(vinyl.getId());
                cartItem.setQuantity(request.getQuantity());
                cartItemMapper.insert(cartItem);
                log.info("Cart item inserted. userId={}, cartId={}, cartItemId={}, vinylId={}, quantity={}",
                        currentUser.user().getId(), cart.getId(), cartItem.getId(), vinyl.getId(), request.getQuantity());
                return;
            }

            existingItem.setQuantity(nextQuantity);
            cartItemMapper.updateById(existingItem);
            log.info("Cart item quantity updated. userId={}, cartId={}, cartItemId={}, vinylId={}, quantity={}",
                    currentUser.user().getId(), cart.getId(), existingItem.getId(), vinyl.getId(), nextQuantity);
        } catch (RuntimeException ex) {
            log.error("Failed to add cart item. userId={}, vinylId={}, quantity={}",
                    currentUser.user().getId(), request.getVinylId(), request.getQuantity(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public void updateItemQuantity(RequestUserContext.CurrentUser currentUser, Long cartItemId, CartItemUpdateDTO request) {
        CartItem cartItem = getOwnedCartItem(currentUser.user().getId(), cartItemId);
        Vinyl vinyl = requirePurchasableVinyl(cartItem.getVinylId(), currentUser.user().getId());
        ensureStock(vinyl, request.getQuantity());
        cartItem.setQuantity(request.getQuantity());
        cartItemMapper.updateById(cartItem);
    }

    @Override
    @Transactional
    public void removeItem(RequestUserContext.CurrentUser currentUser, Long cartItemId) {
        CartItem cartItem = getOwnedCartItem(currentUser.user().getId(), cartItemId);
        cartItemMapper.deleteById(cartItem.getId());
    }

    private CartViewVO buildCartView(Cart cart) {
        List<CartItem> cartItems = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
                .orderByDesc(CartItem::getId));

        if (cartItems.isEmpty()) {
            return new CartViewVO(cart.getId(), 0, BigDecimal.ZERO, List.of());
        }

        Map<Long, Vinyl> vinylMap = cartItems.stream()
                .map(CartItem::getVinylId)
                .distinct()
                .map(vinylMapper::selectById)
                .filter(vinyl -> vinyl != null)
                .collect(Collectors.toMap(Vinyl::getId, Function.identity()));

        Map<Long, User> sellerMap = vinylMap.values().stream()
                .map(Vinyl::getSellerUserId)
                .distinct()
                .map(userMapper::selectById)
                .filter(user -> user != null)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<CartItemVO> itemViews = cartItems.stream()
                .map(item -> toCartItemVO(item, vinylMap.get(item.getVinylId()), sellerMap))
                .toList();

        int itemCount = itemViews.stream().mapToInt(CartItemVO::getQuantity).sum();
        BigDecimal subtotal = itemViews.stream()
                .map(CartItemVO::getLineTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartViewVO(cart.getId(), itemCount, subtotal, itemViews);
    }

    private CartItemVO toCartItemVO(CartItem item, Vinyl vinyl, Map<Long, User> sellerMap) {
        if (vinyl == null) {
            throw new CartStateException("Cart contains a vinyl that no longer exists.");
        }
        User seller = sellerMap.get(vinyl.getSellerUserId());
        BigDecimal lineTotal = vinyl.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemVO(
                item.getId(),
                vinyl.getId(),
                vinyl.getSellerUserId(),
                seller != null ? seller.getDisplayName() : "Unknown retailer",
                vinyl.getTitle(),
                vinyl.getArtistName(),
                vinyl.getFormatType(),
                vinyl.getConditionGrade(),
                vinyl.getPrice(),
                item.getQuantity(),
                vinyl.getStockQuantity(),
                vinyl.getListingStatus(),
                vinyl.getCoverImageUrl(),
                lineTotal
        );
    }

    private Cart getOrCreateCart(Long userId) {
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByAsc(Cart::getId));
        if (carts.size() > 1) {
            log.error("Cart state violation. userId={} has {} carts.", userId, carts.size());
            throw new CartStateException("Multiple carts were found for the current user.");
        }
        if (!carts.isEmpty()) {
            return carts.get(0);
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cartMapper.insert(cart);
        log.info("Cart created for user. userId={}, cartId={}", userId, cart.getId());
        return cart;
    }

    private CartItem getOwnedCartItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> cartItems = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, cartItemId)
                .eq(CartItem::getCartId, cart.getId())
                .orderByAsc(CartItem::getId));
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart item not found.");
        }
        if (cartItems.size() > 1) {
            throw new CartStateException("Duplicate cart items were found for the current cart.");
        }
        return cartItems.get(0);
    }

    private CartItem findCartItemByCartAndVinyl(Long cartId, Long vinylId) {
        List<CartItem> cartItems = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cartId)
                .eq(CartItem::getVinylId, vinylId)
                .orderByAsc(CartItem::getId));
        if (cartItems.isEmpty()) {
            return null;
        }
        if (cartItems.size() > 1) {
            log.error("Duplicate cart entry detected. cartId={}, vinylId={}, duplicateCount={}", cartId, vinylId, cartItems.size());
            throw new CartStateException("Duplicate cart entries were found for the same vinyl.");
        }
        return cartItems.get(0);
    }

    private Vinyl requirePurchasableVinyl(Long vinylId, Long currentUserId) {
        Vinyl vinyl = vinylMapper.selectById(vinylId);
        if (vinyl == null) {
            throw new IllegalArgumentException("Vinyl listing not found.");
        }
        if (currentUserId.equals(vinyl.getSellerUserId())) {
            throw new IllegalArgumentException("You cannot add your own listing to the cart.");
        }
        if (!"ACTIVE".equals(vinyl.getListingStatus()) || vinyl.getStockQuantity() == null || vinyl.getStockQuantity() < 1) {
            throw new IllegalArgumentException("This vinyl listing is not available for purchase.");
        }
        return vinyl;
    }

    private void ensureStock(Vinyl vinyl, Integer quantity) {
        if (vinyl.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Requested quantity exceeds the current stock.");
        }
    }
}
