package mate.academy.spring_boot_security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.spring_boot_security.dto.cartItem.CartItemRequestDto;
import mate.academy.spring_boot_security.dto.shoppingCart.ShoppingCartDto;
import mate.academy.spring_boot_security.exception.EntityNotFoundException;
import mate.academy.spring_boot_security.model.User;
import mate.academy.spring_boot_security.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ShoppingCart", description = "Endpoints for managing cart")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add book to shopping cart")
    public ShoppingCartDto addBook(Authentication authentication,
                                   @RequestBody CartItemRequestDto cartDto) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new EntityNotFoundException("User not authenticated");
        }
        return shoppingCartService.addBook(user.getId(), cartDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user shopping cart",
            description = "Retrieve current user's shopping cart")
    public ShoppingCartDto getShoppingCartById(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new EntityNotFoundException("User not authenticated");
        }
        return shoppingCartService.getCart(user.getId());
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update book quantity in shopping cart",
            description = "Update quantity of a book in the shopping cart")
    public ShoppingCartDto updateBookQuantity(@PathVariable Long cartItemId,
                                      @RequestBody CartItemRequestDto cartDto) {
        return shoppingCartService.updateBook(cartItemId, cartDto);
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove book from shopping cart",
                description = "Remove a book from the shopping cart")
    public void deleteCartItem(Long userId, @PathVariable Long cartItemId) {
        shoppingCartService.deleteCartItemId(userId, cartItemId);
    }
}
