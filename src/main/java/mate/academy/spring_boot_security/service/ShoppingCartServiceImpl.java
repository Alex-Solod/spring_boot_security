package mate.academy.spring_boot_security.service;

import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.cartItem.CartItemRequestDto;
import mate.academy.spring_boot_security.dto.shoppingCart.ShoppingCartDto;
import mate.academy.spring_boot_security.exception.EntityNotFoundException;
import mate.academy.spring_boot_security.mapper.CartItemMapper;
import mate.academy.spring_boot_security.mapper.ShoppingCartMapper;
import mate.academy.spring_boot_security.model.CartItem;
import mate.academy.spring_boot_security.model.ShoppingCart;
import mate.academy.spring_boot_security.repository.CartItemRepository;
import mate.academy.spring_boot_security.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getCart(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .map(shoppingCartMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart for user with id: "
                                + userId + " was not found"));
    }

    @Override
    public ShoppingCartDto addBook(Long userId, CartItemRequestDto cartDto) {
        ShoppingCart cart = getCartByUserId(userId);

        CartItem cartItem = cartItemMapper.toEntity(cartDto);
        cartItem.setShoppingCart(cart);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto updateBook(Long cartItemId, CartItemRequestDto cartDto) {
        CartItem cartItem = getCartItemById(cartItemId);
        cartItemMapper.updateCartFromDto(cartDto, cartItem);
        cartItemRepository.save(cartItem);

        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Override
    public void deleteCartItemId(Long userId, Long cartItemId) {
        CartItem cartItem = getCartItemById(cartItemId);

        Long ownerId = cartItem.getShoppingCart()
                .getUser()
                .getId();
        if (!ownerId.equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        cartItemRepository.delete(cartItem);

    }

    private CartItem getCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item with id " + cartItemId + " was not found"));
    }

    private ShoppingCart getCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Cart not found " + userId));
    }
}
