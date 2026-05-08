package mate.academy.spring_boot_security.service;

import mate.academy.spring_boot_security.dto.cartItem.CartItemRequestDto;
import mate.academy.spring_boot_security.dto.shoppingCart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getCart(Long userId);

    ShoppingCartDto addBook(Long userId, CartItemRequestDto cartDto);

    ShoppingCartDto updateBook(Long cartItemId, CartItemRequestDto cartDto);

    void deleteCartItem(Long cartItemId);
}
